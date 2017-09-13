package engine;

import engine.diskbplustree.DiskBplusTree;
import engine.diskbplustree.DiskNode;
import filestore.code.CodeUtils;
import support.logging.Log;
import support.logging.LogFactory;
import utils.CheckUtils;
import utils.ConfigUtils;
import utils.DateUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.*;

/**
 *      B+tree
 * |----1024-------|----pageSize----|more
 * 内存管理器,不同表对应不同的内存管理器
 * Created by rx on 2017/9/4.
 */
public class MemManager<T> {
    /**
     * 写入db文件路径
     */
    private final static String FILE_PATH;
    /**
     * 页大小,4096
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
    private int MAX_SIZE;

    /**
     * 磁盘空间逻辑页数
     */
    private int MAX_ID;

    /**
     * 空闲页管理
     */
    private List<Integer> freePage = new LinkedList<Integer>();

    private Log log = LogFactory.getLog(MemManager.class);

    public MemManager (String tableName) {
        this.tableName = tableName;
        tableMemManager.put(tableName, this);
    }

    /**
     * 添加一个空闲页
     * @param freeId
     */
    public void addFreePage(Integer freeId) {
        freePage.add(freeId);
    }

    /**
     * 移除最后一个空闲页,并将其返回
     * 若为空，返回-1
     * @return
     */
    public int removeAndRetFreePage() {
        int freePageSize = freePage.size();
        if (freePageSize == 0) {
            return -1;
        }
        Integer retId = freePage.get(freePageSize - 1);
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
                //缓存磁盘页
                putNodeToCache(id, diskNode);
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

    /**
     * 缓存
     * LRU算法
     * @param id
     * @param diskNode
     */
    public void putNodeToCache(long id, DiskNode<T> diskNode) {
        if (cacheMap.size() < CACHE_SIZE) {
            //缓存未满
            Long dateTime = Long.parseLong(DateUtils.convertDate2Str(new Date(), DateUtils.TIME_CACHE_FORMAT));
            cacheMap.put(id, new CachePage(dateTime, diskNode));
        } else {
            //如果缓存满了，扔掉时间戳最小的那个
            // TODO: 2017/9/6 待优化，可加个负载因子
            // TODO: 2017/9/12 可维护个最小堆
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
    }

    public void writeBptToDisk(Bplustree<T,Long> bpt) throws Exception {
        int position = 1;
        int end = 1024;
        ByteBuffer writeBuf = ByteBuffer.allocate(1024);
        FileChannel outchannel = null;

        try {
            RandomAccessFile afile = new RandomAccessFile(FILE_PATH
                    + tableName + "_indexFile.db", "rw");
            outchannel = afile.getChannel();

            writeBuf.flip();
            //设置可读区间，不足0补齐
            writeBuf.position(position);
            writeBuf.limit(end);

            while(writeBuf.hasRemaining()) {
                outchannel.write(writeBuf);
            }
        } catch (FileNotFoundException e) {
            throw new Exception(e);
        } catch (IOException e) {
            throw new Exception(e);
        } finally {
            if (outchannel != null) {
                try {
                    outchannel.close();
                } catch (IOException e) {
                    throw new Exception(e);
                }
            }
        }
    }
    /**
     * 从磁盘中读取b+树
     * @return
     * @throws Exception
     */
    public Bplustree<T, Integer> readBptFromDisk() throws Exception {
        int positon = 1;
        int end = 1024;
        ByteBuffer readBuf = ByteBuffer.allocate(1024);
        FileChannel inchannel = null;

        try {
            RandomAccessFile afile = new RandomAccessFile(FILE_PATH
                    + tableName + "_indexFile.db", "rw");
            inchannel = afile.getChannel();
            //设置写区间，不足0补齐
            readBuf.position(positon);
            readBuf.limit(end);
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
                try {
                    inchannel.close();
                } catch (IOException e) {
                    throw new Exception(e);
                }
            }
        }
        Bplustree<T, Integer> diskBpt = (DiskBplusTree<T>)CodeUtils.decode(readBuf);
        return diskBpt;
    }

    /**
     * 用于批量写入磁盘，减少IO次数
     * @param cacheMap
     */
    public void batchWriteToDisk(Map<Integer, DiskNode<T>> cacheMap) throws Exception {
        ByteBuffer batchWriteBuf = ByteBuffer.allocate(PAGE_SIZE);
        FileChannel outChannel = null;

        try {
            RandomAccessFile afile = new RandomAccessFile(FILE_PATH
                    + tableName + "_indexFile.db", "rw");
            outChannel = afile.getChannel();
            for (Integer id : cacheMap.keySet()) {
                DiskNode<T> diskNode = cacheMap.get(id);
                //检查每页大小
                int diskNodeBytesLen = CodeUtils.getBytesArrLength(diskNode);
                CheckUtils.state(diskNodeBytesLen > 4000,
                        "the page is too big!", diskNode);

                //编码
                CodeUtils.encode(diskNode, batchWriteBuf);
                int pos = 1025 + PAGE_SIZE * id;

                // TODO: 2017/9/12 测试的时候看看该行能否去掉
                batchWriteBuf.flip();
                batchWriteBuf.position(0);
                batchWriteBuf.limit(PAGE_SIZE - 1);

                outChannel.position(pos);
                while (batchWriteBuf.hasRemaining()) {
                    outChannel.write(batchWriteBuf);
                }

                batchWriteBuf.clear();
            }
        } catch (FileNotFoundException e) {
            throw new Exception(e);
        } catch (IOException e) {
            throw new Exception(e);
        } finally {
            if (outChannel != null) {
                try {
                    outChannel.close();
                } catch (IOException e) {
                    throw new Exception(e);
                }
            }
        }
    }

    /**
     * 将diskNode写入磁盘
     * @param id
     * @param diskNode
     * @return
     * @throws Exception
     */
    public boolean writeToDisk(int id, DiskNode<T> diskNode) throws Exception {
        int position = 1025 + id * PAGE_SIZE;

        ByteBuffer writeBuf = ByteBuffer.allocate(PAGE_SIZE);

        //检查页大小
        int diskNodeBytesLen = CodeUtils.getBytesArrLength(diskNode);
        CheckUtils.state(diskNodeBytesLen > 4000,
                "the page is too big!", diskNode);
        //将数据页编码存入buf
        CodeUtils.encode(diskNode, writeBuf);

        FileChannel outChannel = null;
        try {
            RandomAccessFile afile = new RandomAccessFile(FILE_PATH
                    + tableName + "_indexFile.db", "rw");
            outChannel = afile.getChannel();
            outChannel.position(position);

            writeBuf.flip();
            writeBuf.position(0);
            //不足之处0补齐
            writeBuf.limit(PAGE_SIZE - 1);
            while(writeBuf.hasRemaining()) {
                outChannel.write(writeBuf);
            }
            //缓存该结点
            putNodeToCache(id, diskNode);
            return true;
        } catch (FileNotFoundException e) {
            throw new Exception(e);
        } catch (IOException e) {
            throw new Exception(e);
        } finally {
            if (outChannel != null) {
                try {
                    outChannel.close();
                } catch (IOException e) {
                    throw new Exception(e);
                }
            }
        }
    }

    /**
     * 用于批量读出节点数据
     * @param ids
     * @return
     */
//    public List<DiskNode<T>> batchReadFromDisk(List<Long> ids) {
//
//    }
    /**
     * 从磁盘中读取节点
     * @param id
     * @return
     * @throws Exception
     */
    private DiskNode<T> readFromDisk(long id) throws Exception {
        //该结点的起始位置,1开始
        long positon = 1025 + id * PAGE_SIZE;

        ByteBuffer readBuf = ByteBuffer.allocate(PAGE_SIZE);
        FileChannel inchannel = null;
        try {
            RandomAccessFile afile = new RandomAccessFile(FILE_PATH
                    + tableName + "_indexFile.db", "rw");
            inchannel = afile.getChannel();
            inchannel.position(positon);

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

    public void setMAX_SIZE(int MAX_SIZE) {
        this.MAX_SIZE = MAX_SIZE;
    }

    public int getMAX_SIZE() {
        return MAX_SIZE;
    }

    public void setMAX_ID(int MAX_ID) {
        this.MAX_ID = MAX_ID;
    }

    public int getMAX_ID() {
        return MAX_ID;
    }

    public static MemManager getTableMemManager(String tableName) {
        return tableMemManager.get(tableName);
    }

    /**
     * 分裂后磁盘长度添加一页
     * 由于我们使用空闲页管理，所以不设置减少
     */
    public void addMaxSize() {
        MAX_SIZE = MAX_SIZE + PAGE_SIZE;
    }

    /**
     * 分裂后磁盘逻辑页数增加1,并返回添加后的逻辑页
     * 该值不会减少
     */
    public int addAndRetMaxId() {
        addMaxSize();
        return ++MAX_ID;
    }

    /**
     * 获取一个新或空间的叶
     * @return
     */
    public Integer getNewOrFreeId() {
        if (freePageSize() > 0) {
            //还有空间页
            return removeAndRetFreePage();
        } else {
            //增加id
            return addAndRetMaxId();
        }
    }
    /**
     * 返回空闲页的数量
     * @return
     */
    public int freePageSize () {
        return freePage.size();
    }

    /**
     * 获取空闲页列表
     * @return
     */
    public List<Integer> getFreePage() {
        return freePage;
    }

    public static void main(String arg[]) {
        //System.out.println(System.currentTimeMillis());
        String teststr = null;
        teststr = "0";
        System.out.println(teststr);
    }
}
