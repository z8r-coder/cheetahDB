package Utils;

import Models.Column;

import java.util.List;
import java.util.Map;

/**
 * 插入工具类
 * Created by rx on 2017/8/27.
 */
public class InsertUtils {

    public static boolean checkColumn(Map<String, Column> columnMap, List<String> columnName) {
        boolean check = true;
        for (String str : columnName) {
            if (columnMap.get(str) == null) {
                check = false;
                break;
            }
        }
        return check;
    }
}
