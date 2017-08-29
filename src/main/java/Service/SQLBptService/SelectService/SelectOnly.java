package Service.SQLBptService.SelectService;

import Constants.SQLErrorCode;
import Models.Column;
import Models.Row;
import Models.Table;
import Models.Value;
import Parser.Builder.SQLSelectBuilder;
import Utils.ServiceUtils;
import Exception.SelectException;
import Utils.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 不带where的查询
 * Created by rx on 2017/8/26.
 */
public class SelectOnly implements SelectService {

    public void invoke(Table table, SQLSelectBuilder selectBuilder) throws SelectException {
        //列名
        List<String> columns = selectBuilder.columns();

        List<Column> standerCol = table.getColumns();

        Map<String, List<Value>> valuesMap = new HashMap<String, List<Value>>();

        if (!ServiceUtils.checkColumn(columns, standerCol)) {
            throw new SelectException(SQLErrorCode.SQL00042);
        }

        List<Row> rows = table.getRows();

        List<Integer> indexList = new ArrayList<Integer>();
        //获取串值的index
        for (String column : columns) {
            for (int i = 0; i < standerCol.size();i++) {
                if (StringUtils.equals(column,standerCol.get(i).getName())) {
                    indexList.add(i);
                    break;
                }
            }
            valuesMap.put(column, new ArrayList<Value>());
        }

        for (Row row : rows) {
            List<Value> values = row.getValues();
            for (Integer integer : indexList) {
                String colName = values.get(integer).getColumName();

            }
        }
    }
}
