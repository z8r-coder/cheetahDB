package Engine.BPlusTree;

import java.util.Map;

/**
 * Created by rx on 2017/8/22.
 */
public class SimpleEntry<T,E> implements Map.Entry<T,E>{
    private T key;

    public SimpleEntry(T key) {
        this.key = key;
    }
    public T getKey() {
        return key;
    }

    public void setKey(T key) {
        this.key = key;
    }

    //无value值
    public E getValue() {
        return null;
    }

    public Object setValue(Object value) {
        return null;
    }
}
