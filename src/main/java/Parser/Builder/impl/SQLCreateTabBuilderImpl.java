package Parser.Builder.impl;

import Parser.AST;
import Parser.AstGen.SQLCreateTabAST;
import Parser.Builder.SQLBuildAdapter;
import Parser.Builder.SQLCreateTabBuilder;
import Parser.SQLParserUtils;
import Parser.Visitor.SchemaStatVisitor;
import Utils.ASTUtils;
import Utils.StringUtils;

import java.util.List;

/**
 * Created by ruanxin on 2017/8/16.
 */
public class SQLCreateTabBuilderImpl implements SQLCreateTabBuilder, SQLBuildAdapter {
    private SQLCreateTabAST ast;

    public SQLCreateTabBuilderImpl(SQLCreateTabAST ast) {
        this.ast = ast;
    }

    public void build(String sql) {
        AST ast = SQLParserUtils.AssSQlASTgen(sql);

    }

    public String tableName() {
        if (StringUtils.isEmpty(ast.getTableName())) {
            return null;
        }
        return ast.getTableName();
    }

    public List<SchemaStatVisitor.Column> Columns() {

        return null;
    }
}
