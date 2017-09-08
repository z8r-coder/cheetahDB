package utils;

import support.logging.Log;
import support.logging.LogFactory;

import java.util.Map;

/**
 * Created by ruanxin on 2017/8/13.
 */
public class MapUtils {

    private final static Log logger = LogFactory.getLog(MapUtils.class);

    /**
     * null?
     * @param map
     * @return
     */
    public static boolean isEmpty(Map map) {
        if (map == null) {
            return true;
        }
        return false;
    }

}
