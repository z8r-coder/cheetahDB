package executor.sqlbptexecutor.updateservice;

import models.Table;
import parser.Builder.SQLUpdateBuilder;

/**
 * 更新服务基础类
 * Created by rx on 2017/8/26.
 */
public interface UpdateService {

    public void invoke(Table table, SQLUpdateBuilder updateBuilder);
}
