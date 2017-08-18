package Parser.Builder.impl;

import Parser.Builder.SQLBuilder;
import Parser.Builder.SQLShowDbBuilder;
import Parser.SQLASTType;

/**
 * Created by ruanxin on 2017/8/16.
 */
public class SQLShowDbBuilderImpl implements SQLShowDbBuilder {


    public void build(String sql) throws Exception {

    }

    public SQLASTType grammerType() {
        return SQLASTType.SHOW_DATABASES;
    }
}
