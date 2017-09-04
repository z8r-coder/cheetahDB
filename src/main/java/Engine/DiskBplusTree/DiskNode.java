package Engine.DiskBplusTree;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 数据页
 * Created by rx on 2017/9/4.
 */
public class DiskNode<T> {

    /**
     * 是否是叶节点
     */
    private boolean leaf;

    /**
     * 是否是根结点
     */
    private boolean root;

    /**
     * 该数据页中的数据
     */
    private List<Map.Entry<Comparable, T>> entries = new ArrayList<Map.Entry<Comparable, T>>();

    /**
     * 父节点在磁盘中的位置
     */
    private long parentId;

    /**
     * 前驱节点在磁盘中的位置
     */
    private long prevId;

    /**
     * 后继节点在磁盘中的位置
     */
    private long nextId;

    /**
     * 儿子结点在磁盘中的位置
     */
    private List<Long> childrenId;

    public DiskNode(boolean leaf) {
        this.leaf = leaf;
        if (!leaf) {
            childrenId = new ArrayList<Long>();
        }
    }

    public DiskNode(boolean leaf, boolean root) {
        this.leaf = leaf;
        this.root = root;
        if (!leaf) {
            childrenId = new ArrayList<Long>();
        }
    }

    public boolean isLeaf() {
        return leaf;
    }

    public void setLeaf(boolean leaf) {
        this.leaf = leaf;
    }

    public boolean isRoot() {
        return root;
    }

    public void setRoot(boolean root) {
        this.root = root;
    }

    public List<Map.Entry<Comparable, T>> getEntries() {
        return entries;
    }

    public void setEntries(List<Map.Entry<Comparable, T>> entries) {
        this.entries = entries;
    }

    public long getParentId() {
        return parentId;
    }

    public void setParentId(long parentId) {
        this.parentId = parentId;
    }

    public long getPrevId() {
        return prevId;
    }

    public void setPrevId(long prevId) {
        this.prevId = prevId;
    }

    public long getNextId() {
        return nextId;
    }

    public void setNextId(long nextId) {
        this.nextId = nextId;
    }

    public List<Long> getChildrenId() {
        return childrenId;
    }

    public void setChildrenId(List<Long> childrenId) {
        this.childrenId = childrenId;
    }

    /**
     * 单查询入口
     * @param key
     * @return
     */
//    public T search(Comparable key) {
//        if (leaf) {
//            for (Map.Entry<Comparable, T> entry : entries) {
//                if (entry.getKey().compareTo(key) == 0) {
//                    return entry.getValue();
//                }
//            }
//            return null;
//        } else {
//            if (key.compareTo(entries.get(0).getKey()) <= 0) {
//
//                return children.get(0).search(key);
//            } else if (key.compareTo(entries.get(entries.size() - 1).getKey()) >= 0) {
//                return children.get(children.size() - 1).search(key);
//            } else {
//                for (int i = 0; i < entries.size();i++) {
//                    if (entries.get(i).getKey().compareTo(key) <= 0 &&
//                            entries.get(i + 1).getKey().compareTo(key) >0) {
//                        return children.get(i).search(key);
//                    }
//                }
//            }
//        }
//    }
}
