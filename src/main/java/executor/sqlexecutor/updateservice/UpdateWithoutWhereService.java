package executor.sqlexecutor.updateservice;

import models.Table;
import parser.builder.SQLUpdateBuilder;

/**
 * 不带where的更新服务
 * Created by rx on 2017/8/26.
 */
public class UpdateWithoutWhereService implements UpdateService {

    public void invoke(Table table, SQLUpdateBuilder updateBuilder) {

    }
}
