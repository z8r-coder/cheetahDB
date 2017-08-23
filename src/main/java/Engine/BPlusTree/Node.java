package Engine.BPlusTree;

import Engine.Bplustree;

import java.util.*;

/**
 * comparable res < 0  this < value
 *            res = 0  this = value
 *            res > 0  this > value
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

    public void remove(Comparable key, Bplustree bpt) {
        if (leaf) {
            if (!contains(key)) {
                return;
            }

            //即是叶子节点又是根节点，直接删除
            if (root) {
                remove(key);
            } else {
                if (entries.size() > bpt.getOrder() / 2 &&
                        entries.size() > 2) {
                    remove(key);
                } else {
                    //若本节点关键字数量小于M / 2,并且前驱节点字数大于M / 2，则从其借补
                    if (previous != null &&
                            previous.getEntries().size() > bpt.getOrder() / 2 &&
                            previous.getEntries().size() > 2 &&
                            previous.getParent() == parent) {
                        int preSize = previous.getEntries().size();
                        Map.Entry<Comparable, Object> entry = previous.getEntries().get(preSize - 1);
                        previous.getEntries().remove(entry);

                    }
                }
            }
        }
    }
    public void remove(Comparable key) {

    }

    /**
     * 找到需要插入的位置
     * @param key
     * @param obj
     * @param bpt
     */
    public void insert(Comparable key, Object obj, Bplustree bpt) {
        if (leaf) {
            //叶子结点
            if (contains(key) || entries.size() < bpt.getOrder()) {
                insert(key,obj);
                if (parent != null) {
                    //更新父结点
                    parent.updateInsert(bpt);
                }
            } else {
                //需要分裂
                Node left = new Node(true);
                Node right = new Node(true);

                if (previous != null) {
                    previous.setNext(left);
                    left.setPrevious(previous);
                }
                if (next != null) {
                    next.setPrevious(right);
                    right.setNext(next);
                }
                if (previous == null) {
                    bpt.setHead(left);
                }

                left.setNext(right);
                right.setPrevious(left);
                previous = null;
                next = null;

                int leftSize = (bpt.getOrder() + 1) / 2 + (bpt.getOrder() + 1) % 2;
                int rightSize = (bpt.getOrder() + 1) / 2;

                insert(key, obj);
                for (int i = 0; i < leftSize; i++) {
                    left.getEntries().add(entries.get(i));
                }

                for (int i = 0; i < rightSize; i++) {
                    right.getEntries().add(entries.get(leftSize + i));
                }

                //非根结点
                if (parent != null) {
                    int index = parent.getChildren().indexOf(this);
                    parent.getChildren().remove(this);
                    left.setParent(parent);
                    right.setParent(parent);
                    parent.getChildren().add(index, left);
                    parent.getChildren().add(index + 1, right);
                    setEntries(null);
                    setChildren(null);

                    parent.updateInsert(bpt);
                    setParent(null);
                } else {
                    root = false;
                    Node parent = new Node(false, true);
                    bpt.setRoot(parent);
                    left.setParent(parent);
                    right.setParent(parent);
                    parent.getChildren().add(left);
                    parent.getChildren().add(right);
                    setEntries(null);
                    setChildren(null);

                    parent.updateInsert(bpt);
                }
            }
        } else {
            //非叶节点
            if (key.compareTo(entries.get(0).getKey()) <= 0) {
                children.get(0).insert(key,obj, bpt);
            } else if (key.compareTo(entries.get(entries.size() - 1).getKey()) >= 0) {
                children.get(children.size() - 1).insert(key, obj, bpt);
            } else {
                for (int i = 0; i < entries.size(); i++) {
                    if (entries.get(i).getKey().compareTo(key) <= 0 &&
                            entries.get(i + 1).getKey().compareTo(key) >0) {
                        children.get(i).insert(key, obj, bpt);
                        break;
                    }
                }
            }
        }
    }

    /**
     * 插入关键字
     * @param key
     * @param obj
     */
    public void insert(Comparable key, Object obj) {
        Map.Entry<Comparable, Object> entry = new SimpleEntry<Comparable, Object>(key, obj);
        if (entries.size() == 0) {
            entries.add(entry);
            return;
        }

        for (int i = 0; i < entries.size();i++) {
            if (entries.get(i).getKey().compareTo(key) >= 0) {
                entries.add(i, entry);
                return;
            }
        }

        entries.add(entry);
    }
    public void update(Comparable key, Object obj, Bplustree bpt) {

    }

    public void updateInsert(Bplustree bpt) {
        validate(this, bpt);

        if (children.size() > bpt.getOrder()) {
            Node left = new Node(false);
            Node right = new Node(false);

            //左右俩子字节的长度
            int leftSize = (bpt.getOrder() + 1) / 2 + (bpt.getOrder() + 1) % 2;
            int rightSize = (bpt.getOrder() + 1) / 2;

            for (int i = 0; i < leftSize; i++) {
                left.getChildren().add(children.get(i));
                Comparable key = children.get(i).getEntries().get(0).getKey();
                left.getEntries().add(new SimpleEntry<Comparable, Object>(key,null));
                children.get(i).setParent(left);
            }

            for (int i = 0; i < rightSize;i++) {
                right.getChildren().add(children.get(i));
                Comparable key = children.get(leftSize + i).getEntries().get(0).getKey();
                right.getEntries().add(new SimpleEntry<Comparable, Object>(key,null));
                children.get(leftSize + i).setParent(right);
            }

            if (parent != null) {
                int index = parent.getChildren().indexOf(this);
                parent.getChildren().remove(this);
                left.setParent(parent);
                right.setParent(parent);

                parent.getChildren().add(index, left);
                parent.getChildren().add(index + 1, right);

                setEntries(null);
                setChildren(null);

                parent.updateInsert(bpt);
                setParent(null);
            } else {
                root = false;
                Node parent = new Node(false, true);
                bpt.setRoot(parent);
                left.setParent(parent);
                right.setParent(parent);
                parent.getChildren().add(left);
                parent.getChildren().add(right);
                setEntries(null);
                setChildren(null);

                parent.updateInsert(bpt);
            }
        }
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
                    node.getEntries().add(i, new SimpleEntry<Comparable, Object>(key, null));
                    if (!node.root) {
                        validate(node.getParent(), bpt);
                    }
                }
            }
            //若子节点数不等于关键字个数但仍大于M/2并且小于M，并且大于2
        } else if (node.root && node.getChildren().size() >= 2 ||
                node.getChildren().size() >= bpt.getOrder() / 2 &&
                node.getChildren().size() <= bpt.getOrder() &&
                node.getChildren().size() >= 2) {
            node.getEntries().clear();
            for (int i = 0; i < node.getChildren().size();i++) {
                Comparable key = node.getChildren().get(i).getEntries().get(0).getKey();
                node.getEntries().add(new SimpleEntry<Comparable, Object>(key, null));
                if (!node.root) {
                    validate(node.getParent(), bpt);
                }
            }
        }
    }

    /**
     * 当前结点是否包含该关键字
     * @param key
     * @return
     */
    protected boolean contains(Comparable key) {
        for (Map.Entry<Comparable, Object> entry : entries) {
            if (entry.getKey().compareTo(key) == 0) {
                return true;
            }
        }
        return false;
    }
}
