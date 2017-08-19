package Engine.Bplus.impl;

import Engine.Bplus.BptConstant;
import Engine.Bplus.Entry;
import Engine.Bplus.LeafPage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rx on 2017/8/19.
 */
public class LeafPageImpl<T,E> implements LeafPage<T,E> {
    private LeafPage before;
    private LeafPage after;
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

    public boolean insert(Entry<T, E> entry) {
        return false;
    }
}
