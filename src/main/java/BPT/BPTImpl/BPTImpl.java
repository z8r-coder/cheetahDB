package BPT.BPTImpl;

import BPT.BPT;
import BPT.Entry;
import BPT.Node;
import BPT.NodeImpl.InteriorNode;
import BPT.NodeImpl.LeafNode;
import Exception.*;

import java.util.Vector;

/**
 * Created by roy on 2017/7/3
 */
public class BPTImpl<T,E> implements BPT{
    private Node<T,E> root;

    public BPTImpl() {
        root = new LeafNode<T,E>(null);
    }
    //外部注入
    public void setRoot(Node<T, E> root) {
        this.root = root;
    }

    public Node<T, E> getRoot() {
        return root;
    }

    public Entry search(Node node, Object key) throws Exception {
        if (node instanceof LeafNode) {
            Vector<Entry<T,E>> entries = ((LeafNode) node).getEntries();
            int pivot = binarySearch(node, entries,(T)key);
            if (pivot == -1) {
                System.err.println("Entry未找到！");
                return null;
            }
            return entries.get(pivot);
        }else if (node instanceof InteriorNode) {
            Vector<Entry<T,E>> entries = ((InteriorNode) node).getEntries();
            Vector<Node<T,E>> nodes = ((InteriorNode) node).getPage_pointers();
            int pivot = binarySearch(node, entries, (T)key);
            if (entries.get(pivot).getKey().hashCode() == key.hashCode()) {
                //查询到
                return entries.get(pivot);
            }else if (entries.get(pivot).getKey().hashCode() > key.hashCode()) {
                //左边
                Entry<T,E> entry = search(nodes.get(pivot),key);
                return entry;
            }else {
                //右边
                Entry<T,E> entry = search(nodes.get(pivot + 1),key);
                return entry;
            }
        }else {
            throw new NodeClassNotFoundException(getClass().getName());
        }
    }

    public void delete(Object key) throws Exception {

    }

    public void update(Node node, Object key, Entry entry) throws Exception {
        Entry<T,E> nodeEntry = search(node,key);
        nodeEntry.setKey((T) entry.getKey());
        nodeEntry.setValue((E) entry.getValue());
    }

    public void insert(Node node,Entry entry) throws Exception {
        if (node instanceof LeafNode) {
            ((LeafNode) node).insertEntry(entry);
        }else if (node instanceof InteriorNode) {
            Vector<Entry<T,E>> entries = ((InteriorNode) node).getEntries();
            Vector<Node>  nodes = ((InteriorNode) node).getPage_pointers();
            int index = binarySearch(node,entries, (T) entry.getKey());
            if (entries.get(index).getKey().hashCode() == entry.getKey().hashCode()) {
                //不能有相同的键
                throw new KeyConflictException(getClass().getName());
            }else if (entries.get(index).getKey().hashCode() > entry.getKey().hashCode()) {
                //左边 = index
                insert(nodes.get(index),entry);
            }else {
                //右边 = index + 1
                insert(nodes.get(index + 1),entry);
            }
        }else {
            throw new NodeClassNotFoundException(getClass().getName());
        }
    }

    /**
     * 辅助方法，二分查找
     * @param entries
     * @param key
     * @return
     */
    private int binarySearch(Node node, Vector<Entry<T,E>> entries, T key) throws Exception{
        int start = 0;
        int end = entries.size() - 1;
        int mid = (start + end) / 2;
        while (entries.get(mid).getKey().hashCode() != key.hashCode() && end > start) {
            if (entries.get(mid).getKey().hashCode() > key.hashCode()) {
                //右边
                end = mid - 1;
            }else if (entries.get(mid).getKey().hashCode() < key.hashCode()) {
                //左边
                start = mid + 1;
            }
            mid = (start + end) / 2;
        }
        if (node instanceof LeafNode) {
            return (entries.get(mid).getKey().hashCode() != key.hashCode())? -1 : mid;
        }else if (node instanceof InteriorNode) {
            return mid;
        }else {
            throw new NodeClassNotFoundException(getClass().getName());
        }
    }
}
