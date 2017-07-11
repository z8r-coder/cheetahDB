package BPT;

import java.util.Vector;

/**
 * Created by roy on 2017/7/2.
 */
public class LeafNode<T,E> {
    Vector<Entry<T,E>> entries;//条目集合
    LeafNode next_page;

    //条目集合是排序的
    public void addEntry(Entry<T,E> entry) {
        Vector<Entry<T,E>> cloneEntry = (Vector<Entry<T, E>>) entries.clone();
        for (int i = 0; i < cloneEntry.size();i++) {
            if (cloneEntry.get(i).getKey().hashCode() >= entry.getKey().hashCode()) {
                entries.insertElementAt(entry, i);

            }
            return;
        }
    }
    public void setNext_page(LeafNode next_page) {
        this.next_page = next_page;
    }

    public LeafNode getNext_page() {
        return next_page;
    }
}
