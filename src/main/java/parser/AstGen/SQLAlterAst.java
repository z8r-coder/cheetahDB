package parser.AstGen;

import parser.ASTNode;
import parser.Visitor.SQLASTVisitor;

/**
 * Created by ruanxin on 2017/8/12.
 */
public class SQLAlterAst implements BaseAST {
    ASTNode astNode;
    public SQLAlterAst(ASTNode astNode) {
        this.astNode = astNode;
    }

    public void setAstNode(ASTNode astNode) {
        this.astNode = astNode;
    }

    public ASTNode getAstNode() {
        return astNode;
    }

    public void accept(SQLASTVisitor visitor) {

    }
}
