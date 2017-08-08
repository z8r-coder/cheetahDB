package Parser.AstGen;

import Parser.AST;
import Parser.ASTNode;

import java.util.List;

/**
 * Created by ruanxin on 2017/8/9.
 */
public interface BaseAST extends SQLAst {
    /**
     * 注入结点集合
     * @param set
     */
    public void setCollection(List<ASTNode> set);
    /**
     * 返回结点集合
     */
    public List<ASTNode> getCollection();
}
