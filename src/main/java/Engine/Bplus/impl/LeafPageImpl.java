package Engine.Bplus.impl;

import Engine.Bplus.BptConstant;
import Engine.Bplus.Entry;
import Engine.Bplus.IndexPage;
import Engine.Bplus.LeafPage;
import Engine.EngineUtils;
import Utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rx on 2017/8/19.
 */
public class LeafPageImpl<T,E> implements LeafPage<T,E> {
    private LeafPage before;
    private LeafPage after;
    private boolean first;//是否是第一个子节点
    private boolean last;//是否是最后一个子节点
    private List<Entry<T, E>> entries = new ArrayList<Entry<T, E>>(BptConstant.PAGE_SIZE);

    public LeafPageImpl(LeafPage before, LeafPage after) {
        this.before = before;
        this.after = after;
    }

    public void setAfter(LeafPage after) {
        this.after = after;
    }

    public LeafPage getAfter() {
        return after;
    }

    public void setBefore(LeafPage before) {
        this.before = before;
    }

    public LeafPage getBefore() {
        return before;
    }

    public void setFirst(boolean first) {
        this.first = first;
    }

    public boolean getFirst() {
        return first;
    }

    public void setLast (boolean last) {
        this.last = last;
    }

    public boolean getLast() {
        return last;
    }

    public int length() {
        return entries.size();
    }

    public void add(Entry<T,E> entry) {
        entries.add(entry);
    }

    public void add(int index, Entry<T, E> entry) {
        entries.add(index, entry);
    }

    //现默认按mid点分裂
    public Entry<T,E> insert(Entry<T, E> entry) {
        if (entries.size() == 0) {
            entries.add(entry);
            return null;
        }
        int mid = binarySearch(entries, entry.getKey());
        if (entries.size() < BptConstant.PAGE_SIZE) {
            entries.add(mid, entry);
            return null;
        } else if (first == false && before != null && before.length() < BptConstant.PAGE_SIZE) {
            //先看左结点，非首结点并且左兄弟结点非空(该判断可无),且未满，则旋转结点
            Entry<T,E> tmp = entries.get(0);
            before.add(tmp);

            entries.remove(0);
            entries.add(mid, entry);

            //返回索引层
            return entries.get(0);
        } else if (last == false && after != null && after.length() < BptConstant.PAGE_SIZE) {
            //右结点是否满,并且自己非尾结点
            Entry<T,E> tmp = entries.get(BptConstant.PAGE_SIZE - 1);
            after.add(0,tmp);

            entries.remove(BptConstant.PAGE_SIZE - 1);
            entries.add(mid, entry);

            //返回索引层
            return tmp;
        } else {
            //分裂交给上indexPage来处理，此处返回索引entry
            int splitMid = BptConstant.PAGE_SIZE / 2 + 1;
            entries.add(mid, entry);
            return entries.get(splitMid);
        }
    }

    private int binarySearch(List<Entry<T, E>> list, T target) {
        //此处个target为索引列
        int start = 0;
        int end = list.size() - 1;
        int mid = (start + end) / 2;
        while (list.get(mid).getKey().hashCode() != target.hashCode() && end > start) {
            if (list.get(mid).getKey().hashCode() > target.hashCode()) {
                end = mid - 1;
            } else if (list.get(mid).getKey().hashCode() < target.hashCode()) {
                start = mid + 1;
            }
            mid = (start + end) / 2;
        }
        if (list.get(mid).getKey().hashCode() >= target.hashCode()) {
            return mid;
        } else {
            return mid + 1;
        }
    }
}
