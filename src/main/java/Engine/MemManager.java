package Engine;

import Engine.MemBPlusTree.Node;
import Support.Manager.Manager;

import java.nio.ByteBuffer;

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
     * 内存池
     */
    private static ByteBuffer bufferPool = ByteBuffer.allocate(100000000);

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
    public Node<T> getPageById(long id) {
        
    }
}
