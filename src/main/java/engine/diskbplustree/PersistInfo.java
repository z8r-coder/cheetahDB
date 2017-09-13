package engine.diskbplustree;

import java.io.Serializable;
import java.util.List;

/**
 * 持久化信息（除了节点信息外，需要持久化的信息）
 * Created by rx on 2017/9/12.
 */
public class PersistInfo implements Serializable {
    /**
     * 根节点位置
     */
    private int rootId;

    /**
     * 空闲页
     */
    private List<Integer> freePage;

    /**
     * 链表头节点
     */
    private int headId;

    /**
     * 阶数
     */
    private int order;

    /**
     * 目前磁盘所占最大值
     */
    private int MAX_SIZE;

    /**
     * 目前磁盘所占最大页号
     */
    private int MAX_ID;

    public int getRootId() {
        return rootId;
    }

    public void setRootId(int rootId) {
        this.rootId = rootId;
    }

    public List<Integer> getFreePage() {
        return freePage;
    }

    public void setFreePage(List<Integer> freePage) {
        this.freePage = freePage;
    }

    public int getHeadId() {
        return headId;
    }

    public void setHeadId(int headId) {
        this.headId = headId;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public int getMAX_SIZE() {
        return MAX_SIZE;
    }

    public void setMAX_SIZE(int MAX_SIZE) {
        this.MAX_SIZE = MAX_SIZE;
    }

    public int getMAX_ID() {
        return MAX_ID;
    }

    public void setMAX_ID(int MAX_ID) {
        this.MAX_ID = MAX_ID;
    }
}
