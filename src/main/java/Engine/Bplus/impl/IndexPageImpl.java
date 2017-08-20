package Engine.Bplus.impl;

import Engine.Bplus.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rx on 2017/8/19.
 */
public class IndexPageImpl<T,E> implements IndexPage<T,E>{
    private List<Entry<T,E>> entries = new ArrayList<Entry<T, E>>(BptConstant.PAGE_SIZE);//索引
    private List<Page<T,E>> leafPages = new ArrayList<Page<T,E>>(BptConstant.PAGE_SIZE);

    public Entry<T, E> insert(Entry<T, E> entry) {
        int mid = binarySearch(entries, entry.getKey());
        Entry<T,E> resEntry = null;
        Page<T,E> page = null;
        int pos;
        if (entry.getKey().hashCode() < entries.get(mid).getKey().hashCode()) {
            pos = mid;
            page = leafPages.get(mid);
            resEntry = page.insert(entry);
        } else {
            pos = mid + 1;
            page = leafPages.get(mid + 1);
            resEntry = page.insert(entry);
        }
        if (page instanceof LeafPage) {
            if (((LeafPage) page).length() > BptConstant.PAGE_SIZE) {

            }
        }
        return null;
    }

    public void add(LeafPage<T, E> leafPage) {
        leafPages.add(leafPage);
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
