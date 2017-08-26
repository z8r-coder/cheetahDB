package Service.SQLBptService.DeleteService;

import Models.Table;
import Parser.Builder.SQLDeleteBuilder;

/**
 * 删除基础服务
 * Created by rx on 2017/8/26.
 */
public interface DeleteService {

    /**
     * 执行删除操作
     * @param table
     * @param deleteBuilder
     */
    public void invoke(Table table, SQLDeleteBuilder deleteBuilder);
}
