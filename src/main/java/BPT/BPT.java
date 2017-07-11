package BPT;

/**
 * Created by ROY on 2017/7/5.
 */
public interface BPT<T,E> {
    /**
     * 查询条目
     * @param key
     * @return
     */
    public Entry search(Node node,T key) throws Exception;

    /**
     * 插入条目
     * @param entry
     */
    public void insert(Node node, Entry<T,E> entry) throws Exception;

    /**
     * 删除条目
     * @param key
     */
    public void delete(T key) throws Exception;

    /**
     * 更新条目
     * @param key
     */
    public void update(Node node, T key, Entry<T,E> entry) throws Exception;
}
