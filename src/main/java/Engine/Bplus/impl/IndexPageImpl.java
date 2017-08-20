package Engine.Bplus.impl;

import Engine.Bplus.Entry;
import Engine.Bplus.IndexPage;
import Engine.Bplus.LeafPage;

import java.util.List;

/**
 * Created by rx on 2017/8/19.
 */
public class IndexPageImpl<T,E> implements IndexPage<T,E>{
    private List<Entry<T,E>> entries;//索引
    private List<LeafPage> leafPages;

    public Entry<T, E> insert(Entry<T, E> entry) {
        return null;
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
