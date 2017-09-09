package utils;

import support.logging.Log;
import support.logging.LogFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 配置文件读取(EngineConfig)
 * Created by rx on 2017/8/30.
 */
public class ConfigUtils {
    private final static Log LOG = LogFactory.getLog(ConfigUtils.class);
    private String pageSize;
    private String dataPath;
    private String absolutePath;
    private String cacheSize;

    private String capacitySmall;
    private String capacityMedium;
    private String capacityLarge;

    private String freeBlockSmall;
    private String freeBlockMedium;
    private String freeBlockLarge;

    private String socketSize;
    private String serverPort;
    private static ConfigUtils config;
    private Properties properties;


    public static ConfigUtils getConfig() {
        if (config == null) {
            config = new ConfigUtils();
        }
        return config;
    }

    // TODO: 2017/9/9 可分开配置，目前需要配置的为 engine config and server config
    public void loadPropertiesFromSrc() {
        InputStream in = null;

        try {
            in = ConfigUtils.class.getClassLoader().getResourceAsStream("EngineConfig.properties");
            if (in != null) {
                this.properties = new Properties();
                this.properties.load(in);
            }
            loadProperties(properties);
        } catch (Exception e) {
            LOG.error("Read properties Error", e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void loadProperties(Properties pro) {
        String PAGE_CAPACITY = pro.getProperty("page-capacity");
        if (!StringUtils.isBlank(PAGE_CAPACITY)) {
            this.pageSize = PAGE_CAPACITY.trim();
        }

        String DATA_PATH = pro.getProperty("data-path");
        if (!StringUtils.isBlank(DATA_PATH)) {
            this.dataPath = DATA_PATH.trim();
        }

        String ABSOLUTE_PATH = pro.getProperty("absolute-path");
        if (!StringUtils.isBlank(ABSOLUTE_PATH)) {
            this.absolutePath = ABSOLUTE_PATH.trim();
        }

        String CACHE_SIZE = pro.getProperty("cache-size");
        if (!StringUtils.isBlank(CACHE_SIZE)) {
            this.cacheSize = CACHE_SIZE;
        }

        String CAPACITY_SMALL = pro.getProperty("capacity-small");
        if (!StringUtils.isBlank(CAPACITY_SMALL)) {
            this.capacitySmall = CAPACITY_SMALL;
        }

        String CAPACITY_MEDIUM = pro.getProperty("capacity-medium");
        if (!StringUtils.isBlank(CAPACITY_MEDIUM)) {
            this.capacityMedium = CAPACITY_MEDIUM;
        }

        String CAPACITY_LARGE = pro.getProperty("capacity-large");
        if (!StringUtils.isBlank(CAPACITY_LARGE)) {
            this.capacityLarge = CAPACITY_LARGE;
        }

        String FREE_BLOCK_SMALL = pro.getProperty("free-block-small");
        if (!StringUtils.isBlank(FREE_BLOCK_SMALL)) {
            this.freeBlockSmall = FREE_BLOCK_SMALL;
        }

        String FREE_BLOCK_MEDIUM = pro.getProperty("free-block-medium");
        if (!StringUtils.isBlank(FREE_BLOCK_MEDIUM)) {
            this.freeBlockMedium = FREE_BLOCK_MEDIUM;
        }

        String FREE_BLOCK_LARGE = pro.getProperty("free-block-large");
        if (!StringUtils.isBlank(FREE_BLOCK_LARGE)) {
            this.freeBlockLarge = FREE_BLOCK_LARGE;
        }

        String SOCKET_SIZE = pro.getProperty("socket-size");
        if (!StringUtils.isBlank(SOCKET_SIZE)) {
            this.socketSize = SOCKET_SIZE;
        }

        String SERVER_PORT = pro.getProperty("server-port");
        if (!StringUtils.isBlank(SERVER_PORT)) {
            this.serverPort = SERVER_PORT;
        }
    }

    public void setPageSize(String pageSize) {
        this.pageSize = pageSize;
    }

    public String getPageSize() {
        return pageSize;
    }

    public void setDataPath(String dataPath) {
        this.dataPath = dataPath;
    }

    public String getDataPath() {
        return dataPath;
    }

    public void setAbsolutePath(String absolutePath) {
        this.absolutePath = absolutePath;
    }

    public String getAbsolutePath() {
        return absolutePath;
    }

    public void setCacheSize(String cacheSize) {
        this.cacheSize = cacheSize;
    }

    public String getCacheSize() {
        return cacheSize;
    }

    public String getCapacitySmall() {
        return capacitySmall;
    }

    public void setCapacitySmall(String capacitySmall) {
        this.capacitySmall = capacitySmall;
    }

    public String getCapacityMedium() {
        return capacityMedium;
    }

    public void setCapacityMedium(String capacityMedium) {
        this.capacityMedium = capacityMedium;
    }

    public String getCapacityLarge() {
        return capacityLarge;
    }

    public void setCapacityLarge(String capacityLarge) {
        this.capacityLarge = capacityLarge;
    }

    public String getFreeBlockSmall() {
        return freeBlockSmall;
    }

    public void setFreeBlockSmall(String freeBlockSmall) {
        this.freeBlockSmall = freeBlockSmall;
    }

    public String getFreeBlockMedium() {
        return freeBlockMedium;
    }

    public void setFreeBlockMedium(String freeBlockMedium) {
        this.freeBlockMedium = freeBlockMedium;
    }

    public String getFreeBlockLarge() {
        return freeBlockLarge;
    }

    public void setFreeBlockLarge(String freeBlockLarge) {
        this.freeBlockLarge = freeBlockLarge;
    }

    public String getSocketSize() {
        return socketSize;
    }

    public void setSocketSize(String socketSize) {
        this.socketSize = socketSize;
    }

    public void setServerPort(String serverPort) {
        this.serverPort = serverPort;
    }

    public String getServerPort() {
        return serverPort;
    }

    public static void main(String args[]) {
        //加载配置文件
        ConfigUtils.getConfig().loadPropertiesFromSrc();

        System.out.println(ConfigUtils.getConfig().getPageSize());

        System.out.println(ConfigUtils.getConfig().getCacheSize());

        System.out.println(ConfigUtils.getConfig().getCapacitySmall());

        System.out.println(ConfigUtils.getConfig().getCapacityMedium());

        System.out.println(ConfigUtils.getConfig().getCapacityLarge());

        System.out.println(ConfigUtils.getConfig().getFreeBlockSmall());

        System.out.println(ConfigUtils.getConfig().getFreeBlockMedium());

        System.out.println(ConfigUtils.getConfig().getFreeBlockLarge());

        System.out.println(ConfigUtils.getConfig().getSocketSize());
    }
}
