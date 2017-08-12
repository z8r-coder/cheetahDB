package Parser.AstGen;

import Parser.ASTNode;
import Parser.Visitor.SQLASTVisitor;

/**
 * Created by ruanxin on 2017/8/9.
 */
public class SQLInsertAst implements BaseAST {
    private ASTNode astNode;
    public SQLInsertAst (ASTNode astNode) {
        this.astNode = astNode;
    }

    public ASTNode getAstNode() {
        return astNode;
    }

    public void setAstNode(ASTNode astNode) {
        this.astNode = astNode;
    }
    public void accept(SQLASTVisitor visitor) {

    }
}
