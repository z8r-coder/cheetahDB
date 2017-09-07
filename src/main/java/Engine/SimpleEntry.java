package Engine;

import java.util.Map;

/**
 * Created by rx on 2017/8/22.
 */
public class SimpleEntry<T,E> implements Map.Entry<T,E>{
    private T key;
    private E value;

    public SimpleEntry(T key, E value) {
        this.key = key;
        this.value = value;
    }
    public T getKey() {
        return key;
    }

    public void setKey(T key) {
        this.key = key;
    }

    public E getValue() {
        return value;
    }

    public E setValue(E value) {
        this.value = value;
        return value;
    }
}
