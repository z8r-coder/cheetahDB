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
import Utils.InsertUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 单行非缺省插入
 * Created by rx on 2017/8/26.
 */
public class InsertSingleService implements InsertService{

    public void invoke(Table table, SQLInsertBuilder insertBuilder) throws Exception {
        List<List<Value>> values = insertBuilder.values();

        List<String> columnNames = insertBuilder.columnName();

        List<Value> singleValues = values.get(0);

        List<Column> standerCol = table.getColumns();

        Map<String, Column> columnMap = table.getColumnMap();

        //检查插入的列和值是否匹配
        if (columnNames.size() != singleValues.size()) {
            throw new InsertException(SQLErrorCode.SQL00002);
        }

        //检查是否插入正确的列
        if (!InsertUtils.checkColumn(columnMap, columnNames)) {
            throw new InsertException(SQLErrorCode.SQL00007);
        }

        Row row = new Row();

        Map<String, Value> valueMap = new HashMap<String, Value>();

        for (int i = 0; i < columnNames.size();i++) {
            Column column = columnMap.get(columnNames.get(i));
            if (column.getDataType() != singleValues.get(i).getDataType()) {
                throw new InsertException(SQLErrorCode.SQL00005);
            }
            if (column.getPrimaryKey()) {
                if (singleValues.get(i) == null) {
                    throw new InsertException(SQLErrorCode.SQL00006);
                }
                //设置该行的主键value
                row.setPRIMARY_KEY(singleValues.get(i));
            }

            String colName = column.getName();
            Value val = singleValues.get(i);

            val.setColumName(colName);
            //若存在插入列相同的，则以最后门面列的值为准
            valueMap.put(colName, val);
        }

        if (row.getPRIMARY_KEY() == null) {
            throw new InsertException(SQLErrorCode.SQL00006);
        }

        List<Value> insertValue = new ArrayList<Value>();
        for (Column column : standerCol) {
            if (valueMap.get(column.getName()) == null) {
                //若插入行中，不存在该行，先判断该列是否允许空
                if (column.getNotNull()) {
                    //若不允许为空
                    throw new InsertException(SQLErrorCode.SQL00008);
                }
                //unique约束的column可以插入多个列
                Value value = new Value("NULL", SQLDataType.NULL);
                value.setColumName(column.getName());
                insertValue.add(value);
            } else {
                Value value = valueMap.get(column.getName());
                insertValue.add(value);
            }
        }

        row.setValues(insertValue);
        //索引值
        Value indexValue = row.getPRIMARY_KEY();
        Bplustree bpt = table.getIndexTree(indexValue.getColumName());

        //维护索引树
        if (bpt.search(indexValue) != null) {
            //主键冲突
            throw new InsertException(SQLErrorCode.SQL00009);
        }
        bpt.insert(indexValue, row);
        //将该行添加到表空间
        table.addRow(row);
    }
}
