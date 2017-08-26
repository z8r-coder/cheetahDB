package Service.SQLBptService.UpdateService;

import Models.Table;
import Parser.Builder.SQLUpdateBuilder;

/**
 * 不带where的更新服务
 * Created by rx on 2017/8/26.
 */
public class UpdateWithoutWhereService implements UpdateService {

    public void invoke(Table table, SQLUpdateBuilder updateBuilder) {

    }
}
