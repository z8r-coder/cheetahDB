package Service.SQLBptService.InsertService;

import Constants.SQLErrorCode;
import Engine.Bplustree;
import Models.Column;
import Models.Row;
import Models.Table;
import Models.Value;
import Parser.Builder.SQLInsertBuilder;
import Exception.InsertException;
import Parser.SQLDataType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 单行缺省插入
 * Created by rx on 2017/8/26.
 */
public class InsertSingleDefaultService implements InsertService {

    public void invoke(Table table, SQLInsertBuilder insertBuilder) throws InsertException {
        List<List<Value>> values = insertBuilder.values();

        List<Value> singleValues = values.get(0);

        List<Column> standerCol = table.getColumns();

        Map<String, Column> columnMap = table.getColumnMap();

        if (singleValues.size() > standerCol.size()) {
            throw new InsertException(SQLErrorCode.SQL00010);
        }

        Row row = new Row();

        for (int i = 0; i < singleValues.size();i++) {
            //检查数据类型是否一致
            if (standerCol.get(i).getDataType() != singleValues.get(i).getDataType()) {
                throw new InsertException(SQLErrorCode.SQL00005);
            }

            //主键是否为空
            if (standerCol.get(i).getPrimaryKey()) {
                if (InsertUtils.checkValueIsEmpty(singleValues.get(i))) {
                    throw new InsertException(SQLErrorCode.SQL00006);
                }
                row.setPRIMARY_KEY(singleValues.get(i));
            }

            String columnName = standerCol.get(i).getName();
            Value value = singleValues.get(i);

            value.setColumName(columnName);
        }

        //是否设置主键
        if (row.getPRIMARY_KEY() == null) {
            throw new InsertException(SQLErrorCode.SQL00006);
        }

        List<Value> insertValues = new ArrayList<Value>();
        int length = singleValues.size();
        for (int i = 0; i < length;i++) {
            Value value = singleValues.get(i);
            insertValues.add(value);
            row.putValue(value.getColumName(), value);
        }

        for (int i = length; i < standerCol.size();i++) {
            insertValues.add(new Value("NULL", SQLDataType.NULL));
        }

        //设置行的全部值信息
        row.setValues(insertValues);

        //维护索引树
        Value indexValue = row.getPRIMARY_KEY();
        Bplustree bpt = table.getIndexTree(indexValue.getColumName());

        if (bpt.search(indexValue) != null) {
            throw new InsertException(SQLErrorCode.SQL00009);
        }

        bpt.insert(indexValue, row);

        table.addRow(row);
    }
}
