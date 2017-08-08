package Parser.AstGen;

import Parser.ASTNode;

import java.util.List;

/**
 * select ast
 *
 * Created by ruanxin on 2017/8/9.
 */
public class SelectAst implements BaseAST{
    private List<ASTNode> select_set;
    public void setCollection(List<ASTNode> select_set) {
        this.select_set = select_set;
    }

    public List<ASTNode> getCollection() {
        return select_set;
    }
}
