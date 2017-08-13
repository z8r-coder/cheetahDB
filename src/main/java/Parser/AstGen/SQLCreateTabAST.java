package Parser.AstGen;

import Parser.ASTNode;
import Parser.Visitor.SQLASTVisitor;

/**
 * Created by ruanxin on 2017/8/12.
 */
public class SQLCreateTabAST implements BaseAST{
    private ASTNode astNode;

    public SQLCreateTabAST(ASTNode astNode) {
        this.astNode = astNode;
    }

    public void setRoot(ASTNode astNode) {
        this.astNode = astNode;
    }

    public ASTNode getRoot() {
        return astNode;
    }


    public void accept(SQLASTVisitor visitor) throws Exception {
        visitor.visit(this);
    }
}
