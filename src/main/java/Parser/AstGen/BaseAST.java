package Parser.AstGen;

import Parser.AST;
import Parser.ASTNode;
import Parser.Visitor.SQLASTVisitor;

import java.util.List;

/**
 * Created by ruanxin on 2017/8/9.
 */
public interface BaseAST extends SQLAst {
    public void accept(SQLASTVisitor visitor) throws Exception;
}
