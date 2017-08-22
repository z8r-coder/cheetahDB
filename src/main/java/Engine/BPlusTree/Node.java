package Engine.BPlusTree;

import Engine.Bplustree;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Created by rx on 2017/8/21.
 */
public class Node {
    private boolean leaf;

    private boolean root;

    private Node parent;

    private Node previous;//叶子结点的前驱结点

    private Node next;//叶子结点后继结点

    private List<Map.Entry<Comparable, Object>> entries = new ArrayList<Map.Entry<Comparable, Object>>();

    private List<Node> children;

    public Node(boolean leaf) {
        this.leaf = leaf;
        if (!leaf) {
            children = new ArrayList<Node>();
        }
    }
    public Node(boolean leaf, boolean root) {
        this.leaf = leaf;
        this.root = root;
    }

    public void setRoot(boolean root) {
        this.root = root;
    }

    public boolean getRoot() {
        return root;
    }

    public void setChildren(List<Node> children) {
        this.children = children;
    }

    public List<Node> getChildren() {
        return children;
    }

    public void setLeaf(boolean leaf) {
        this.leaf = leaf;
    }

    public boolean getLeaf() {
        return leaf;
    }

    public void setEntries(List<Map.Entry<Comparable, Object>> entries) {
        this.entries = entries;
    }

    public List<Map.Entry<Comparable, Object>> getEntries() {
        return entries;
    }

    public void setPrevious(Node previous) {
        this.previous = previous;
    }

    public Node getPrevious() {
        return previous;
    }

    public void setNext(Node next) {
        this.next = next;
    }

    public Node getNext() {
        return next;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    public Node getParent() {
        return parent;
    }

    public Object search(Comparable key) {
        if (leaf) {
            for (Map.Entry<Comparable, Object> entry : entries) {
                if (entry.getKey().compareTo(key) == 0) {
                    return entry.getValue();
                }
            }
            return null;
        } else {
            if (key.compareTo(entries.get(0).getKey()) <= 0) {
                return children.get(0).search(key);
            } else if (key.compareTo(entries.get(entries.size() - 1).getKey()) >= 0) {
                return children.get(children.size() - 1).search(key);
            } else {
                for (int i = 0; i < entries.size();i++) {
                    if (entries.get(i).getKey().compareTo(key) <= 0 &&
                            entries.get(i + 1).getKey().compareTo(key) >0) {
                        return children.get(i).search(key);
                    }
                }
            }
        }
        return null;
    }

    public void remove(Comparable key) {

    }

    public void insert(Comparable key, Object obj, Bplustree bpt) {

    }

    public void update(Comparable key, Object obj, Bplustree bpt) {

    }

    public void updateInsert(Bplustree btp) {

    }

    /**
     * 调整结点关键字
     * @param node
     * @param bpt
     */
    protected static void validate(Node node, Bplustree bpt) {
        if (node.getEntries().size() == node.getChildren().size()) {
            for (int i = 0; i < node.getEntries().size();i++) {
                //取子结点
                Comparable key = node.getChildren().get(i).getEntries().get(0).getKey();
                if (node.getEntries().get(i).getKey().compareTo(key) != 0) {
                    node.getEntries().remove(i);
                    node.getEntries().add(i, new SimpleEntry<Comparable, Object>(key));
                }
            }
        }
    }

}
