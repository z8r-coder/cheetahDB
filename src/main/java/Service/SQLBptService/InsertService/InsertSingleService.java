package Service.SQLBptService.InsertService;

import Engine.Bplustree;
import Models.Column;
import Models.Row;
import Models.Table;
import Models.Value;
import Parser.Builder.SQLInsertBuilder;

import java.util.List;

/**
 * 单行非缺省插入
 * Created by rx on 2017/8/26.
 */
public class InsertSingleService implements InsertService{

    public void invoke(Table table, SQLInsertBuilder insertBuilder) {
        List<List<Value>> values = insertBuilder.values();
        List<Column> columns = insertBuilder.columns();


    }
}
