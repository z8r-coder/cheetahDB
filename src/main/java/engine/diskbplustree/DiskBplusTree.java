package engine.diskbplustree;

import engine.Bplustree;

import engine.MemManager;
import exception.SelectException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 基于磁盘的b+树索引
 *
 * Created by rx on 2017/9/4.
 */
public class DiskBplusTree<T> implements Bplustree<T,Integer>{
    /**
     * 根节点所在位置
     */
    private int rootId;

    /**
     * 阶数
     */
    private int order;

    /**
     * 叶节点链表头结点所在位置
     */
    private int headId;
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
    private transient Map<Integer, DiskNode<T>> changeDiskNodeCache = new HashMap<Integer, DiskNode<T>>();

    public DiskBplusTree(int order, int rootId, String tableName) {
        this.order = order;
        this.rootId = rootId;
        this.tableName = tableName;
        this.memManager = MemManager.getTableMemManager(tableName);
    }
    public T search(Comparable key) {
        DiskNode<T> rootNode = memManager.getPageById(rootId);
        return rootNode.search(key, this);
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
        PersistInfo persistInfo = new PersistInfo();
        persistInfoInit(persistInfo);
    }

    public void update(Comparable key, T obj) {
        DiskNode<T> rootNode = memManager.getPageById(rootId);
        rootNode.update(key, obj, this);
    }

    public int getOrder() {
        return order;
    }

    public void setRoot(Integer root) {
        this.rootId = root;
    }

    public Integer getRoot() {
        return rootId;
    }

    public void setHead(Integer head) {
        this.headId = head;
    }

    public Integer getHeadId() {
        return headId;
    }

    public void setMemManager(MemManager<T> memManager) {
        this.memManager = memManager;
    }

    public MemManager<T> getMemManager() {
        return memManager;
    }

    public void setChangeDiskNodeCache(Map<Integer, DiskNode<T>> changeDiskNodeCache) {
        this.changeDiskNodeCache = changeDiskNodeCache;
    }

    public Map<Integer, DiskNode<T>> getChangeDiskNodeCache() {
        return changeDiskNodeCache;
    }


    /**
     * 添加改变后的diskNode
     * @param diskNode
     */
    public void putChangeNode (Integer id, DiskNode<T> diskNode) {
        changeDiskNodeCache.put(id, diskNode);
    }

    /**
     * 通过id获取缓存的diskNode
     * @param id
     * @return
     */
    public DiskNode<T> getChangeNode(Integer id) {
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
    public DiskNode<T> removeChangeNode(Integer id) {
        return changeDiskNodeCache.remove(id);
    }

    /**
     * 封装内存管理器的空闲页管理
     * @return
     */
    public Integer getFreeId() {
        return memManager.getNewOrFreeId();
    }

    /**
     * 增添空闲页
     * @param id
     */
    public void addFreeId(Integer id) {
        memManager.addFreePage(id);
    }
    /**
     * 遍历叶节点
     * @param root
     */
    public void visitorLeaf(Integer root) {

    }

    private void persistInfoInit(PersistInfo persistInfo) {
        persistInfo.setFreePage(memManager.getFreePage());
        persistInfo.setHeadId(headId);
        persistInfo.setMAX_ID(memManager.getMAX_ID());
        persistInfo.setMAX_SIZE(memManager.getMAX_SIZE());
        persistInfo.setOrder(order);
        persistInfo.setRootId(rootId);
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
