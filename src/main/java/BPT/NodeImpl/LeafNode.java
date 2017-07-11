package BPT.NodeImpl;

import BPT.*;
import Exception.*;

import java.util.Vector;

/**
 * Created by roy on 2017/7/2.
 */
public class LeafNode<T,E> implements Node {
    private Vector<Entry<T,E>> entries;//条目集合
    private LeafNode<T,E> next_page;//下一页
    private InteriorNode<T,E> parentNode;//父节点

    public LeafNode(LeafNode<T,E> next_page) {
        entries = new Vector<Entry<T, E>>();
        this.next_page = next_page;
    }
    public LeafNode(LeafNode<T,E> next_page, Vector<Entry<T,E>> entries) {
        this.next_page = next_page;
        this.entries = entries;
    }
    public void insertEntry(Entry entry) throws Exception {
        int pivot = binarySearch(entries, (T) entry.getKey());
        if (entries.get(pivot).getKey().hashCode() > entry.getKey().hashCode()) {
            entries.insertElementAt(entry, pivot);
        }else if (entries.get(pivot).getKey().hashCode() < entry.getKey().hashCode()) {
            entries.insertElementAt(entry, pivot + 1);
        }else {
            throw new KeyConflictException(getClass().getName());
        }
        if (entries.size() >= Node.CAPACITY) {
            //分裂
            int mid = entries.size() / 2;
            //需要上升的结点
            Entry<T,E> flowEntry = entries.get(mid);
            Vector<Entry<T,E>> leftEntries = new Vector<Entry<T, E>>();
            Vector<Entry<T,E>> rightEntries = new Vector<Entry<T, E>>();
            for (int i = 0; i < mid;i++) {
                leftEntries.add(entries.get(i));
            }
            for (int i = mid + 1; i < entries.size(); i++) {
                rightEntries.add(entries.get(i));
            }
            Node<T,E> rightNode = new LeafNode<T, E>(null, rightEntries);
            Node<T,E> leftNode = new LeafNode<T, E>((LeafNode<T, E>) rightNode, leftEntries);
            if (parentNode == null) {
                //父节点为空，创建父节点
                parentNode = new InteriorNode<T,E>();
                parentNode.insertEntry(flowEntry, leftNode, rightNode);
            }else {
                parentNode.insertEntry(flowEntry, leftNode, rightNode);
            }
        }

    }


    public void deleteEntry(Object key) throws Exception {

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
                //左边
                end = mid - 1;
            } else if (entries.get(mid).getKey().hashCode() < key.hashCode()) {
                //右边
                start = mid + 1;
            }
            mid = (start + end) / 2;
        }
        return mid;
    }
    public void setEntries(Vector<Entry<T, E>> entries) {
        this.entries = entries;
    }

    public Vector<Entry<T, E>> getEntries() {
        return entries;
    }

    public void setNext_page(LeafNode next_page) {
        this.next_page = next_page;
    }

    public LeafNode getNext_page() {
        return next_page;
    }

    public void setParentNode(InteriorNode parentNode) {
        this.parentNode = parentNode;
    }
    public InteriorNode<T, E> getParentNode() {
        return parentNode;
    }
}
