package Engine;

/**
 * Created by rx on 2017/8/21.
 */
public interface Bplustree {
    /**
     * 查询
     * @param key
     * @return
     */
    public Object Search(Comparable key);

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
}
