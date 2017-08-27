package Engine;

import Engine.BPlusTree.Node;

/**
 * Created by rx on 2017/8/21.
 */
public interface Bplustree {
    /**
     * 查询
     * @param key
     * @return
     */
    public Object search(Comparable key);

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
    public void insert(Comparable key, Object obj);

    /**
     * 更新结点
     * @param key
     * @param obj
     */
    public void update(Comparable key, Object obj);

    /**
     * 获取B+树的阶数
     * @return
     */
    public int getOrder();

    /**
     * 设置根结点
     * @param root
     */
    public void setRoot(Node root);

    /**
     * 获取根结点
     * @return
     */
    public Node getRoot();

    /**
     * 设置b+树叶子的头结点
     * @param head
     */
    public void setHead(Node head);

    /**
     * 访问输出叶子结点
     * @param root
     */
    public void visitorLeaf(Node root);
}
