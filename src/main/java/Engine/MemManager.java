package Engine;

import Engine.DiskBplusTree.DiskNode;
import Engine.MemBPlusTree.Node;
import Support.Manager.Manager;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

/**
 * table Name    keep
 * |---20b---|----10b---|----pageSize----|more
 * 内存管理器
 * Created by rx on 2017/9/4.
 */
public class MemManager<T> {

    /**
     * 内存管理器
     */
    private static MemManager memManager;

    /**
     * 内存池,frame
     */
    private static ByteBuffer bufferPool = ByteBuffer.allocate(100000000);

    /**
     * 记录页在缓存池位置
     */
    private Map<Long,Position> cacheMap = new HashMap<Long, Position>();

    public static MemManager getMemManager() {
        if (memManager == null) {
            memManager = new MemManager();
        }
        return memManager;
    }

    /**
     * 获取缓存池
     * @return
     */
    public static ByteBuffer getBufferPool() {
        return bufferPool;
    }

    /**
     * 通过id从磁盘中获取数据页
     * @param id
     * @return
     */
    public DiskNode<T> getPageById(long id) {
        if (cacheMap.get(id) == null) {
            //缓存页中没找到
        }
        return null;
    }

    static class Position {
        int start;
        int end;
    }
}
