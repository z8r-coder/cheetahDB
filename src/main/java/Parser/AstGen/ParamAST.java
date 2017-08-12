package Parser.AstGen;

import Parser.ASTNode;
import Parser.Visitor.SQLASTVisitor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ruanxin on 2017/8/10.
 */
public class ParamAST implements BaseAST {
    private ASTNode root;
    private List<String> paramName = new ArrayList<String>();
    public void setRoot(ASTNode root) {
        this.root = root;
    }

    public ASTNode getRoot() {
        return root;
    }

    public void accept(SQLASTVisitor visitor) {

    }
}
