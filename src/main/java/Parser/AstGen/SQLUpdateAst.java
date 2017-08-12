package Parser.AstGen;

import Parser.ASTNode;
import Parser.Visitor.SQLASTVisitor;

/**
 * Created by ruanxin on 2017/8/12.
 */
public class SQLUpdateAst implements BaseAST{
    private ASTNode astNode;

    public SQLUpdateAst(ASTNode astNode) {
        this.astNode = astNode;
    }

    public void accept(SQLASTVisitor visitor) {

    }
}
