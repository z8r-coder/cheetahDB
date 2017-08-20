package Engine.Bplus;

/**
 * b+树
 * Created by ruanxin on 2017/8/19.
 */
public interface Bpt<T,E> {
    /**
     * 插入条目
     * @param entry
     * @return
     */
    public boolean insert(Entry<T,E> entry);
}
