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

    private static ConfigUtils config;
    private Properties properties;


    public static ConfigUtils getConfig() {
        if (config == null) {
            config = new ConfigUtils();
        }
        return config;
    }

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

    public static void main(String args[]) {
        //加载配置文件
        ConfigUtils.getConfig().loadPropertiesFromSrc();

        System.out.println(ConfigUtils.getConfig().getPageSize());

        System.out.println(ConfigUtils.getConfig().getCacheSize());
    }
}
