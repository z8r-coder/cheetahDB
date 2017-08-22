package Engine.BPlusTree;

import Engine.Bplustree;
import Engine.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by rx on 2017/8/21.
 */
public class NodeImpl implements Node{
    private boolean leaf;

    private boolean root;

    private Node parent;

    private Node previous;//叶子结点的前驱结点

    private Node next;//叶子结点后继结点

    private List<Map.Entry<Comparable, Object>> entries = new ArrayList<Map.Entry<Comparable, Object>>();

    private List<Node> children;

    public NodeImpl(boolean leaf) {
        this.leaf = leaf;
        if (!leaf) {
            children = new ArrayList<Node>();
        }
    }
    public NodeImpl(boolean leaf, boolean root) {
        this.leaf = leaf;
        this.root = root;
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
}
