package Engine;

import Engine.DiskBplusTree.DiskNode;
import Engine.MemBPlusTree.Node;
import Support.Manager.Manager;
import Utils.ConfigUtils;
import sun.security.krb5.Config;

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
     * 页缓存
     */
    private Map<Long,CachePage> cacheMap;

    public static MemManager getMemManager() {
        if (memManager == null) {
            ConfigUtils.getConfig().loadPropertiesFromSrc();
            String cachePageSize = ConfigUtils.getConfig().getCachePage();
            int pageSize = Integer.parseInt(cachePageSize);
            Map<Long, CachePage> cachePageMap = new HashMap<Long, CachePage>(pageSize);

            memManager = new MemManager();
            memManager.setCacheMap(cachePageMap);
            return memManager;
        }
        return memManager;
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

    static class CachePage {
        //时间戳
        long timestamp;
        DiskNode cacheNode;
    }

    public void setCacheMap(Map<Long, CachePage> cacheMap) {
        this.cacheMap = cacheMap;
    }

    public Map<Long, CachePage> getCacheMap() {
        return cacheMap;
    }

//    private Object readFromDisk(long id) {
//
//    }

    public static void main(String arg[]) {
        System.out.println(System.currentTimeMillis());
    }
}
