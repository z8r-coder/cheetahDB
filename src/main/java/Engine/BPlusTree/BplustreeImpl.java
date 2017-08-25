package Engine.BPlusTree;

import Engine.Bplustree;

import java.util.Random;

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

    public BplustreeImpl (int order,Node root) {
        this.order = order;
        this.root = root;
    }
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
        root.update(key, obj);
    }

    public static void main(String arg[]) {
        Node root = new Node(true,true);
        Bplustree tree = new BplustreeImpl(6, root);
        Random random = new Random();
        long current = System.currentTimeMillis();
        for (int j = 0; j < 100000; j++) {
            for (int i = 0; i < 100; i++) {
                int randomNumber = random.nextInt(1000);
                tree.insert(randomNumber, randomNumber);
            }

            for (int i = 0; i < 100; i++) {
                int randomNumber = random.nextInt(1000);
                tree.delete(randomNumber);
            }
        }

        long duration = System.currentTimeMillis() - current;
        System.out.println("time elpsed for duration: " + duration);
        int search = 80;
        System.out.print(tree.Search(search));
    }
}
