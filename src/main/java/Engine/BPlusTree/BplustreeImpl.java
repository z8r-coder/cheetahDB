package Engine.BPlusTree;

import Engine.Bplustree;
import Engine.Node;

/**
 * Created by rx on 2017/8/21.
 */
public class BplustreeImpl implements Bplustree {
    /**
     * 根结点
     */
    private Node root;
    /**
     * 阶数
     */
    private int order;

    /**
     * 链表头
     */
    private Node head;

    public void setRoot(Node root) {
        this.root = root;
    }

    public Node getRoot() {
        return root;
    }

    public void setHead(Node head) {
        this.head = head;
    }

    public Node getHead() {
        return head;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public int getOrder() {
        return order;
    }

    public Object Search(Comparable key) {
        //0 true 1 false

        return root.search(key);
    }

    public void delete(Comparable key) {
        root.remove(key);
    }

    public void insert(Comparable key, Object obj) {
        root.insert(key, obj, this);
    }

    public void update(Comparable key, Object obj) {
        root.update(key, obj, this);
    }

    public static void main(String arg[]) {
        Bplustree bpt = new BplustreeImpl();
        bpt.Search("b");
    }
}
