package Parser.AstGen;

import Parser.AST;
import Parser.ASTNode;

import java.util.List;

/**
 * Created by ruanxin on 2017/8/9.
 */
public interface BaseAST extends SQLAst {
    /**
     * 注入根结点
     * @param astNode
     */
    public void setRoot(ASTNode astNode);
    /**
     * 返回根结点
     */
    public ASTNode getRoot();
}
