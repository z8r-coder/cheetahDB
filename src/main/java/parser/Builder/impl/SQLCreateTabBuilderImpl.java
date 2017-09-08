package parser.Builder.impl;

import models.Column;
import parser.AST;
import parser.AstGen.SQLCreateTabAST;
import parser.Builder.SQLCreateTabBuilder;
import parser.SQLASTType;
import parser.SQLParserUtils;
import parser.Visitor.SchemaStatVisitor;

import java.util.List;

/**
 * Created by ruanxin on 2017/8/16.
 */
public class SQLCreateTabBuilderImpl implements SQLCreateTabBuilder {
    private SQLCreateTabAST ast;

    public SQLCreateTabBuilderImpl() {
    }

    public void build(String sql) throws Exception {
        AST past = SQLParserUtils.AssSQlASTgen(sql);
        ast = new SQLCreateTabAST(past);
        ast.accept(new SchemaStatVisitor());
    }

    public SQLASTType grammerType() {
        return ast.getAst().getAstType();
    }

    public String tableName() {
        return ast.getTableName();
    }

    public List<Column> Columns() {
        return ast.getColumns();
    }
}
