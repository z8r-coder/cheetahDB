package BPT.NodeImpl;
import BPT.*;

import java.util.Vector;
import Exception.*;

/**
 * Created by Roy on 2017/7/3.
 */
public class InteriorNode<T,E> implements Node {
    Vector<Entry<T,E>> entries;//entry集合
    Vector<Node<T,E>> page_pointers;//指针集合
    InteriorNode<T,E> parentNode;//父节点
    public InteriorNode() {
        entries = new Vector<Entry<T, E>>();
        page_pointers = new Vector<Node<T,E>>();
    }
    public InteriorNode(Vector<Entry<T,E>> entries, Vector<Node<T,E>> page_pointers) {
        this.entries = entries;
        this.page_pointers = page_pointers;
    }

    public void setEntries(Vector<Entry<T, E>> entries) {
        this.entries = entries;
    }

    public Vector<Entry<T, E>> getEntries() {
        return entries;
    }

    public void setPage_pointers(Vector<Node<T,E>> page_pointers) {
        this.page_pointers = page_pointers;
    }

    public Vector<Node<T,E>> getPage_pointers() {
        return page_pointers;
    }

    //可优化
    public void insertEntry(Entry entry,Node leftNode, Node rightNode) throws Exception{
        int pivot = binarySearch(entries, (T) entry.getKey());
        //需要合并page_pointers
        if (entries.get(pivot).getKey().hashCode() > entry.getKey().hashCode()) {
            entries.insertElementAt(entry, pivot);
            Node<T,E> node = page_pointers.get(pivot);
            //上升后左结点调整
            upwardAdjustment(node, leftNode);
            page_pointers.insertElementAt(rightNode, pivot + 1);
        }else if (entries.get(pivot).getKey().hashCode() < entry.getKey().hashCode()) {
            entries.insertElementAt(entry, pivot + 1);
            Node<T,E> node = page_pointers.get(pivot + 1);
            //上升后左结点调整
            upwardAdjustment(node, leftNode);
            page_pointers.insertElementAt(rightNode, pivot + 2);
        }else {
            throw new KeyConflictException(getClass().getName());
        }
        if (entries.size() >= Node.CAPACITY) {
            //满了，分裂，上升
            int entry_mid = entries.size() / 2;
            Entry<T,E> flowEntry = entries.get(entry_mid);
            //分裂存在于两个node中的entres
            Vector<Entry<T,E>> leftEntries = new Vector<Entry<T, E>>();
            Vector<Entry<T,E>> rightEntries = new Vector<Entry<T, E>>();
            for (int i = 0; i < entry_mid;i++) {
                leftEntries.add(entries.get(i));
            }
            for (int i = entry_mid + 1;i < entries.size();i++) {
                rightEntries.add(entries.get(i));
            }
            int pagepointer_mid = page_pointers.size() / 2;
            //分裂存在于两个node中的page_pointers
            Vector<Node<T,E>> leftPage_pointers = new Vector<Node<T, E>>(Node.CAPACITY);
            Vector<Node<T,E>> rightPage_pointers = new Vector<Node<T, E>>(Node.CAPACITY);
            //分奇偶情况
            if (page_pointers.size() % 2 == 0) {
                //偶数
                for (int i = 0; i < pagepointer_mid;i++) {
                    leftPage_pointers.add(page_pointers.get(i));
                }
                for (int i = pagepointer_mid; i < page_pointers.size();i++) {
                    rightPage_pointers.add(page_pointers.get(i));
                }
            }else {
                //奇数
                for (int i = 0; i <= pagepointer_mid;i++) {
                    leftPage_pointers.add(page_pointers.get(i));

                }
                for (int i = pagepointer_mid + 1; i < page_pointers.size();i++) {
                    rightPage_pointers.add(page_pointers.get(i));
                }
            }
            InteriorNode<T,E> flowleftNode = new InteriorNode(leftEntries, leftPage_pointers);
            InteriorNode<T,E> flowrightNode = new InteriorNode(rightEntries,rightPage_pointers);
            for (int i = 0; i < leftPage_pointers.size();i++) {
                leftPage_pointers.get(i).setParentNode(flowleftNode);
            }
            for (int i = 0; i < rightPage_pointers.size();i++) {
                rightPage_pointers.get(i).setParentNode(flowleftNode);
            }
            if (parentNode == null) {
                parentNode = new InteriorNode<T, E>();
                flowNodeSetParentNode(parentNode, flowleftNode, flowrightNode);
                parentNode.insertEntry(flowEntry, flowleftNode,flowrightNode);

            }else {
                flowNodeSetParentNode(parentNode, flowleftNode, flowrightNode);
                parentNode.insertEntry(flowEntry, flowleftNode, flowrightNode);
            }
        }
    }
    public void setParentNode(InteriorNode parentNode) {
        this.parentNode = parentNode;
    }
    public void queryEntry(Object key) throws Exception{
    }

    public void deleteEntry(Object key) throws Exception{

    }

    public void updateEntry(Object key) throws Exception{

    }
    private void flowNodeSetParentNode(InteriorNode parentNode, Node leftNode, Node rightNode) {
        leftNode.setParentNode(parentNode);
        rightNode.setParentNode(parentNode);
    }
    /**
     * 当节点上升时的调整，需要将pivot位置的结点设置成leftNode
     * @param node
     * @param leftNode
     * @throws Exception
     */
    private void upwardAdjustment(Node node, Node leftNode) throws Exception{
        if (node instanceof LeafNode) {
            //说明传进来的也是LeafNode
            ((LeafNode) node).setEntries(((LeafNode)leftNode).getEntries());
            ((LeafNode) node).setNext_page(((LeafNode)leftNode).getNext_page());
        } else if (node instanceof InteriorNode) {
            //传进来的也是InteriorNode
            ((InteriorNode) node).setEntries(((InteriorNode)leftNode).getEntries());
            ((InteriorNode) node).setPage_pointers(((InteriorNode)leftNode).getPage_pointers());
        }else {
            throw new NodeClassNotFoundException(getClass().getName());
        }
    }
    /**
     * 为了保证泛华，此处重新写，后期可优化
     * @param entries
     * @param key
     * @return
     * @throws Exception
     */
    private int binarySearch(Vector<Entry<T,E>> entries, T key) throws Exception {
        int start = 0;
        int end = entries.size() - 1;
        int mid = (start + end) / 2;
        while (entries.get(mid).getKey().hashCode() != key.hashCode() && end > start) {
            if (entries.get(mid).getKey().hashCode() > key.hashCode()) {
                //右边
                end = mid - 1;
            } else if (entries.get(mid).getKey().hashCode() < key.hashCode()) {
                //左边
                start = mid + 1;
            }
            mid = (start + end) / 2;
        }
        return mid;
    }
}
