package engine.fulltextindex;

import engine.MemManager;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 全文搜索管理器
 * Created by rx on 2017/9/11.
 */
public class FullIndexManager {
    /**
     * 管理未满的页
     */
    private Map<Long, Integer> diffToFull;

    /**
     * 管理空闲页
     */
    private List<Long> freePage = new LinkedList<Long>();

    /**
     * 每个表对应一个全局搜索管理器
     */
    private static Map<String, FullIndexManager> tableMemManager = new HashMap<String, FullIndexManager>();

    /**
     * 表明
     */
    private String tableName;
    /**
     * 缓存 LRU
     */
    private Map<Long, FullIndexCachePage> cachePageMap = new HashMap<Long, FullIndexCachePage>();

    public FullIndexManager(String tableName) {
        this.tableName = tableName;
        tableMemManager.put(tableName, this);
    }

    static class FullIndexCachePage {
        long timestamp;
        TextPage cachePage;

        public FullIndexCachePage(long timestamp, TextPage cachePage) {
            this.timestamp = timestamp;
            this.cachePage = cachePage;
        }
    }
}
