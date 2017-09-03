package Engine.BPlusTree;

import Engine.Bplustree;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Random;
import Exception.SelectException;

/**
 * Created by rx on 2017/8/21.
 */
public class BplustreeImpl<T> implements Bplustree, Serializable {
    /**
     * 根结点
     */
    private Node<T> root;
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

    public T search(Comparable key) {
        //0 true 1 false

        return root.search(key);
    }

    public List<T> searchForList(Comparable key, String rp) throws SelectException {
        return root.searchForList(key, rp);
    }

    public void delete(Comparable key) {
        root.remove(key);
    }

    public void insert(Comparable key, Object obj) {
        root.insert(key, (T) obj, this);
    }

    public void update(Comparable key, Object obj) {
        root.update(key, (T) obj);
    }

    public void visitorLeaf(Node node) {
        if (node == null) {
            return;
        } else {
            if (node.getLeaf()) {
                List<Map.Entry<Comparable, Object>> ens = node.getEntries();
                for (int i = 0; i < ens.size();i++) {
                    System.out.println(ens.get(i).getKey());
                }
            } else {
                List<Node> child = node.getChildren();
                for (int i = 0; i < child.size();i++) {
                    visitorLeaf(child.get(i));
                }
            }
        }
    }
    public static void main(String arg[]) {
        Node<Integer> root = new Node(true,true);
        Bplustree<Integer> tree = new BplustreeImpl(1024, root);
        Random random = new Random();
        long current = System.currentTimeMillis();
        for (int j = 0; j < 1000; j++) {
            for (int i = 0; i < 100; i++) {
                int randomNumber = random.nextInt(1000);
                tree.insert(randomNumber, randomNumber);
            }

//            for (int i = 0; i < 100; i++) {
//                int randomNumber = random.nextInt(1000);
//                tree.delete(randomNumber);
//            }
        }

//        tree.visitorLeaf(tree.getRoot());

        long duration = System.currentTimeMillis() - current;
        System.out.println("time elpsed for duration: " + duration);
        int search = 80;
        System.out.print(tree.search(search));
    }
}
