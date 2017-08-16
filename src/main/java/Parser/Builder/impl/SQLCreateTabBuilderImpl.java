package Parser.Builder.impl;

import Parser.AST;
import Parser.AstGen.SQLCreateTabAST;
import Parser.Builder.SQLBuilder;
import Parser.Builder.SQLCreateTabBuilder;
import Parser.SQLParserUtils;
import Parser.Visitor.SchemaStatVisitor;

import java.util.List;

/**
 * Created by ruanxin on 2017/8/16.
 */
public class SQLCreateTabBuilderImpl implements SQLCreateTabBuilder, SQLBuilder {
    private SQLCreateTabAST ast;

    public SQLCreateTabBuilderImpl() {
    }

    public void build(String sql) throws Exception {
        AST past = SQLParserUtils.AssSQlASTgen(sql);
        ast = new SQLCreateTabAST(past);
        ast.accept(new SchemaStatVisitor());
    }

    public String grammerType() {
        return "Crt_tab";
    }

    public String tableName() {
        return ast.getTableName();
    }

    public List<SchemaStatVisitor.Column> Columns() {
        return ast.getColumns();
    }
}
