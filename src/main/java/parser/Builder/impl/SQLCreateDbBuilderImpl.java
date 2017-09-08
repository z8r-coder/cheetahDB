package parser.Builder.impl;

import parser.AST;
import parser.AstGen.SQLCreateDbAst;
import parser.Builder.SQLCreateDbBuilder;
import parser.SQLASTType;
import parser.SQLParserUtils;
import parser.Visitor.SchemaStatVisitor;

/**
 * Created by ruanxin on 2017/8/16.
 */
public class SQLCreateDbBuilderImpl implements SQLCreateDbBuilder {
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

    public SQLASTType grammerType() {
        return crtDbAst.getAst().getAstType();
    }
}
