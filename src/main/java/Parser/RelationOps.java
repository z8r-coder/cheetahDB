package Parser;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by roy on 2017/7/30.
 */
public class RelationOps {
    static Map<String, SortCode> map;

    //获取关系符号表
    public static Map<String, SortCode> getRSMap() {
        if (map == null) {
            map = new HashMap<String, SortCode>();
            map.put("<", SortCode.LT);
            map.put(">", SortCode.GT);
            map.put("!=", SortCode.NEQ);
            map.put(">=", SortCode.GTET);
            map.put("<=", SortCode.LTET);
            map.put("=", SortCode.EQ);
            return map;
        }
        return map;
    }

    public static boolean containValue(String key) {
        if (map == null) {
            return false;
        }else {
            return map.containsKey(key);
        }
    }
}
