package Engine.DiskBplusTree;

import Engine.Bplustree;
import Engine.MemBPlusTree.Node;

import Engine.MemManager;
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
     * 叶节点链表头结点所在位置
     */
    private long headId;
    /**
     * 所属表名
     */
    private String tableName;

    /**
     * 该表的内存管理器
     */
    private transient MemManager<T> memManager;

    public DiskBplusTree(int order, long rootId, String tableName) {
        this.order = order;
        this.rootId = rootId;
        this.tableName = tableName;
        this.memManager = MemManager.getTableMemManager(tableName);
    }
    public T search(Comparable key) {
        return null;
    }

    public List<T> searchForList(Comparable key, String rp) throws SelectException {
        return null;
    }

    public void delete(Comparable key) {

    }

    public void insert(Comparable key, T obj) {
        DiskNode<T> rootNode = memManager.getPageById(rootId);
        rootNode.insert(key, obj, this, memManager);
    }

    public void update(Comparable key, T obj) {

    }

    public int getOrder() {
        return order;
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

    public void setMemManager(MemManager<T> memManager) {
        this.memManager = memManager;
    }

    public MemManager<T> getMemManager() {
        return memManager;
    }

    public void visitorLeaf(Long root) {

    }
}
