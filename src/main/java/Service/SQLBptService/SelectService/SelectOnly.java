package Service.SQLBptService.SelectService;

import Models.Table;
import Parser.Builder.SQLSelectBuilder;

/**
 * 不带where的查询
 * Created by rx on 2017/8/26.
 */
public class SelectOnly implements SelectService {

    public void invoke(Table table, SQLSelectBuilder selectBuilder) {

    }
}
