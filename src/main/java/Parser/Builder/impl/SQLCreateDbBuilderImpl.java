package Parser.Builder.impl;

import Parser.AstGen.SQLCreateDbAst;
import Parser.Builder.SQLBuildAdapter;
import Parser.Builder.SQLCreateDbBuilder;

/**
 * Created by ruanxin on 2017/8/16.
 */
public class SQLCreateDbBuilderImpl implements SQLCreateDbBuilder, SQLBuildAdapter{
    private SQLCreateDbAst crtDbAst;

    public String dataBaseName() {
        return null;
    }

    public void build(String sql) {

    }
}
