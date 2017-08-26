package Models;

import java.util.HashMap;
import java.util.Map;

/**
 * 数据库空间
 * Created by rx on 2017/8/26.
 */
public class DataBase {
    private Map<String, Table> tableMap = new HashMap<String, Table>(16);

    public Map<String, Table> getTableMap() {
        return tableMap;
    }

    public void putTable (String name, Table table) {
        tableMap.put(name, table);
    }

    public Table getTable(String name) {
        return tableMap.get(name);
    }
}
