package Parser.AstGen;

import Parser.ASTNode;
import Parser.Visitor.SQLASTVisitor;

/**
 * Created by ruanxin on 2017/8/12.
 */
public class SQLShowDbAst implements BaseAST{
    private ASTNode astNode;

    public SQLShowDbAst (ASTNode astNode) {
        this.astNode = astNode;
    }

    public void accept(SQLASTVisitor visitor) {

    }
}
