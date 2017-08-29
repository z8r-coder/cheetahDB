package Service.SQLBptService.SelectService;

import Models.SimpleTable;
import Models.Table;
import Parser.Builder.SQLSelectBuilder;
import Exception.SelectException;

/**
 * 查询基础服务类
 * Created by rx on 2017/8/26.
 */
public interface SelectService {
    /**
     * 执行查询语句
     * @param table
     * @param selectBuilder
     */
    public SimpleTable invoke (Table table, SQLSelectBuilder selectBuilder) throws SelectException;
}
