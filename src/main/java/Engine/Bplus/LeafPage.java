package Engine.Bplus;

/**
 * 结点页
 * Created by ruanxin on 2017/8/19.
 */
public interface LeafPage<T,E> extends Page<T,E> {
    /**
     * 插入条目
     * @param entry
     * @return
     */
    public Entry<T,E> insert(Entry<T,E> entry);

    /**
     * 直接插入集合尾部，不用二分
     * @param entry
     * @return
     */
    public void add(Entry<T,E> entry);

    public void add(int index, Entry<T,E> entry);
    /**
     * 返回页中元素个数
     * @return
     */
    public int length();
}
