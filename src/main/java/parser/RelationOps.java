package parser;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by roy on 2017/7/30.
 */
public class RelationOps {
    static Map<String, SortCode> map = new HashMap<String, SortCode>();

    static {
        map.put("<", SortCode.LT);
        map.put(">", SortCode.GT);
        map.put("!=", SortCode.NEQ);
        map.put(">=", SortCode.GTET);
        map.put("<=", SortCode.LTET);
        map.put("=", SortCode.EQ);
    }

    public static boolean containValue(String key) {
        if (map == null) {
            return false;
        }else {
            return map.containsKey(key);
        }
    }
}
