package Engine.Bplus;

import java.util.List;

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

    /**
     * 返回叶结点中所有的条目
     * @return
     */
    public List<Entry<T, E>> getEntries();

    /**
     * 设置后置结点
     * @param after
     */
    public void setAfter(LeafPage after);

    /**
     * 获得后置结点
     * @return
     */
    public LeafPage getAfter();

    /**
     * 设置前置结点
     * @param before
     */
    public void setBefore(LeafPage before);

    /**
     * 获得前置结点
     * @return
     */
    public LeafPage getBefore();

    /**
     * 设置该结点是否为首结点
     * @param first
     */
    public void setFirst(boolean first);

    /**
     * 获取该结点是否为首结点信息
     * @return
     */
    public boolean getFirst();

    /**
     * 设置该节点是否为尾结点
     * @param last
     */
    public void setLast (boolean last);

    /**
     * 获取该结点是否为尾结点
     * @return
     */
    public boolean getLast();
}
