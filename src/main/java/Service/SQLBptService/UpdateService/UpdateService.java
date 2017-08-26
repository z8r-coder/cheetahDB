package Service.SQLBptService.UpdateService;

import Models.Table;
import Parser.Builder.SQLUpdateBuilder;

/**
 * 更新服务基础类
 * Created by rx on 2017/8/26.
 */
public interface UpdateService {

    public void invoke(Table table, SQLUpdateBuilder updateBuilder);
}
