package Engine.DiskBplusTree;

import Engine.Bplustree;
import Engine.MemBPlusTree.Node;

import Engine.MemManager;
import Exception.SelectException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    /**
     * 缓存修改过的页
     */
    private transient Map<Long, DiskNode<T>> changeDiskNodeCache = new HashMap<Long, DiskNode<T>>();

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
        DiskNode<T> rootNode = memManager.getPageById(rootId);
        rootNode.remove(key, this, memManager);
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

    public void setChangeDiskNodeCache(Map<Long, DiskNode<T>> changeDiskNodeCache) {
        this.changeDiskNodeCache = changeDiskNodeCache;
    }

    public Map<Long, DiskNode<T>> getChangeDiskNodeCache() {
        return changeDiskNodeCache;
    }

    /**
     * 添加改变后的diskNode
     * @param diskNode
     */
    public void putChangeNode (Long id, DiskNode<T> diskNode) {
        changeDiskNodeCache.put(id, diskNode);
    }

    /**
     * 通过id获取缓存的diskNode
     * @param id
     * @return
     */
    public DiskNode<T> getChangeNode(Long id) {
        if (changeDiskNodeCache.get(id) == null) {
            //若修改节点缓存中没有，去磁盘读
            DiskNode diskNode = memManager.getPageById(id);
            return diskNode;
        }
        //若有缓存，则直接读
        return changeDiskNodeCache.get(id);
    }

    /**
     * 从缓存中移除，防止无用插入和非法访问
     * @param id
     * @return
     */
    public DiskNode<T> removeChangeNode(Long id) {
        return changeDiskNodeCache.remove(id);
    }

    /**
     * 封装内存管理器的空闲页管理
     * @return
     */
    public Long getFreeId() {
        return memManager.getNewOrFreeId();
    }

    /**
     * 增添空闲页
     * @param id
     */
    public void addFreeId(long id) {
        memManager.addFreePage(id);
    }
    /**
     * 遍历叶节点
     * @param root
     */
    public void visitorLeaf(Long root) {

    }

    public static void main(String arg[]) {
        Map<Integer, String> map = new HashMap<Integer, String>();
        map.put(2, "22");
        System.out.println(map.get(2));
        System.out.println(map.remove(2));
        System.out.println(map.get(2));
        System.out.println(map.remove(2));
    }
}
