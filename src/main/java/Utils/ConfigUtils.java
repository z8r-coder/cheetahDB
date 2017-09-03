package Utils;

import Support.Logging.Log;
import Support.Logging.LogFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 配置文件读取
 * Created by rx on 2017/8/30.
 */
public class ConfigUtils {
    private final static Log LOG = LogFactory.getLog(ConfigUtils.class);
    private String pageSize;
    private String bufferSize;

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

    public void loadProperties(Properties pro) {
        String PAGE_CAPACITY = pro.getProperty("page-capacity");
        if (!StringUtils.isBlank(PAGE_CAPACITY)) {
            this.pageSize = PAGE_CAPACITY.trim();
        }

        String BUFFER_SIZE = pro.getProperty("buffer-size");
        if (!StringUtils.isBlank(BUFFER_SIZE)) {
            this.bufferSize = BUFFER_SIZE.trim();
        }
    }

    public void setPageSize(String pageSize) {
        this.pageSize = pageSize;
    }

    public String getPageSize() {
        return pageSize;
    }

    public void setBufferSize(String bufferSize) {
        this.bufferSize = bufferSize;
    }

    public String getBufferSize() {
        return bufferSize;
    }

    public static void main(String args[]) {
        //加载配置文件
        ConfigUtils.getConfig().loadPropertiesFromSrc();

        System.out.println(ConfigUtils.getConfig().getPageSize());

        System.out.println(ConfigUtils.getConfig().getBufferSize());
    }
}
