package Service.SQLBptService.InsertService;

import Models.Table;
import Parser.Builder.SQLInsertBuilder;

/**
 * 插入服务基础类
 * Created by rx on 2017/8/26.
 */
public interface InsertService {
    /**
     * 执行插入操作
     */
    public void invoke(Table table, SQLInsertBuilder insertBuilder);
}
