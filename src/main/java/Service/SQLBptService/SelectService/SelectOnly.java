package Service.SQLBptService.SelectService;

import Constants.SQLErrorCode;
import Models.*;
import Parser.Builder.SQLSelectBuilder;
import Utils.ListUtils;
import Utils.ServiceUtils;
import Exception.SelectException;
import Utils.StringUtils;

import java.util.*;

/**
 * 不带where的查询
 * Created by rx on 2017/8/26.
 */
public class SelectOnly implements SelectService {

    public SimpleTable invoke(Table table, SQLSelectBuilder selectBuilder) throws SelectException {
        //被选出的列名
        Set<String> columns = selectBuilder.columns();

        List<Column> standerCol = table.getColumns();

        List<Row> rows = table.getRows();
        //"*"
        if (columns.size() == 1) {
            Iterator it = columns.iterator();
            String column = (String) it.next();
            if (column.equals("*")) {
                SimpleTable simpleTable = new SimpleTable(ListUtils.List2Set(standerCol,0,standerCol.size() - 1));
                List<SimpleRow> simpleRows = new ArrayList<SimpleRow>();

                for (Row row : rows) {
                    simpleRows.add(new SimpleRow(row.getValues()));
                }
                simpleTable.setSimpleRows(simpleRows);
                return simpleTable;
            }
        }
        
        if (!ServiceUtils.checkColumn(columns, standerCol)) {
            throw new SelectException(SQLErrorCode.SQL00042);
        }

        List<Integer> indexList = new ArrayList<Integer>();
        //获取串值的index
        for (String column : columns) {
            for (int i = 0; i < standerCol.size();i++) {
                if (StringUtils.equals(column,standerCol.get(i).getName())) {
                    indexList.add(i);
                    break;
                }
            }
        }
        SimpleTable simpleTable = new SimpleTable(columns);

        for (Row row : rows) {
            //所有值
            List<Value> values = row.getValues();
            SimpleRow simpleRow = new SimpleRow();
            //选出来的值
            List<Value> selectValue = new ArrayList<Value>();
            for (Integer integer : indexList) {
                selectValue.add(values.get(integer));
            }
            simpleRow.setValues(values);
            simpleTable.addSimpleRow(simpleRow);
        }
        return simpleTable;
    }
}
