package filestore;

import utils.ConfigUtils;

/**
 * Created by rx on 2017/9/2.
 */
public class DataSource {
    public static void main(String args[]) {
        ConfigUtils.getConfig().loadPropertiesFromSrc();

        System.out.println(ConfigUtils.getConfig().getAbsolutePath());
    }
}
