package Utils;

import Models.Column;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rx on 2017/8/28.
 */
public class ServiceUtils {

    public static boolean checkColumn(List<String> columns, List<Column> standerCol) {
        int count = 0;
        List<String> standerStr = new ArrayList<String>();

        for (Column column : standerCol) {
            standerStr.add(column.getName());
        }

        for (String cName : columns) {
            if (standerStr.contains(cName)) {
                count++;
                break;
            }
        }
        return count == columns.size()?true : false;
    }
}
