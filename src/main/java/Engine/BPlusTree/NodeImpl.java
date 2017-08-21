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
    public Object search(Comparable key) {

    }

    public void remove(Comparable key) {

    }

    public void insert(Comparable key, Object obj, Bplustree bpt) {

    }

    public void update(Comparable key, Object obj, Bplustree bpt) {

    }
}
