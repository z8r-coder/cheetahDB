package parser.AstGen;

import parser.AST;
import parser.Visitor.SQLASTVisitor;

/**
 * Created by ruanxin on 2017/8/12.
 */
public class SQLCreateDbAst implements BaseAST{
    private AST ast;

    private String databaseName;

    public SQLCreateDbAst(AST ast) {
        this.ast = ast;
    }

    public void setAst(AST ast) {
        this.ast = ast;
    }

    public AST getAst() {
        return ast;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public void accept(SQLASTVisitor visitor) throws Exception {
        visitor.visit(this);
    }
}
