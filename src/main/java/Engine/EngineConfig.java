package Engine;

import Support.Logging.Log;
import Support.Logging.LogFactory;
import Utils.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 引擎配置文件读取
 * Created by rx on 2017/8/30.
 */
public class EngineConfig {
    private final static Log LOG = LogFactory.getLog(EngineConfig.class);
    private String pageSize;

    private static EngineConfig config;
    private Properties properties;


    public static EngineConfig getConfig() {
        if (config == null) {
            config = new EngineConfig();
        }
        return config;
    }

    public void loadPropertiesFromSrc() {
        InputStream in = null;

        try {
            in = EngineConfig.class.getClassLoader().getResourceAsStream("EngineConfig.properties");
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
        String value = pro.getProperty("page-capacity");
        if (!StringUtils.isBlank(value)) {
            this.pageSize = value.trim();
        }
    }

    public void setPageSize(String pageSize) {
        this.pageSize = pageSize;
    }

    public String getPageSize() {
        return pageSize;
    }
}
