package Parser.AstGen;

import Models.Column;
import Models.Value;
import Parser.AST;
import Parser.ASTNode;
import Parser.Visitor.SQLASTVisitor;
import Parser.Visitor.SchemaStatVisitor;

import java.util.List;

/**
 * Created by ruanxin on 2017/8/9.
 */
public class SQLInsertAst implements BaseAST {
    private AST ast;

    private String tableName;

    private List<Column> columns;//插入columns

    private List<List<Value>> list_values;//插入集合

    public SQLInsertAst (AST ast) {
        this.ast = ast;
    }

    public void setAst(AST ast) {
        this.ast = ast;
    }

    public AST getAst() {
        return ast;
    }

    public void setColumns(List<Column> columns) {
        this.columns = columns;
    }

    public List<Column> getColumns() {
        return columns;
    }

    public void setValues(List<List<Value>> list_values) {
        this.list_values = list_values;
    }

    public List<List<Value>> getValues() {
        return list_values;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getTableName() {
        return tableName;
    }

    public void accept(SQLASTVisitor visitor) throws Exception {
        visitor.visit(this);
    }
}
