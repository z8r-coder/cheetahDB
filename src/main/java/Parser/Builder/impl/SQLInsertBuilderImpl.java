package Parser.Builder.impl;

import Parser.AST;
import Parser.AstGen.SQLInsertAst;
import Parser.Builder.SQLBuilder;
import Parser.Builder.SQLInsertBuilder;
import Parser.SQLParserUtils;
import Parser.Visitor.SchemaStatVisitor;

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

    public String grammerType() {
        return "Insert";
    }

    public String from() {
        return insertAst.getTableName();
    }

    public List<SchemaStatVisitor.Column> columns() {
        return insertAst.getColumns();
    }

    public List<List<SchemaStatVisitor.Value>> values() {
        return insertAst.getValues();
    }
}
