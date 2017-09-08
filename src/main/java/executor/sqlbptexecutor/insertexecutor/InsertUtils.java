package executor.sqlbptexecutor.insertexecutor;

import constants.SQLErrorCode;
import engine.Bplustree;
import models.Column;
import models.Row;
import models.Table;
import models.Value;
import exception.InsertException;
import java.util.List;
import java.util.Map;

/**
 * Created by rx on 2017/9/2.
 */
public class InsertUtils {

    /**
     * 判断value是否为空
     * @param value
     * @return
     */
    public static boolean checkValueIsEmpty(Value value) {
        if (value == null || value.getVal() == null || value.getVal() == "") {
            return true;
        }
        return false;
    }

    /**
     * 将行信息添加入表空间，并维护索引树
     * @param rows
     * @param bpt
     * @param table
     * @throws InsertException
     */
    public static void addTableSpace(List<Row> rows, Bplustree bpt, Table table) throws InsertException {
        for (Row row : rows) {
            Value indexValue = row.getPRIMARY_KEY();
            if (bpt.search(indexValue) != null) {
                throw new InsertException(SQLErrorCode.SQL00009);
            }
            bpt.insert(indexValue, row);
            //将该行添加到表空间
            table.addRow(row);
        }
    }

    /**
     * 检查插入的列是否为空
     * @param columnMap
     * @param columnName
     * @return
     */
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
