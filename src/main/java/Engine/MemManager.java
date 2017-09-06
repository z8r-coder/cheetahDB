package Engine;

import Engine.DiskBplusTree.DiskNode;
import Engine.MemBPlusTree.Node;
import FileStore.Code.CodeUtils;
import Support.Logging.Log;
import Support.Logging.LogFactory;
import Support.Manager.Manager;
import Utils.ConfigUtils;
import Utils.DateUtils;
import sun.security.krb5.Config;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.*;

/**
 * table Name    keep
 * |---20b---|----10b---|----pageSize----|more
 * 内存管理器,不同表对应不同的内存管理器
 * Created by rx on 2017/9/4.
 */
public class MemManager<T> {
    /**
     * 写入db文件路径
     */
    private final static String FILE_PATH;
    /**
     * 页大小
     */
    private final static int PAGE_SIZE;

    /**
     * 缓存大小，页数量
     */
    private final static int CACHE_SIZE;

    /**
     * 每个表对应的管理器
     */
    private static Map<String, MemManager> tableMemManager = new HashMap<String, MemManager>();
    static {
        ConfigUtils.getConfig().loadPropertiesFromSrc();
        PAGE_SIZE = Integer.parseInt(ConfigUtils.getConfig().getPageSize());

        FILE_PATH = ConfigUtils.getConfig().getAbsolutePath();

        CACHE_SIZE = Integer.parseInt(ConfigUtils.getConfig().getCacheSize());
    }

    /**
     * 页缓存
     */
    private Map<Long,CachePage> cacheMap = new HashMap<Long, CachePage>(PAGE_SIZE);
    /**
     * 表名
     */
    private String tableName;

    /**
     * 磁盘空间目前的最大值
     */
    private long MAX_SIZE;

    /**
     * 空闲页管理
     */
    private List<Long> freePage = new LinkedList<Long>();

    private Log log = LogFactory.getLog(MemManager.class);

    public MemManager (String tableName) {
        this.tableName = tableName;
        tableMemManager.put(tableName, this);
    }

    /**
     * 添加一个空闲页
     * @param freeId
     */
    public void addFreePage(Long freeId) {
        freePage.add(freeId);
    }

    /**
     * 移除最后一个空闲页
     * 若为空，返回-1
     * @return
     */
    public long removeFreePage() {
        int freePageSize = freePage.size();
        if (freePageSize == 0) {
            return -1;
        }
        Long retId = freePage.get(freePageSize - 1);
        freePage.remove(freePageSize - 1);
        return retId;
    }
    
    /**
     * 通过id从磁盘中获取数据页
     * 缓存算法用的LRU
     * @param id
     * @return
     */
    public DiskNode<T> getPageById(long id) {
        if (cacheMap.get(id) == null) {
            //缓存页中没找到，从磁盘中读取
            try {
                DiskNode diskNode = readFromDisk(id);
                if (cacheMap.size() < CACHE_SIZE) {
                    //缓存未满直接存入
                    Long dateTime = Long.parseLong(DateUtils.convertDate2Str(new Date(), DateUtils.TIME_CACHE_FORMAT));
                    CachePage cachePage = new CachePage(dateTime, diskNode);
                    cacheMap.put(id, cachePage);
                } else {
                    //如果缓存满了，扔掉时间戳最小的那个
                    // TODO: 2017/9/6 待优化，可加个负载因子
                    long min_tm = Long.parseLong(DateUtils.MAX_TIME_STAMP);
                    long min_id = -1;
                    for (long removeid : cacheMap.keySet()) {
                        CachePage cachePage = cacheMap.get(removeid);
                        if (cachePage.timestamp < min_tm) {
                            min_id = cachePage.cacheNode.getId();
                            min_tm = removeid;
                        }
                    }
                    //移除掉最少的
                    cacheMap.remove(min_id);
                    //将新的放进去
                    Long dateTime = Long.parseLong(DateUtils.convertDate2Str(new Date(), DateUtils.TIME_CACHE_FORMAT));
                    CachePage cachePage = new CachePage(dateTime, diskNode);
                    cacheMap.put(id, cachePage);
                }
                return diskNode;
            } catch (Exception e) {
                log.error("getPageById ERROR!", e);
            }
        }
        //在缓存中找到
        CachePage cachePage = cacheMap.get(id);
        //更新时间戳
        cachePage.timestamp = Long.parseLong(DateUtils.convertDate2Str(new Date(), DateUtils.TIME_CACHE_FORMAT));
        return cachePage.cacheNode;
    }

    static class CachePage<T> {
        //时间戳
        long timestamp;
        DiskNode<T> cacheNode;

        public CachePage(long timestamp, DiskNode<T> cacheNode) {
            this.cacheNode = cacheNode;
            this.timestamp = timestamp;
        }
    }

    public void setCacheMap(Map<Long, CachePage> cacheMap) {
        this.cacheMap = cacheMap;
    }

    public Map<Long, CachePage> getCacheMap() {
        return cacheMap;
    }

    public boolean writeToDisk(long id, DiskNode<T> diskNode) throws Exception {
        long position = 31 + id * PAGE_SIZE;
        //终止位置，不足0补齐
        long end = 30 + (id + 1) * PAGE_SIZE;
        ByteBuffer writeBuf = ByteBuffer.allocate(PAGE_SIZE);
        //将数据页编码存入buf
        CodeUtils.encode(diskNode, writeBuf);

        FileChannel outChannel = null;
        try {
            RandomAccessFile afile = new RandomAccessFile(FILE_PATH
                    + tableName + "_indexFile.db", "rw");
            outChannel = afile.getChannel();
            outChannel.position(position);

            writeBuf.flip();
            while(writeBuf.hasRemaining()) {
                outChannel.write(writeBuf);
            }

            //不足的0补齐
            if (outChannel.position() < end) {
                int startPos = (int) (end - outChannel.position());
                ByteBuffer zeroBuffer = ByteBuffer.allocate(startPos);
                outChannel.position(startPos + 1);
                for (int i = 0; i < zeroBuffer.capacity();i++) {
                    zeroBuffer.put((byte) 0);
                }
                //将补齐的零写入磁盘

                while (zeroBuffer.hasRemaining()) {
                    outChannel.write(zeroBuffer);
                }
            }
            return true;
        } catch (FileNotFoundException e) {
            throw new Exception(e);
        } catch (IOException e) {
            throw new Exception(e);
        } finally {
            if (outChannel != null) {
                outChannel.close();
            }
        }
    }
    private DiskNode<T> readFromDisk(long id) throws Exception {
        //该结点的起始位置,1开始
        long positon = 31 + id * PAGE_SIZE;

        ByteBuffer readBuf = ByteBuffer.allocate(PAGE_SIZE);
        FileChannel inchannel = null;
        try {
            RandomAccessFile afile = new RandomAccessFile(FILE_PATH
                    + tableName + "_indexFile.db", "rw");
            inchannel = afile.getChannel();
            inchannel.position(positon);
            inchannel.truncate(positon + PAGE_SIZE - 1);

            int bytesRead = inchannel.read(readBuf);

            if (bytesRead < 0) {
                //啥都没读到
                log.error("start:" + positon + "end:" + (positon + PAGE_SIZE - 1));
                throw new Exception("nothing read");
            }
        } catch (FileNotFoundException e) {
            throw new Exception(e);
        } catch (IOException e) {
            throw new Exception(e);
        } finally {
            if (inchannel != null) {
                inchannel.close();
            }
        }
        DiskNode<T> diskNode = (DiskNode<T>) CodeUtils.decode(readBuf);

        return diskNode;
    }

    public void setMAX_SIZE(long MAX_SIZE) {
        this.MAX_SIZE = MAX_SIZE;
    }

    public long getMAX_SIZE() {
        return MAX_SIZE;
    }

    public static MemManager getTableMemManager(String tableName) {
        return tableMemManager.get(tableName);
    }

    public static void main(String arg[]) {
        System.out.println(System.currentTimeMillis());
    }
}
