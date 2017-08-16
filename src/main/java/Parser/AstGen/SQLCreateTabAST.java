package Parser.AstGen;

import Parser.AST;
import Parser.ASTNode;
import Parser.Visitor.SQLASTVisitor;
import Parser.Visitor.SchemaStatVisitor;

import java.util.List;

/**
 * Created by ruanxin on 2017/8/12.
 */
public class SQLCreateTabAST implements BaseAST{
    private AST ast;

    // TODO: 2017/8/13 column是否应该放在visitor内，再考虑
    private List<SchemaStatVisitor.Column> columns;

    private String tableName;
    public SQLCreateTabAST(AST ast) {
        this.ast = ast;
    }

    public AST getAst() {
        return ast;
    }

    public void setAst(AST ast) {
        this.ast = ast;
    }

    public void setColumns(List<SchemaStatVisitor.Column> columns) {
        this.columns = columns;
    }

    public List<SchemaStatVisitor.Column> getColumns() {
        return columns;
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
