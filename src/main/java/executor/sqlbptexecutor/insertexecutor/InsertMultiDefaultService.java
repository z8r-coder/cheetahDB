package executor.sqlbptexecutor.insertexecutor;

import constants.SQLErrorCode;
import engine.Bplustree;
import models.Column;
import models.Row;
import models.Table;
import models.Value;
import parser.Builder.SQLInsertBuilder;

import exception.InsertException;
import parser.SQLDataType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 多行缺省插入
 * Created by rx on 2017/8/26.
 */
public class InsertMultiDefaultService implements InsertService {

    public void invoke(Table table, SQLInsertBuilder insertBuilder) throws InsertException {
        List<List<Value>> values = insertBuilder.values();

        List<Column> standerCol = table.getColumns();

        Map<String, Column> columnMap = table.getColumnMap();

        for (List<Value> vv : values) {
            if (vv.size() > standerCol.size()) {
                throw new InsertException(SQLErrorCode.SQL00010);
            }
        }

        List<Row> rows = new ArrayList<Row>();
        int rowCount = values.size();
        List<Map<String, Value>> valueMaps = new ArrayList<Map<String, Value>>();

        //初始化行和值map
        for (int i = 0; i < rowCount;i++) {
            rows.add(new Row());
            valueMaps.add(new HashMap<String, Value>());
        }

        for (int i = 0; i < rowCount;i++) {
            List<Value> singleValues = values.get(i);
            for (int j = 0; j < singleValues.size();j++) {
                if (standerCol.get(j).getDataType() != singleValues.get(j).getDataType()) {
                    throw new InsertException(SQLErrorCode.SQL00005);
                }
                if (standerCol.get(j).getPrimaryKey()) {
                    if (singleValues.get(j) == null) {
                        throw new InsertException(SQLErrorCode.SQL00006);
                    }
                    rows.get(i).setPRIMARY_KEY(singleValues.get(j));
                }
                Value value = singleValues.get(j);
                value.setColumName(standerCol.get(j).getName());
                valueMaps.get(j).put(value.getColumName(), value);
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
        for (int i = 0; i < rowCount;i++) {
            List<Value> singleValue = values.get(i);
            for (int j = 0; j < singleValue.size(); j++) {
                insertValues.get(i).add(singleValue.get(j));
                //设置每行对应的列-值map
                rows.get(i).putValue(singleValue.get(j).getColumName(), singleValue.get(j));
            }
            for (int j = singleValue.size(); j < standerCol.size();j++) {
                insertValues.get(i).add(new Value("NULL", SQLDataType.NULL));
            }
            rows.get(i).setValues(insertValues.get(i));
        }

        //维护索引结构
        Column primaryKey = table.getPRIMARY_KEY();
        Bplustree bpt = table.getIndexTree(primaryKey.getName());

        InsertUtils.addTableSpace(rows, bpt, table);
    }
}
