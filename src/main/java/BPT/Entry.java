package BPT;

import javax.swing.*;

/**
 * Created by roy on 2017/7/2.
 */
public class Entry<T,E> {
    T key;//键
    E value;//值

    public Entry (T key, E value) {
        this.key = key;
        this.value = value;
    }
    public void setKey(T key) {
        this.key = key;
    }

    public T getKey() {
        return key;
    }

    public void setValue(E value) {
        this.value = value;
    }

    public E getValue() {
        return value;
    }
}