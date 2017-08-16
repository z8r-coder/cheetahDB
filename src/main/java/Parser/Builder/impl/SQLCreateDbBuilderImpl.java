package Parser.Builder.impl;

import Parser.AST;
import Parser.AstGen.SQLCreateDbAst;
import Parser.Builder.SQLBuilder;
import Parser.Builder.SQLCreateDbBuilder;
import Parser.SQLParserUtils;
import Parser.Visitor.SchemaStatVisitor;

/**
 * Created by ruanxin on 2017/8/16.
 */
public class SQLCreateDbBuilderImpl implements SQLCreateDbBuilder, SQLBuilder {
    private SQLCreateDbAst crtDbAst;

    public SQLCreateDbBuilderImpl(){}

    public String dataBaseName() {
        return crtDbAst.getDatabaseName();
    }

    public void build(String sql) throws Exception {
        AST past = SQLParserUtils.AssSQlASTgen(sql);
        crtDbAst = new SQLCreateDbAst(past);
        crtDbAst.accept(new SchemaStatVisitor());
    }

    public String grammerType() {
        return "CrtDb";
    }
}
