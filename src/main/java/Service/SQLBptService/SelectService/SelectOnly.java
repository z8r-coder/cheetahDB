package Service.SQLBptService.SelectService;

import Models.Column;
import Models.Row;
import Models.Table;
import Parser.Builder.SQLSelectBuilder;

import java.util.List;
import java.util.Map;

/**
 * 不带where的查询
 * Created by rx on 2017/8/26.
 */
public class SelectOnly implements SelectService {

    public void invoke(Table table, SQLSelectBuilder selectBuilder) {
        List<String> columns = selectBuilder.columns();

        List<Row> rows = table.getRows();
    }
}
