package Parser.AstGen;

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

    private List<SchemaStatVisitor.Column> columns;//插入columns

    private List<SchemaStatVisitor.Value> values;//插入集合

    public SQLInsertAst (AST ast) {
        this.ast = ast;
    }

    public void setAst(AST ast) {
        this.ast = ast;
    }

    public AST getAst() {
        return ast;
    }

    public void setColumns(List<SchemaStatVisitor.Column> columns) {
        this.columns = columns;
    }

    public List<SchemaStatVisitor.Column> getColumns() {
        return columns;
    }

    public void setValues(List<SchemaStatVisitor.Value> values) {
        this.values = values;
    }

    public List<SchemaStatVisitor.Value> getValues() {
        return values;
    }
    public void accept(SQLASTVisitor visitor) throws Exception {
        visitor.visit(this);
    }
}
