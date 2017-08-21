package Engine;

/**
 * Created by rx on 2017/8/21.
 */
public interface Node {
    /**
     * 查询
     * @param key
     * @return
     */
    public  Object search(Comparable key);

    /**
     * 删除
     * @param key
     */
    public void remove(Comparable key);

    /**
     * 插入
     * @param key
     */
    public void insert(Comparable key, Object obj, Bplustree bpt);

    /**
     * 更新
     * @param key
     * @param obj
     */
    public void update(Comparable key, Object obj,Bplustree bpt);
}
