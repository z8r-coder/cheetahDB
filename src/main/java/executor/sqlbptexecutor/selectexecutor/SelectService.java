package executor.sqlbptexecutor.selectexecutor;

import models.SimpleTable;
import models.Table;
import parser.Builder.SQLSelectBuilder;
import exception.SelectException;

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
