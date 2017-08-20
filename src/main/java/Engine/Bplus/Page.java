package Engine.Bplus;

/**
 * Created by rx on 2017/8/19.
 */
public interface Page<T,E> {
    /**
     * 插入条目
     * @param entry
     * @return
     */
    public Entry<T,E> insert(Entry<T,E> entry);
}
