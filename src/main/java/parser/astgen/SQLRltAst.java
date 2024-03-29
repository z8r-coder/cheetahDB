package parser.astgen;

import parser.ASTNode;
import parser.visitor.SQLASTVisitor;

/**
 * Created by ruanxin on 2017/8/12.
 */
public class SQLRltAst implements BaseAST {
    private ASTNode astNode;

    public SQLRltAst (ASTNode astNode) {
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
