package engine.membplustree;

import constants.SQLErrorCode;
import engine.Bplustree;

import java.util.*;

import engine.SimpleEntry;
import exception.SelectException;

/**
 * comparable res < 0  this < value
 *            res = 0  this = value
 *            res > 0  this > value
 * Created by rx on 2017/8/21.
 */
public class Node<T> {
    private boolean leaf;

    private boolean root;

    private Node<T> parent;

    private Node<T> previous;//叶子结点的前驱结点

    private Node<T> next;//叶子结点后继结点

    private List<Map.Entry<Comparable, T>> entries = new ArrayList<Map.Entry<Comparable, T>>(1024);

    private List<Node<T>> children;

    public Node(boolean leaf) {
        this.leaf = leaf;
        if (!leaf) {
            children = new ArrayList<Node<T>>();
        }
    }
    public Node(boolean leaf, boolean root) {
        this.leaf = leaf;
        this.root = root;
        if (!leaf) {
            children = new ArrayList<Node<T>>();
        }
    }

    public void setRoot(boolean root) {
        this.root = root;
    }

    public boolean getRoot() {
        return root;
    }

    public void setChildren(List<Node<T>> children) {
        this.children = children;
    }

    public List<Node<T>> getChildren() {
        return children;
    }

    public void setLeaf(boolean leaf) {
        this.leaf = leaf;
    }

    public boolean getLeaf() {
        return leaf;
    }

    public void setEntries(List<Map.Entry<Comparable, T>> entries) {
        this.entries = entries;
    }

    public List<Map.Entry<Comparable, T>> getEntries() {
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

    /**
     * 单个查询接口
     * @param key
     * @return
     */
    public T search(Comparable key) {
        if (leaf) {
            for (Map.Entry<Comparable, T> entry : entries) {
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

    /**
     * 辅助查询，获取子节点
     * @param key
     * @return
     */
    public Node<T> searchAuxiliary(Comparable key) {
        if (leaf) {
            return this;
        } else {
            if (key.compareTo(entries.get(0).getKey()) < 0) {
                return children.get(0).searchAuxiliary(key);
            } else if (key.compareTo(entries.get(entries.size() - 1).getKey()) >= 0) {
                return children.get(children.size() - 1).searchAuxiliary(key);
            } else {
                for (int i = 0; i < entries.size();i++) {
                    if (entries.get(i).getKey().compareTo(key) <= 0 &&
                            entries.get(i + 1).getKey().compareTo(key) >0) {
                        return children.get(0).searchAuxiliary(key);
                    }
                }
            }
        }
        return null;
    }

    /**
     * 多对象查询数组
     * @param key 索引
     * @Param rp  关系
     * @return
     */
    public List<T> searchForList(Comparable key, String rp) throws SelectException {
        List<T> resList = new ArrayList<T>();
        Node<T> curNode = searchAuxiliary(key);
        if (curNode == null) {
            return null;
        }

        if (rp.equals("<")) {
            //获取第一个大于等于key的值
            int index = searchFirstBiggerEq(entries, key, ">=");
            for (int i = 0; i < index;i++) {
                resList.add(entries.get(i).getValue());
            }
            addPrevious2List(resList);
        } else if (rp.equals("<=")) {
            //获取第一个大于key的值
            int index = searchFirstBiggerEq(entries, key, ">");
            for (int i = 0; i < index;i++) {
                resList.add(entries.get(i).getValue());
            }
            addPrevious2List(resList);
        } else if (rp.equals(">")) {
            int index = searchFirstBiggerEq(entries, key, ">");
            for (int i = index; i < entries.size();i++) {
                resList.add(entries.get(i).getValue());
            }
            addNext2List(resList);
        } else if (rp.equals(">=")) {
            int index = searchFirstBiggerEq(entries, key, ">=");
            for (int i = index; i < entries.size();i++) {
                resList.add(entries.get(i).getValue());
            }
            addNext2List(resList);
        }
        return resList;
    }

    /**
     * 将当前结点后面的结点的所有entry全部放入resEntries
     * @param resEntries
     * @return
     */
    private List<T> addNext2List(List<T> resEntries) {
        Node<T> node = next;
        while (node != null) {
            List<Map.Entry<Comparable, T>> tmpEntries = node.getEntries();
            for (int i = 0; i < tmpEntries.size();i++) {
                resEntries.add(tmpEntries.get(i).getValue());
            }
            node = node.getNext();
        }
        return resEntries;
    }
    /**
     * 将当前结点前面结点的所有entry全部放入resEntries
     * @param resEntries
     * @return
     */
    private List<T> addPrevious2List(List<T> resEntries) {
        Node<T> node = previous;
        while (node != null) {
            List<Map.Entry<Comparable, T>> tmpEntries = node.getEntries();
            for (int i = 0; i < tmpEntries.size();i++) {
                resEntries.add(tmpEntries.get(i).getValue());
            }
            node = node.getPrevious();
        }
        return resEntries;
    }
    /**
     * 查询第一个与key相等，比key大
     * @param entries
     * @param key
     * @param op
     * @return
     */
    private int searchFirstBiggerEq(List<Map.Entry<Comparable, T>> entries,
                                 Comparable key, String op) throws SelectException {
        if (op.equals(">=")) {
            for (int i = 0; i < entries.size();i++) {
                //找到第一个大于或等于key的位置
                if (entries.get(i).getKey().compareTo(key) >= 0) {
                    return i;
                }
            }
            //若不存在相等的则返回-1
            return entries.size();
        } else if (op.equals(">")) {
            //找到第一个比key大的位置
            for (int i = 0; i < entries.size();i++) {
                if (entries.get(i).getKey().compareTo(key) > 0) {
                    return i;
                }
            }
            //若key为该entries中最大的则返回-1
            return entries.size();
        } else {
            throw new SelectException(SQLErrorCode.SQL00043);
        }
    }

    /**
     * 找到remove的位置
     * @param key
     * @param bpt
     */
    public void remove(Comparable key, Bplustree bpt) {
        if (leaf) {
            int index = contains(key);
            if (index < 0) {
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
                        Map.Entry<Comparable, T> entry = previous.getEntries().get(preSize - 1);
                        previous.getEntries().remove(entry);

                        entries.add(0, entry);
                        remove(key);
                    } else if (next != null &&
                            next.getEntries().size() > bpt.getOrder() / 2 &&
                            next.getEntries().size() > 2 &&
                            next.getParent() == parent) {
                        Map.Entry<Comparable, T> entry = next.getEntries().get(0);
                        next.getEntries().remove(entry);

                        entries.add(entry);
                        remove(key);
                    } else {
                        if (previous != null &&
                                (previous.getEntries().size() <= bpt.getOrder() / 2 ||
                                previous.getEntries().size() <= 2) &&
                                previous.getParent() == parent) {
                            //同前面的结点合并
                            for (int i = previous.getEntries().size() - 1;
                                    i >= 0;i--) {
                                entries.add(0, previous.getEntries().get(i));
                            }
                            remove(key);
                            previous.setParent(null);
                            previous.setEntries(null);
                            parent.getChildren().remove(previous);

                            //更新链表
                            if (previous.getPrevious() != null) {
                                Node tmp = previous;
                                tmp.getPrevious().setNext(this);
                                previous = tmp.getPrevious();
                                tmp.setPrevious(null);
                                tmp.setNext(null);
                            } else {
                                bpt.setHead(this);
                                previous.setNext(null);
                                previous = null;
                            }
                        } else if (next != null &&
                                (next.getEntries().size() <= bpt.getOrder() / 2 ||
                                next.getEntries().size() <= 2) &&
                                next.getParent() == parent) {
                            for (int i = 0; i < next.getEntries().size();i++) {
                                entries.add(next.getEntries().get(i));
                            }
                            remove(key);
                            next.setParent(null);
                            next.setEntries(null);
                            parent.getChildren().remove(next);

                            //更新链表
                            if (next.getNext() != null) {
                                Node tmp = next;
                                tmp.getNext().setPrevious(this);
                                next = tmp.getNext();
                                tmp.setPrevious(null);
                                tmp.setNext(null);
                            } else {
                                next.setPrevious(null);
                                next = null;
                            }
                        }
                    }
                }
                parent.updateRemove(bpt);
            }
        } else {
            if (key.compareTo(entries.get(0).getKey()) <= 0) {
                children.get(0).remove(key, bpt);
            } else if (key.compareTo(entries.get(entries.size() - 1).getKey()) >= 0) {
                children.get(children.size() - 1).remove(key, bpt);
            } else {
                for (int i = 0; i < entries.size();i++) {
                    if (entries.get(i).getKey().compareTo(key) <= 0 &&
                            entries.get(i + 1).getKey().compareTo(key) > 0) {
                        children.get(i).remove(key,bpt);
                        break;
                    }
                }
            }
        }
    }

    /**
     * 调整结点
     * @param bpt
     */
    public void updateRemove(Bplustree bpt) {
        validate(this, bpt);

        if (children.size() < bpt.getOrder() / 2 || children.size() < 2) {
            //子结点个数少于M / 2 或者小于2 ，则需要合并结点
            if (root) {
                if (children.size() >= 2) {
                    return;
                } else {
                    //合并
                    Node root = children.get(0);
                    bpt.setRoot(root);
                    root.setParent(null);
                    root.setRoot(true);
                    setEntries(null);
                    setChildren(null);
                }
            } else {
                int currentIndex = parent.getChildren().indexOf(this);
                int preIndex = currentIndex - 1;
                int nextIndex = currentIndex + 1;
                Node<T> previous = null, next = null;

                if (preIndex >= 0) {
                    previous = parent.getChildren().get(preIndex);
                }
                if (nextIndex < parent.getChildren().size()) {
                    next = parent.getChildren().get(nextIndex);
                }

                if (previous != null &&
                        previous.getChildren().size() > bpt.getOrder() / 2 &&
                        previous.getChildren().size() > 2) {
                    int idx = previous.getChildren().size() - 1;
                    Node<T> borrow =previous.getChildren().get(idx);
                    previous.getChildren().remove(idx);
                    borrow.setParent(this);

                    children.add(0, borrow);
                    validate(previous, bpt);
                    validate(this, bpt);
                    parent.updateRemove(bpt);

                } else if (next != null &&
                        next.getChildren().size() > bpt.getOrder() / 2 &&
                        next.getChildren().size() > 2) {
                    Node borrow = next.getChildren().get(0);
                    next.getChildren().remove(0);
                    borrow.setParent(this);
                    children.add(borrow);
                    validate(next, bpt);
                    validate(this, bpt);
                    parent.updateRemove(bpt);
                } else {
                    //合并结点
                    if (previous != null &&
                            (previous.getChildren().size() <= bpt.getOrder() / 2 ||
                            previous.getChildren().size() <= 2)) {
                        //同前面的结点合并
                        for (int i = previous.getChildren().size() - 1; i >= 0;i--) {
                            Node child = previous.getChildren().get(i);
                            children.add(0, child);
                            child.setParent(this);
                        }
                        previous.setChildren(null);
                        previous.setEntries(null);
                        previous.setParent(null);
                        parent.getChildren().remove(previous);
                        validate(this, bpt);
                        parent.updateRemove(bpt);
                    } else if (next != null
                            && (next.getChildren().size() <= bpt.getOrder() / 2 ||
                    next.getChildren().size() <= 2)) {
                        for (int i = 0; i < next.getChildren().size();i++) {
                            Node child = next.getChildren().get(i);
                            children.add(child);
                            child.setParent(this);
                        }
                        next.setChildren(null);
                        next.setEntries(null);
                        next.setParent(null);
                        parent.getChildren().remove(next);
                        validate(this, bpt);
                        parent.updateRemove(bpt);
                    }
                }
            }
        }
    }

    /**
     * 删除条目
     * @param key
     */
    public void remove(Comparable key) {
        int index = -1;
        for (int i = 0; i < entries.size();i++) {
            if (entries.get(i).getKey().compareTo(key) == 0) {
                index = i;
                break;
            }
        }
        if (index != -1) {
            entries.remove(index);
        }
    }

    /**
     * 找到需要插入的位置
     * @param key
     * @param obj
     * @param bpt
     */
    public void insert(Comparable key, T obj, Bplustree bpt) {
        if (leaf) {
            //叶子结点
            if (entries.size() < bpt.getOrder()) {
                int index = insert(key,obj);
                if (parent != null && index == 0) {
                    //更新父结点
                    parent.updateInsert(bpt);
                }
            } else {
                //需要分裂
                Node<T> left = new Node(true);
                Node<T> right = new Node(true);

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
    public int insert(Comparable key, T obj) {
        Map.Entry<Comparable, T> entry = new SimpleEntry<Comparable, T>(key, obj);
        if (entries.size() == 0) {
            entries.add(entry);
            return 0;
        }

        for (int i = 0; i < entries.size();i++) {
            if (entries.get(i).getKey().compareTo(key) >= 0) {
                entries.add(i, entry);
                return i;
            }
        }

        entries.add(entry);
        return entries.size();
    }

    /**
     * 更新节点
     * @param key
     * @param obj
     */
    public void update(Comparable key, T obj) {
        if (!leaf) {
            //非叶子节点
            if (key.compareTo(entries.get(0).getKey()) <= 0) {
                children.get(0).update(key, obj);
            } else if (key.compareTo(entries.get(entries.size() - 1).getKey()) >= 0) {
                children.get(children.size() - 1).update(key, obj);
            } else {
                for (int i = 0; i < entries.size();i++) {
                    if (entries.get(i).getKey().compareTo(key) <= 0 &&
                            entries.get(i + 1).getKey().compareTo(key) > 0) {
                        children.get(i).update(key, obj);
                        break;
                    }
                }
            }
        } else {
            //根节点
            int index = contains(key);
            if (index < 0) {
                return;
            } else {
                entries.get(index).setValue(obj);
            }
        }
    }

    /**
     * 内部结点更新
     * @param bpt
     */
    public void updateInsert(Bplustree bpt) {
        validate(this, bpt);

        if (children.size() > bpt.getOrder()) {
            Node left = new Node(false);
            Node right = new Node(false);

            //左右俩子节点的长度
            int leftSize = (bpt.getOrder() + 1) / 2 + (bpt.getOrder() + 1) % 2;
            int rightSize = (bpt.getOrder() + 1) / 2;

            for (int i = 0; i < leftSize; i++) {
                left.getChildren().add(children.get(i));
                Comparable key = children.get(i).getEntries().get(0).getKey();
                left.getEntries().add(new SimpleEntry<Comparable, Object>(key,null));
                children.get(i).setParent(left);
            }

            for (int i = 0; i < rightSize;i++) {
                right.getChildren().add(children.get(leftSize + i));
                Comparable key = children.get(leftSize + i).getEntries().get(0).getKey();
                right.getEntries().add(new SimpleEntry<Comparable, Object>(key,null));
                children.get(leftSize + i).setParent(right);
            }

            if (parent != null) {
                //非根结点
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
                //根结点
                root = false;
                Node parent = new Node(false, true);
                bpt.setRoot(parent);
                left.setParent(parent);
                right.setParent(parent);
                parent.getChildren().add(left);
                parent.getChildren().add(right);
                setEntries(null);
                setChildren(null);
            }
        }
    }

    /**
     * 调整结点关键字
     * @param node
     * @param bpt
     */
    protected void validate(Node<T> node, Bplustree bpt) {
        if (node.getEntries().size() == node.getChildren().size()) {
            //关键字和子结点数目相同
            for (int i = 0; i < node.getEntries().size();i++) {
                //取子结点
                Comparable key = node.getChildren().get(i).getEntries().get(0).getKey();
                if (node.getEntries().get(i).getKey().compareTo(key) != 0) {
                    node.getEntries().remove(i);
                    node.getEntries().add(i, new SimpleEntry<Comparable, T>(key, null));
                }
            }
            if (!node.root) {
                validate(node.getParent(), bpt);
            }
            //若子节点数不等于关键字个数但仍大于M/2并且小于M，并且大于2
        } else if (node.root && node.getChildren().size() >= 2 ||
                node.getChildren().size() >= bpt.getOrder() / 2 &&
                node.getChildren().size() <= bpt.getOrder() &&
                node.getChildren().size() >= 2) {
            node.getEntries().clear();
            for (int i = 0; i < node.getChildren().size();i++) {
                Comparable key = node.getChildren().get(i).getEntries().get(0).getKey();
                node.getEntries().add(new SimpleEntry<Comparable, T>(key, null));
            }
            if (!node.root) {
                validate(node.getParent(), bpt);
            }
        }
    }

    /**
     * 当前结点是否包含该关键字
     * @param key
     * @return
     */
    protected int contains(Comparable key) {
        for (int i = 0; i < entries.size(); i++) {
            if (entries.get(i).getKey().compareTo(key) == 0) {
                return i;
            }
        }
        return -1;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("isRoot: ");
        sb.append(root);
        sb.append(", ");
        sb.append("isLeaf: ");
        sb.append(leaf);
        sb.append(", ");
        sb.append("keys: ");
        for (Map.Entry entry : entries){
            sb.append(entry.getKey());
            sb.append(", ");
        }
        sb.append(", ");
        return sb.toString();
    }

    public static void main(String arg[]) {
        for (int i = 0; i < 0;i++) {
            System.out.println(11);
        }
    }
}
