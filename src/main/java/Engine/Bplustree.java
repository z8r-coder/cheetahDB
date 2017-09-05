package Engine;

import Engine.MemBPlusTree.Node;

import java.util.List;
import Exception.SelectException;

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
     * 访问输出叶子结点
     * @param root
     */
    public void visitorLeaf(E root);
}
