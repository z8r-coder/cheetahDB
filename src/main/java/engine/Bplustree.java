package engine;

import engine.diskbplustree.DiskNode;

import java.util.List;
import exception.SelectException;

/**
 * Created by rx on 2017/8/21.
 */
public interface Bplustree<T,E> {
    /**
     * 查询
     * @param key
     * @return
     */
    public T search(Comparable key);

    /**
     * 区域查询
     * @param key
     * @param rp
     * @return
     */
    public List<T> searchForList(Comparable key, String rp) throws SelectException;
    /**
     * 移除
     * @param key
     */
    public void delete(Comparable key);

    /**
     * 插入结点
     * @param key
     * @param obj
     */
    public void insert(Comparable key, T obj);

    /**
     * 更新结点
     * @param key
     * @param obj
     */
    public void update(Comparable key, T obj);

    /**
     * 获取B+树的阶数
     * @return
     */
    public int getOrder();

    /**
     * 设置根结点
     * @param root
     */
    public void setRoot(E root);

    /**
     * 获取根结点
     * @return
     */
    public E getRoot();

    /**
     * 设置b+树叶子的头结点
     * @param head
     */
    public void setHead(E head);

    /**
     * 获取头节点
     * @return
     */
    public E getHeadId();

    /**
     * 访问输出叶子结点
     * @param root
     */
    public void visitorLeaf(E root);

    /**
     * 添加改变后的diskNode
     * @param diskNode
     */
    public void putChangeNode(E id, DiskNode<T> diskNode);

    /**
     * 通过缓存或磁盘获取节点
     * @param id
     * @return
     */
    public DiskNode<T> getChangeNode(E id);

    /**
     * 从缓存中移除，防止无用插入和非法访问
     * @param id
     * @return
     */
    public DiskNode<T> removeChangeNode(E id);

    /**
     * 封装内存管理器的空闲页管理
     * @return
     */
    public E getFreeId();

    /**
     * 内存管理器增加空闲页
     * @param id
     */
    public void addFreeId(E id);
}
