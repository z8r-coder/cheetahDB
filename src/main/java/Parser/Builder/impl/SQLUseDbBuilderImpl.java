package Parser.Builder.impl;

import Parser.AST;
import Parser.AstGen.SQLUseAst;
import Parser.Builder.SQLBuilder;
import Parser.Builder.SQLUseDbBuilder;
import Parser.SQLParserUtils;
import Parser.Visitor.SchemaStatVisitor;

/**
 * Created by ruanxin on 2017/8/16.
 */
public class SQLUseDbBuilderImpl implements SQLUseDbBuilder {

    private SQLUseAst useAst;
    public SQLUseDbBuilderImpl(){
    }

    public String dbName() {
        return useAst.getDataName();
    }

    public void build(String sql) throws Exception {
        AST past = SQLParserUtils.AssSQlASTgen(sql);
        useAst = new SQLUseAst(past);
        useAst.accept(new SchemaStatVisitor());
    }

    public String grammerType() {
        return "UseDb";
    }
}
