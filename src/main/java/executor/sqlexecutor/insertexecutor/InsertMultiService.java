package executor.sqlexecutor.insertexecutor;

import constants.SQLErrorCode;
import engine.Bplustree;
import models.Column;
import models.Row;
import models.Table;
import models.Value;
import parser.builder.SQLInsertBuilder;
import exception.InsertException;
import parser.SQLDataType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 多行非缺省插入
 * Created by rx on 2017/8/26.
 */
public class InsertMultiService implements InsertService{

    public void invoke(Table table, SQLInsertBuilder insertBuilder) throws InsertException {
        List<List<Value>> values = insertBuilder.values();

        List<String> columnNames = insertBuilder.columnName();

        List<Column> stanterCol = table.getColumns();

        Map<String, Column> columnMap = table.getColumnMap();

        for (List<Value> vv : values) {
            if (vv.size() != columnNames.size()) {
                throw new InsertException(SQLErrorCode.SQL00002);
            }
        }

        //检查是否插入正确的列
        if (!InsertUtils.checkColumn(columnMap, columnNames)) {
            throw new InsertException(SQLErrorCode.SQL00007);
        }

        List<Row> rows = new ArrayList<Row>();

        int rowCount = values.size();
        List<Map<String, Value>> valueMaps = new ArrayList<Map<String, Value>>();

        //初始化行与值map
        for (int i = 0; i < rowCount;i++) {
            rows.add(new Row());
            valueMaps.add(new HashMap<String, Value>());
        }


        for (int i = 0;i < columnNames.size();i++) {
            Column column = columnMap.get(columnNames.get(i));
            String colName = column.getName();
            for (int j = 0; j < values.size();j++) {
                List<Value> vv = values.get(j);
                if (vv.get(i).getDataType() != column.getDataType()) {
                    throw new InsertException(SQLErrorCode.SQL00005);
                }
                if (column.getPrimaryKey()) {
                    if (vv.get(i) == null) {
                        throw new InsertException(SQLErrorCode.SQL00006);
                    }
                    rows.get(j).setPRIMARY_KEY(vv.get(i));
                }

                Value value = vv.get(i);
                value.setColumName(colName);
                valueMaps.get(j).put(colName, value);
            }
        }

        //检查是否有主键
        for (Row row : rows) {
            if (row.getPRIMARY_KEY() == null) {
                throw new InsertException(SQLErrorCode.SQL00006);
            }
        }

        List<List<Value>> insertValues = new ArrayList<List<Value>>();
        //初始化
        for (int i = 0; i < rowCount;i++) {
            insertValues.add(new ArrayList<Value>());
        }
        for (int i = 0; i < valueMaps.size();i++) {
            for (Column column : stanterCol) {
                if (valueMaps.get(i).get(column.getName()) == null) {
                    Value value = new Value("NULL", SQLDataType.NULL);
                    value.setColumName(column.getName());
                    insertValues.get(i).add(value);
                } else {
                    Value value = valueMaps.get(i).get(column.getName());
                    insertValues.get(i).add(value);
                    rows.get(i).putValue(column.getName(), value);
                }
            }
        }

        for (int i = 0; i < rowCount;i++) {
            rows.get(i).setValues(insertValues.get(i));
        }

        //调整索引结构
        Column primaryKey = table.getPRIMARY_KEY();
        Bplustree bpt = table.getIndexTree(primaryKey.getName());

        InsertUtils.addTableSpace(rows, bpt, table);
    }
}
