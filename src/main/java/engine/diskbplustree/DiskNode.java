package engine.diskbplustree;

import engine.Bplustree;
import engine.SimpleEntry;
import engine.MemManager;
import support.logging.Log;
import support.logging.LogFactory;

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
    private int parentId;

    /**
     * 前驱节点在磁盘中的位置
     * 若前驱节点不存在，则为-1
     */
    private int prevId;

    /**
     * 后继节点在磁盘中的位置
     * 若后继节点不存在，则为-1
     */
    private int nextId;

    /**
     * 自身在磁盘中的位置
     */
    private int id;
    /**
     * 儿子结点在磁盘中的位置
     */
    private List<Integer> childrenId;

    private transient Log log = LogFactory.getLog(DiskNode.class);

    public DiskNode(boolean leaf, int id) {
        this.id = id;
        this.leaf = leaf;
        if (!leaf) {
            childrenId = new ArrayList<Integer>();
        }
    }

    public DiskNode(boolean leaf, boolean root,int id) {
        this.id = id;
        this.leaf = leaf;
        this.root = root;
        if (!leaf) {
            childrenId = new ArrayList<Integer>();
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

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public int getPrevId() {
        return prevId;
    }

    public void setPrevId(int prevId) {
        this.prevId = prevId;
    }

    public int getNextId() {
        return nextId;
    }

    public void setNextId(int nextId) {
        this.nextId = nextId;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public List<Integer> getChildrenId() {
        return childrenId;
    }

    public void setChildrenId(List<Integer> childrenId) {
        this.childrenId = childrenId;
    }


    /**
     * 单查询入口
     * @param key
     * @return
     */
    public T search(Comparable key, Bplustree bpt) {
        if (leaf) {
            for (Map.Entry<Comparable, T> entry : entries) {
                if (entry.getKey().compareTo(key) == 0) {
                    return entry.getValue();
                }
            }
            return null;
        } else {
            if (key.compareTo(entries.get(0).getKey()) <= 0) {
                int childId = childrenId.get(0);
                DiskNode<T> diskChildNode = bpt.getChangeNode(childId);
                return diskChildNode.search(key, bpt);
            } else if (key.compareTo(entries.get(entries.size() - 1).getKey()) >= 0) {
                int childId = childrenId.get(0);
                DiskNode<T> diskChildNode = bpt.getChangeNode(childId);
                return diskChildNode.search(key, bpt);
            } else {
                for (int i = 0; i < entries.size();i++) {
                    if (entries.get(i).getKey().compareTo(key) <= 0 &&
                            entries.get(i + 1).getKey().compareTo(key) >0) {
                        int childId = childrenId.get(i);
                        DiskNode<T> diskChildNode = bpt.getChangeNode(childId);
                        return diskChildNode.search(key, bpt);
                    }
                }
            }
        }
        return null;
    }

    /**
     * 更新该节点
     * @param key
     * @param obj
     */
    public void update(Comparable key, T obj, Bplustree bpt) {
        if (!leaf) {
            //非叶子节点
            if (key.compareTo(entries.get(0).getKey()) <= 0) {
                int childId = childrenId.get(0);
                DiskNode<T> childDiskNode = bpt.getChangeNode(childId);
                childDiskNode.update(key, obj, bpt);
            } else if (key.compareTo(entries.get(entries.size() - 1).getKey()) >= 0) {
                int childId = childrenId.get(entries.size() - 1);
                DiskNode<T> childDiskNode = bpt.getChangeNode(childId);
                childDiskNode.update(key, obj, bpt);
            } else {
                for (int i = 0; i < entries.size();i++) {
                    if (entries.get(i).getKey().compareTo(key) <= 0 &&
                            entries.get(i + 1).getKey().compareTo(key) > 0) {
                        int childId = childrenId.get(i);
                        DiskNode<T> childDiskNode = bpt.getChangeNode(childId);
                        childDiskNode.update(key, obj, bpt);
                        break;
                    }
                }
            }
        } else {
            //根节点
            int index = contain(key);
            if (index < 0) {
                return;
            }
            entries.get(index).setValue(obj);
            //缓存修改的页
            bpt.putChangeNode(id, this);
        }
    }

    /**
     * 找到删除entry的位置
     * @param key
     * @param bpt
     * @param memManager
     */
    public void remove(Comparable key, Bplustree bpt, MemManager<T> memManager) {
        if (leaf) {
            int index = contain(key);
            if (index < 0) {
                //无该结点，直接返回
                return;
            }

            if (root) {
                //即是叶节点也是根结点
                remove(key);
                //缓存
                bpt.putChangeNode(id, this);
            } else {
                //非根结点的叶结点
                if (entries.size() > bpt.getOrder() / 2 &&
                        entries.size() > 2) {
                    remove(key);
                } else {
                    //前驱结点
                    DiskNode<T> prevDiskNode = null;
                    if (prevId != -1) {
                        prevDiskNode = bpt.getChangeNode(prevId);
                    }

                    //后继结点
                    DiskNode<T> nextDiskNode = null;
                    if (nextId != -1) {
                        nextDiskNode = bpt.getChangeNode(nextId);
                    }

                    if (prevId != -1 &&
                            prevDiskNode.getEntries().size() > bpt.getOrder() / 2 &&
                            prevDiskNode.getEntries().size() > 2 &&
                            prevDiskNode.getParentId() == parentId) {
                        //若本节点关键字数量小于M / 2,并且前驱节点字数大于M / 2，则从其借补
                        int prevSize = prevDiskNode.getEntries().size();
                        Map.Entry<Comparable, T> entry = prevDiskNode.getEntries().get(prevSize - 1);
                        prevDiskNode.getEntries().remove(entry);

                        entries.add(0, entry);
                        remove(key);

                        //缓存更新后的结点
                        bpt.putChangeNode(id, this);
                        bpt.putChangeNode(prevDiskNode.getId(), prevDiskNode);
                    } else if (nextId != -1 &&
                            nextDiskNode.getEntries().size() > bpt.getOrder() / 2 &&
                            nextDiskNode.getEntries().size() > 2 &&
                            nextDiskNode.getParentId() == parentId) {
                        //从后继结点借补
                        Map.Entry<Comparable, T> entry = nextDiskNode.getEntries().get(0);
                        nextDiskNode.getEntries().remove(entry);
                        entries.add(entry);
                        remove(key);

                        //缓存更新后的结点
                        bpt.putChangeNode(id, this);
                        bpt.putChangeNode(nextDiskNode.getId(), nextDiskNode);
                    } else {
                        if (prevId != -1 &&
                                (prevDiskNode.getEntries().size() <= bpt.getOrder() / 2 ||
                                prevDiskNode.getEntries().size() <= 2) &&
                                prevDiskNode.getParentId() == parentId) {
                            //同前面的结点结合
                            for (int i = prevDiskNode.getEntries().size() - 1;
                                    i >= 0;i--) {
                                entries.add(0, prevDiskNode.getEntries().get(i));
                            }
                            remove(key);
                            DiskNode<T> diskParentNode = bpt.getChangeNode(parentId);
                            //移除该结点，并在缓存中移除
                            diskParentNode.getChildrenId().remove(prevDiskNode.getId());
                            bpt.removeChangeNode(prevDiskNode.getId());
                            //将其所占位置设置为空间页
                            bpt.addFreeId(prevDiskNode.getId());

                            //更新链表
                            if (prevDiskNode.getPrevId() != -1) {
                                int prepreid = prevDiskNode.getPrevId();
                                DiskNode<T> prepreNode = bpt.getChangeNode(prepreid);
                                prepreNode.setNextId(id);
                                prevId = prepreid;
                                bpt.putChangeNode(prepreid, prepreNode);
                            } else {
                                bpt.setHead(id);
                                prevId = -1;
                            }
                            //缓存改变的结点
                            bpt.putChangeNode(id, this);
                            bpt.putChangeNode(parentId, diskParentNode);
                        } else if (nextId != -1 &&
                                (nextDiskNode.getEntries().size() <= bpt.getOrder() / 2||
                                nextDiskNode.getEntries().size() <= 2) &&
                                nextDiskNode.getParentId() == parentId) {
                            for (int i = 0; i < nextDiskNode.getEntries().size();i++) {
                                entries.add(nextDiskNode.getEntries().get(i));
                            }
                            remove(key);
                            DiskNode<T> diskParentNode = bpt.getChangeNode(parentId);
                            diskParentNode.getChildrenId().remove(nextId);
                            //并将该结点在缓存中删除
                            bpt.removeChangeNode(nextId);
                            //并将该页加入空闲页管理
                            bpt.addFreeId(nextId);

                            //更新链表
                            if (nextDiskNode.getNextId() != -1) {
                                int nextnextId = nextDiskNode.getNextId();
                                DiskNode<T> nextnextNode = bpt.getChangeNode(nextnextId);
                                nextnextNode.setPrevId(id);
                                nextId = nextnextId;
                                bpt.putChangeNode(nextnextId, nextnextNode);
                            }
                            //缓存改变的结点
                            bpt.putChangeNode(id, this);
                            bpt.putChangeNode(parentId, diskParentNode);
                        }
                    }
                }
                DiskNode<T> diskParent = bpt.getChangeNode(parentId);
                diskParent.updateRemove(bpt, memManager);
            }
        } else {
            if (key.compareTo(entries.get(0).getKey()) <= 0) {
                int childId = childrenId.get(0);
                DiskNode<T> diskChildNode = bpt.getChangeNode(childId);
                diskChildNode.remove(key,bpt, memManager);
            } else if (key.compareTo(entries.get(entries.size() - 1).getKey()) >= 0) {
                int childId = childrenId.get(childrenId.size() - 1);
                DiskNode<T> diskChildNode = bpt.getChangeNode(childId);
                diskChildNode.remove(key, bpt, memManager);
            } else {
                for (int i = 0; i < entries.size();i++) {
                    if (entries.get(i).getKey().compareTo(key) <= 0 &&
                            entries.get(i + 1).getKey().compareTo(key) > 0) {
                        int childId = childrenId.get(i);
                        DiskNode<T> diskChildNode = bpt.getChangeNode(childId);
                        diskChildNode.remove(key, bpt, memManager);
                        break;
                    }
                }
            }
        }
    }

    /**
     * 内部节点删除
     * @param bpt
     * @param memManager
     */
    public void updateRemove(Bplustree bpt, MemManager<T> memManager) {
        validate(this, bpt, memManager);

        if (childrenId.size() < bpt.getOrder() / 2 || childrenId.size() < 2) {
            //需要合并节点
            if (root) {
                //根节点
                if (childrenId.size() >= 2) {
                    return;
                } else {
                    //合并,只有一个子节点
                    int rootId = childrenId.get(0);
                    DiskNode<T> diskRoot = bpt.getChangeNode(rootId);
                    bpt.setRoot(rootId);
                    diskRoot.setParentId(-1);
                    diskRoot.setRoot(true);
                    //并将该结点的id添加到空闲页
                    bpt.addFreeId(id);

                    //并将该结点移除缓存区
                    bpt.removeChangeNode(id);
                    bpt.putChangeNode(diskRoot.getId(), diskRoot);

                    setEntries(null);
                    setChildrenId(null);
                }
            } else {
                //非根节点,内部结点不是双向链表，所以要做如下检查
                DiskNode<T> diskParent = bpt.getChangeNode(parentId);
                int currentIndex = diskParent.getChildrenId().indexOf(id);
                int prevIndex = currentIndex - 1;
                int nextIndex = currentIndex + 1;

                DiskNode<T> previous = null, next = null;

                if (prevIndex >= 0) {
                    int prevId = diskParent.getChildrenId().get(prevIndex);
                    previous = bpt.getChangeNode(prevId);
                }

                if (nextIndex < diskParent.getChildrenId().size()) {
                    int nextId = diskParent.getChildrenId().get(nextIndex);
                    next = bpt.getChangeNode(nextId);
                }

                if (previous != null
                        && previous.getChildrenId().size() > bpt.getOrder() / 2
                        && previous.getChildrenId().size() > 2) {
                    //若前置结点的子节点数大于二分之阶数，则旋转结点
                    //获取借来的结点
                    int idx = previous.getChildrenId().size() - 1;
                    int borrowId = previous.getChildrenId().get(idx);
                    DiskNode<T> borrowNode = bpt.getChangeNode(borrowId);
                    previous.getChildrenId().remove(borrowId);
                    borrowNode.setParentId(id);

                    childrenId.add(0,borrowId);
                    validate(previous, bpt, memManager);
                    validate(this, bpt, memManager);

                    //缓存改变的结点
                    bpt.putChangeNode(borrowNode.getId(), borrowNode);
                    bpt.putChangeNode(previous.getId(), previous);
                    bpt.putChangeNode(id, this);
                    diskParent.updateRemove(bpt, memManager);
                } else if (next != null
                        && next.getChildrenId().size() > bpt.getOrder() / 2
                        && next.getChildrenId().size() > 2) {
                    //向后置结点借
                    int borrowId = next.getChildrenId().get(0);
                    DiskNode<T> borrowNode = bpt.getChangeNode(borrowId);
                    next.getChildrenId().remove(borrowId);
                    borrowNode.setParentId(id);

                    childrenId.add(borrowId);
                    validate(next, bpt, memManager);
                    validate(this, bpt, memManager);

                    bpt.putChangeNode(borrowNode.getId(), borrowNode);
                    bpt.putChangeNode(next.getId(), next);
                    bpt.putChangeNode(id, this);
                    diskParent.updateRemove(bpt, memManager);
                } else {
                    //合并结点
                    if (previous != null &&
                            (previous.getChildrenId().size() <= bpt.getOrder() / 2
                            && previous.getChildrenId().size() <= 2)) {
                        //同前面的结点合并
                        for (int i = previous.getChildrenId().size() - 1;i >= 0;i--) {
                            int childId = previous.getChildrenId().get(i);
                            childrenId.add(0, childId);
                            DiskNode<T> diskChildNode = bpt.getChangeNode(childId);
                            diskChildNode.setParentId(id);
                            //缓存更新后的结点
                            bpt.putChangeNode(childId, diskChildNode);
                        }
                        diskParent.getChildrenId().remove(previous.getId());

                        //并在缓存中删除previous这个结点
                        bpt.removeChangeNode(previous.getId());
                        //添加到空间页
                        bpt.addFreeId(previous.getId());
                        //将改变的结点缓存
                        bpt.putChangeNode(id, this);
                        bpt.putChangeNode(diskParent.getId(), diskParent);

                        validate(this, bpt,memManager);
                        diskParent.updateRemove(bpt, memManager);
                    } else if (previous != null &&
                            (next.getChildrenId().size() <= bpt.getOrder() / 2 ||
                            next.getChildrenId().size() <= 2)) {
                        //与后继结点合并
                        for (int i = 0; i < next.getChildrenId().size();i++) {
                            int childId = next.getChildrenId().get(i);
                            DiskNode<T> diskchildNode = bpt.getChangeNode(childId);
                            childrenId.add(childId);
                            diskchildNode.setParentId(id);
                            //缓存更新后的儿子结点
                            bpt.putChangeNode(childId, diskchildNode);
                        }

                        diskParent.getChildrenId().remove(nextId);
                        //在缓存中删除next结点
                        bpt.removeChangeNode(next.getId());
                        //添加到空闲页
                        bpt.addFreeId(next.getId());
                        //缓存变化的结点
                        bpt.putChangeNode(id, this);
                        bpt.putChangeNode(diskParent.getId(), diskParent);
                        validate(this, bpt, memManager);
                        diskParent.updateRemove(bpt, memManager);
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
     * todo 未旋转
     * 寻找entry插入的位置
     * @param key
     * @param obj
     * @param bpt
     * @param memManager
     */
    public void insert(Comparable key, T obj, Bplustree bpt, MemManager<T> memManager) {
        if (leaf) {
            //叶子结点
            if (entries.size() < bpt.getOrder()) {
                //插入条目
                int index = insert(key, obj);
                //缓存
                bpt.putChangeNode(id, this);
                if (parentId != -1 && index == 0) {
                    //非根节点,通过缓存或磁盘获取节点
                    DiskNode<T> diskParentNode = bpt.getChangeNode(parentId);

                    diskParentNode.updateInsert(bpt, memManager);
                }
            } else {
                //分裂
                int rightId = memManager.getNewOrFreeId();
                //左结点的id用本node的id
                DiskNode<T> left = new DiskNode<T>(true,id);
                DiskNode<T> right = new DiskNode<T>(true, rightId);

                if (prevId != -1) {
                    DiskNode<T> prevNode = bpt.getChangeNode(prevId);
                    prevNode.setNextId(id);
                    left.setPrevId(prevId);

                    //缓存
                    bpt.putChangeNode(prevId, prevNode);
                }

                if (nextId != -1) {
                    DiskNode<T> nextNode = bpt.getChangeNode(nextId);
                    nextNode.setPrevId(rightId);
                    right.setNextId(nextId);

                    //缓存
                    bpt.putChangeNode(nextId, nextNode);
                }

                if (prevId == -1) {
                    bpt.setHead(id);
                }
                left.setNextId(rightId);
                right.setPrevId(id);

                int leftSize = (bpt.getOrder() + 1) / 2 + (bpt.getOrder() + 1) % 2;
                int rightSize = (bpt.getOrder() + 1) / 2;

                //将节点先插入该节点，再分配给分裂的两个节点
                insert(key, obj);
                for (int i = 0; i < leftSize; i++) {
                    left.getEntries().add(entries.get(i));
                }
                for (int i = 0; i < rightSize; i++) {
                    right.getEntries().add(entries.get(leftSize + i));
                }

                //非根结点
                if (parentId != -1) {
                    DiskNode<T> diskParentNode = bpt.getChangeNode(parentId);
                    int index = diskParentNode.getChildrenId().indexOf(id);

                    //不用移除本结点，作为左结点的位置
                    left.setParentId(parentId);
                    right.setParentId(parentId);
                    diskParentNode.getChildrenId().add(index + 1,rightId);

                    setEntries(null);
                    setChildrenId(null);

                    //分裂的俩节点缓存,并缓存其父节点
                    bpt.putChangeNode(id, left);
                    bpt.putChangeNode(rightId, right);
                    bpt.putChangeNode(parentId, diskParentNode);

                    diskParentNode.updateInsert(bpt, memManager);
                    setParentId(-1);
                } else {
                    //根节点
                    root = false;

                    int newParentId = memManager.getNewOrFreeId();

                    DiskNode<T> diskParentNode = new DiskNode<T>(false, true, newParentId);
                    left.setParentId(newParentId);
                    right.setParentId(newParentId);

                    //b+树设置新的根结点
                    bpt.setRoot(newParentId);

                    diskParentNode.getChildrenId().add(left.getId());
                    diskParentNode.getChildrenId().add(right.getId());

                    //分裂的俩节点和父节点缓存
                    bpt.putChangeNode(id, left);
                    bpt.putChangeNode(rightId, right);
                    bpt.putChangeNode(newParentId, diskParentNode);

                    setEntries(null);
                    setChildrenId(null);

                    diskParentNode.updateInsert(bpt, memManager);
                }
            }
        } else {
            //非叶结点
            if (key.compareTo(entries.get(0).getKey()) <= 0) {
                int childId = childrenId.get(0);
                DiskNode<T> childNode = bpt.getChangeNode(childId);
                childNode.insert(key, obj, bpt, memManager);
            } else if (key.compareTo(entries.get(entries.size() - 1).getKey()) >= 0) {
                int childId = childrenId.get(0);
                DiskNode<T> childNode = bpt.getChangeNode(childId);
                childNode.insert(key, obj, bpt, memManager);
            } else {
                for (int i = 0; i < entries.size();i++) {
                    if (entries.get(i).getKey().compareTo(key) <= 0
                            && entries.get(i + 1).getKey().compareTo(key) > 0) {
                        int childId = childrenId.get(i);
                        DiskNode<T> childNode = bpt.getChangeNode(childId);
                        childNode.insert(key, obj, bpt, memManager);
                        break;
                    }
                }
            }
        }
    }

    /**
     * 插入具体节点
     * @param key
     * @param obj
     */
    public int insert(Comparable key, T obj) {
        Map.Entry<Comparable, T> entry = new SimpleEntry<Comparable, T>(key,obj);
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
     * todo 未旋转
     * 内部页的更新
     * @param bpt
     * @param memManager
     * @throws Exception
     */
    public void updateInsert(Bplustree bpt, MemManager<T> memManager) {
        validate(this, bpt, memManager);

        if (childrenId.size() > bpt.getOrder()) {
            //分裂，将本身节点在磁盘中的位置给左节点，右节点看空闲页中是否有数据，若没有在添加尾后
            DiskNode<T> left = new DiskNode<T>(false,this.id);
            //获取新的id
            int newId = memManager.getNewOrFreeId();

            DiskNode<T> right = new DiskNode<T>(false, newId);

            int leftSize = (bpt.getOrder() + 1) / 2 + (bpt.getOrder() + 1) % 2;
            int rightSize = (bpt.getOrder() + 1) / 2;
            //将以前一半的子节点放入左节点
            for (int i = 0; i < leftSize; i++) {
                left.getChildrenId().add(childrenId.get(i));
                int childId = childrenId.get(i);
                DiskNode<T> childDiskNode = bpt.getChangeNode(childId);
                Comparable key = childDiskNode.getEntries().get(0).getKey();
                left.getEntries().add(new SimpleEntry<Comparable, T>(key, null));

                childDiskNode.setParentId(this.id);
                bpt.putChangeNode(childId, childDiskNode);
            }

            //后一半的子节点放入右节点
            for (int i = 0; i < rightSize;i++) {
                right.getChildrenId().add(childrenId.get(leftSize + i));
                int childId = childrenId.get(leftSize + i);
                DiskNode<T> childDiskNode = bpt.getChangeNode(childId);
                Comparable key = childDiskNode.getEntries().get(0).getKey();
                right.getEntries().add(new SimpleEntry<Comparable, T>(key, null));

                childDiskNode.setParentId(newId);
                bpt.putChangeNode(childId, childDiskNode);
            }

            if (parentId != -1) {
                //非根结点
                DiskNode<T> parentDiskNode = bpt.getChangeNode(parentId);
                int index = parentDiskNode.getChildrenId().indexOf(id);
                //此处不用移除以前的id，作为left节点的id了
                left.setParentId(parentId);
                right.setParentId(parentId);

                parentDiskNode.getChildrenId().add(index + 1,newId);
                setEntries(null);
                setChildrenId(null);

                //将分裂后的节点和父节点缓存
                bpt.putChangeNode(left.getId(), left);
                bpt.putChangeNode(right.getId(), right);
                bpt.putChangeNode(parentDiskNode.getId(), parentDiskNode);

                parentDiskNode.updateInsert(bpt, memManager);
                setParentId(-1);
            } else {
                //根节点
                root = false;

                int newParentId = memManager.getNewOrFreeId();

                DiskNode<T> diskParentNode = new DiskNode<T>(false, true, newParentId);
                left.setParentId(newParentId);
                right.setParentId(newParentId);

                //b+树设置新的根结点
                bpt.setRoot(newParentId);

                diskParentNode.getChildrenId().add(left.getId());
                diskParentNode.getChildrenId().add(right.getId());

                //将分裂后的节点和新生成的节点缓存
                bpt.putChangeNode(newParentId, diskParentNode);
                bpt.putChangeNode(left.getId(), left);
                bpt.putChangeNode(right.getId(), right);

                setEntries(null);
                setChildrenId(null);
            }
        }
    }

    /**
     * todo 此处写的不好，太多的磁盘io，后期想方法优化
     * 调整关键字节点
     * @param diskNode
     * @param bpt
     */
    public void validate(DiskNode<T> diskNode, Bplustree bpt, MemManager<T> memManager) {
        if (diskNode.getEntries().size() == diskNode.getChildrenId().size()) {
            //关键字节点和节点数目相同
            List<Map.Entry<Comparable, T>> entries = diskNode.getEntries();
            for (int i = 0; i < entries.size();i++) {
                int childrenId = diskNode.getChildrenId().get(i);
                //获取磁盘上的子节点
                DiskNode<T> dnode = bpt.getChangeNode(childrenId);
                Comparable key = dnode.getEntries().get(0).getKey();
                if (diskNode.getEntries().get(i).getKey().compareTo(key) != 0) {
                    diskNode.getEntries().remove(i);
                    diskNode.getEntries().add(i, new SimpleEntry<Comparable, T>(key, null));
                }
            }
            bpt.putChangeNode(diskNode.getId(), diskNode);

            if (!diskNode.root){
                int parentId = diskNode.getParentId();
                DiskNode<T> parentNode = bpt.getChangeNode(parentId);
                validate(parentNode, bpt, memManager);
            }
        } else if (diskNode.root && diskNode.getChildrenId().size() >= 2 ||
                diskNode.getChildrenId().size() >= bpt.getOrder() / 2 &&
                diskNode.getChildrenId().size() <= bpt.getOrder() &&
                diskNode.getChildrenId().size() >= 2) {
                //若子节点数不等于关键字个数但仍大于M/2并且小于M，并且大于2
            diskNode.entries.clear();
            for (int i = 0; i < diskNode.getChildrenId().size();i++) {
                int childrenId = diskNode.getChildrenId().get(i);
                //获取磁盘上的子节点
                DiskNode<T> dnode = memManager.getPageById(childrenId);
                Comparable key = dnode.getEntries().get(0).getKey();
                diskNode.getEntries().add(new SimpleEntry<Comparable, T>(key,null));
            }

            //缓存
            bpt.putChangeNode(diskNode.getId(), diskNode);
            if (!diskNode.root) {
                int parentId = diskNode.getParentId();
                DiskNode<T> parentNode = bpt.getChangeNode(parentId);
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
