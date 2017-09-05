package Engine.DiskBplusTree;

import Engine.Bplustree;
import Engine.MemBPlusTree.Node;

import Exception.SelectException;
import java.util.List;

/**
 * 基于磁盘的b+树索引
 *
 * Created by rx on 2017/9/4.
 */
public class DiskBplusTree<T> implements Bplustree<T,Long>{
    /**
     * 根节点所在位置
     */
    private long rootId;

    /**
     * 阶数
     */
    private int order;

    /**
     * 链表头结点所在位置
     */
    private long headId;
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

    public void setRoot(Long root) {
        this.rootId = root;
    }

    public Long getRoot() {
        return rootId;
    }

    public void setHead(Long head) {
        this.headId = head;
    }

    public void visitorLeaf(Long root) {

    }
}
