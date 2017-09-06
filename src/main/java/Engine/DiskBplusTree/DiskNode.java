package Engine.DiskBplusTree;

import Engine.Bplustree;
import Engine.MemBPlusTree.SimpleEntry;
import Engine.MemManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 一页4KB大小
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
     * 若该节点即为根节点，则parentId = -1
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
     * 自身在磁盘中的位置
     */
    private long id;
    /**
     * 儿子结点在磁盘中的位置
     */
    private List<Long> childrenId;

    public DiskNode(boolean leaf, long id) {
        this.id = id;
        this.leaf = leaf;
        if (!leaf) {
            childrenId = new ArrayList<Long>();
        }
    }

    public DiskNode(boolean leaf, boolean root,long id) {
        this.id = id;
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

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
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

    /**
     * 节点插入
     * @param key
     * @param obj
     * @param bpt
     * @param memManager
     */
    public void insert(Comparable key, T obj, Bplustree bpt, MemManager<T> memManager) {
        if (leaf) {

        }
    }

    /**
     * 内部页的更新
     * @param bpt
     * @param memManager
     * @throws Exception
     */
    public void updateInsert(Bplustree bpt, MemManager<T> memManager) throws Exception {
        validate(this, bpt, memManager);

        if (childrenId.size() > bpt.getOrder()) {
            //分裂，将本身节点在磁盘中的位置给左节点，右节点看空闲页中是否有数据，若没有在添加尾后
            DiskNode<T> left = new DiskNode<T>(false,this.id);
            long newId;
            if (memManager.freePageSize() != 0) {
                //从空闲页中取
                newId = memManager.removeFreePage();
            } else {
                //生成新的页
                newId = memManager.addAndRetMaxId();
            }
            DiskNode<T> right = new DiskNode<T>(false, newId);

            int leftSize = (bpt.getOrder() + 1) / 2 + (bpt.getOrder() + 1) % 2;
            int rightSize = (bpt.getOrder() + 1) / 2;
            //将以前一半的子节点放入左节点
            for (int i = 0; i < leftSize; i++) {
                left.getChildrenId().add(childrenId.get(i));
                long childId = childrenId.get(i);
                DiskNode<T> childDiskNode = memManager.getPageById(childId);
                Comparable key = childDiskNode.getEntries().get(0).getKey();
                left.getEntries().add(new SimpleEntry<Comparable, T>(key, null));

                childDiskNode.setParentId(this.id);
            }

            //后一半的子节点放入右节点
            for (int i = 0; i < rightSize;i++) {
                right.getChildrenId().add(childrenId.get(leftSize + i));
                long childId = childrenId.get(leftSize + i);
                DiskNode<T> childDiskNode = memManager.getPageById(childId);
                Comparable key = childDiskNode.getEntries().get(0).getKey();
                right.getEntries().add(new SimpleEntry<Comparable, T>(key, null));

                childDiskNode.setParentId(newId);
            }

            if (parentId != -1) {
                //非根结点
                DiskNode<T> parentDiskNode = memManager.getPageById(parentId);
                int index = parentDiskNode.getChildrenId().indexOf(id);
                //此处不用移除以前的id，作为left节点的id了
                left.setParentId(parentId);
                right.setParentId(parentId);

                parentDiskNode.getChildrenId().add(index + 1,newId);
                setEntries(null);
                setChildrenId(null);

                parentDiskNode.updateInsert(bpt, memManager);
                setParentId(-1);
                //将源节点的位置更新为left
                memManager.writeToDisk(left.getId(), left);
                //写入右结点
                memManager.writeToDisk(right.getId(), right);
            } else {
                //根节点
                root = false;

                long newParentId;
                if (memManager.freePageSize() != 0) {
                    newParentId = memManager.removeFreePage();
                } else {
                    newParentId = memManager.addAndRetMaxId();
                }

                DiskNode<T> diskParentNode = new DiskNode<T>(false, true, newParentId);
                left.setParentId(newParentId);
                right.setParentId(newParentId);

                //b+树设置新的根结点
                bpt.setRoot(newParentId);

                diskParentNode.getChildrenId().add(left.getId());
                diskParentNode.getChildrenId().add(right.getId());

                setEntries(null);
                setChildrenId(null);
                //将新产生的结点写入磁盘
                memManager.writeToDisk(newParentId, diskParentNode);
                memManager.writeToDisk(right.getId(), right);
            }
        }
    }

    /**
     * todo 此处写的不好，太多的磁盘io，后期想方法优化
     * 调整关键字节点
     * @param diskNode
     * @param bpt
     */
    public void validate(DiskNode<T> diskNode, Bplustree bpt, MemManager<T> memManager) throws Exception {
        if (diskNode.getEntries().size() == diskNode.getChildrenId().size()) {
            //关键字节点和节点数目相同
            List<Map.Entry<Comparable, T>> entries = diskNode.getEntries();
            for (int i = 0; i < entries.size();i++) {
                long childrenId = diskNode.getChildrenId().get(i);
                //获取磁盘上的子节点
                DiskNode<T> dnode = memManager.getPageById(childrenId);
                Comparable key = dnode.getEntries().get(0).getKey();
                if (diskNode.getEntries().get(i).getKey().compareTo(key) != 0) {
                    diskNode.getEntries().remove(i);
                    diskNode.getEntries().add(i, new SimpleEntry<Comparable, T>(key, null));
                }
            }
            //将修改后的结点插入磁盘
            memManager.writeToDisk(diskNode.getId(), diskNode);
            if (!diskNode.root){
                long parentId = diskNode.getParentId();
                DiskNode<T> parentNode = memManager.getPageById(parentId);
                validate(parentNode, bpt, memManager);
            }
        } else if (diskNode.root && diskNode.getChildrenId().size() >= 2 ||
                diskNode.getChildrenId().size() >= bpt.getOrder() / 2 &&
                diskNode.getChildrenId().size() <= bpt.getOrder() &&
                diskNode.getChildrenId().size() >= 2) {
                //若子节点数不等于关键字个数但仍大于M/2并且小于M，并且大于2
            diskNode.entries.clear();
            for (int i = 0; i < diskNode.getChildrenId().size();i++) {
                long childrenId = diskNode.getChildrenId().get(i);
                //获取磁盘上的子节点
                DiskNode<T> dnode = memManager.getPageById(childrenId);
                Comparable key = dnode.getEntries().get(0).getKey();
                diskNode.getEntries().add(new SimpleEntry<Comparable, T>(key,null));
            }

            memManager.writeToDisk(diskNode.getId(), diskNode);
            if (!diskNode.root) {
                long parentId = diskNode.getParentId();
                DiskNode<T> parentNode = memManager.getPageById(parentId);
                validate(parentNode, bpt, memManager);
            }
        }
    }

    protected int contain(Comparable key) {
        for (int i = 0; i < entries.size();i++) {
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
}
