package Parser.AstGen;

import Parser.ASTNode;
import Parser.Visitor.SQLASTVisitor;

/**
 * Created by ruanxin on 2017/8/12.
 */
public class SQLDeleteAst implements BaseAST {
    private ASTNode astNode;
    public SQLDeleteAst(ASTNode astNode) {
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
