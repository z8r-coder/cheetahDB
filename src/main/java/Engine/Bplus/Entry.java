package Engine.Bplus;

/**
 * Created by ruanxin on 2017/8/19.
 */
public class Entry<T,E> {
    private T key;
    private E value;

    public Entry(T key, E value) {
        this.key = key;
        this.value = value;
    }

    public void setKey(T key) {
        this.key = key;
    }

    public void setValue(E value) {
        this.value = value;
    }

    public T getKey() {
        return key;
    }

    public E getValue() {
        return value;
    }
}
