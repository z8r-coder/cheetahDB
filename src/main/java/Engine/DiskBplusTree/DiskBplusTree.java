package Engine.DiskBplusTree;

import Engine.Bplustree;
import Engine.MemBPlusTree.Node;

import Exception.SelectException;
import java.util.List;

/**
 * 基于磁盘的b+树索引
 * Created by rx on 2017/9/4.
 */
public class DiskBplusTree<T> implements Bplustree<T,Integer>{
    public T search(Comparable key) {
        return null;
    }

    public List<T> searchForList(Comparable key, String rp) throws SelectException {
        return null;
    }

    public void delete(Comparable key) {

    }

    public void insert(Comparable key, T obj) {

    }

    public void update(Comparable key, T obj) {

    }

    public int getOrder() {
        return 0;
    }

    public void setRoot(Integer root) {

    }

    public Node getRoot() {
        return null;
    }

    public void setHead(Integer head) {

    }

    public void visitorLeaf(Integer root) {

    }
}
