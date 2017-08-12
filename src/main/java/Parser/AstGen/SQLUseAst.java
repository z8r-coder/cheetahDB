package Parser.AstGen;

import Parser.ASTNode;
import Parser.Visitor.SQLASTVisitor;

/**
 * Created by ruanxin on 2017/8/12.
 */
public class SQLUseAst implements BaseAST{
    private ASTNode astNode;

    public SQLUseAst (ASTNode astNode) {
        this.astNode = astNode;
    }

    public void accept(SQLASTVisitor visitor) {

    }
}
