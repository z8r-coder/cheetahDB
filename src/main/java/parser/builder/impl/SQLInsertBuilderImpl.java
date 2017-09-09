package parser.builder.impl;

import models.Value;
import parser.AST;
import parser.astgen.SQLInsertAst;
import parser.builder.SQLInsertBuilder;
import parser.SQLASTType;
import parser.SQLParserUtils;
import parser.visitor.SchemaStatVisitor;

import java.util.List;

/**
 * Created by ruanxin on 2017/8/16.
 */
public class SQLInsertBuilderImpl implements SQLInsertBuilder {
    private SQLInsertAst insertAst;

    public SQLInsertBuilderImpl(){
    }
    public void build(String sql) throws Exception {
        AST past = SQLParserUtils.AssSQlASTgen(sql);
        insertAst = new SQLInsertAst(past);
        insertAst.accept(new SchemaStatVisitor());
    }

    public SQLASTType grammerType() {
        return insertAst.getAst().getAstType();
    }

    public String from() {
        return insertAst.getTableName();
    }

    public List<String> columnName() {
        return insertAst.getColumnNames();
    }

    public List<List<Value>> values() {
        return insertAst.getValues();
    }
}
