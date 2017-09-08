package parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ruanxin on 2017/7/30.
 */
public class SQLContrainsPattern {
    public static Map<String, List<SortCode>> map;

    public static Map getContrainsPattern() {
        if (map == null) {
            map = new HashMap<String, List<SortCode>>();
            List<SortCode> prim_list = new ArrayList<SortCode>();
            prim_list.add(SortCode.PRIMARY);
            prim_list.add(SortCode.KEY);
            prim_list.add(SortCode.LPARENT);
            map.put("PRIMARY", prim_list);

            List<SortCode> fore_list = new ArrayList<SortCode>();
            fore_list.add(SortCode.FOREIGN);
            fore_list.add(SortCode.KEY);
            fore_list.add(SortCode.LPARENT);
            fore_list.add(SortCode.IDENTIFIED);
            fore_list.add(SortCode.RPARENT);
            fore_list.add(SortCode.REFERENCES);
            fore_list.add(SortCode.IDENTIFIED);
            fore_list.add(SortCode.LPARENT);
            fore_list.add(SortCode.IDENTIFIED);
            fore_list.add(SortCode.RPARENT);
            map.put("FOREIGN", fore_list);

            List<SortCode> uni_list = new ArrayList<SortCode>();
            uni_list.add(SortCode.UNIQUE);
            uni_list.add(SortCode.LPARENT);
            map.put("UNIQUE", uni_list);

            List<SortCode> check_list = new ArrayList<SortCode>();
            check_list.add(SortCode.CHECK);
            check_list.add(SortCode.LPARENT);
            map.put("CHECK", check_list);

            return map;
        }
        return map;
    }
}
