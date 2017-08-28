package Service.SQLBptService.SelectService;

import Constants.SQLErrorCode;
import Models.Column;
import Models.Row;
import Models.Table;
import Models.Value;
import Parser.Builder.SQLSelectBuilder;
import Utils.ServiceUtils;
import Exception.SelectException;

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
        for (String column : columns) {
            valuesMap.put(column, new ArrayList<Value>());
        }
        List<Row> rows = table.getRows();
    }
}
