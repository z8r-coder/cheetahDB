package parser.builder.impl;

import parser.AST;
import parser.astgen.SQLUseAst;
import parser.builder.SQLUseDbBuilder;
import parser.SQLASTType;
import parser.SQLParserUtils;
import parser.visitor.SchemaStatVisitor;

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

    public SQLASTType grammerType() {
        return useAst.getAst().getAstType();
    }
}
