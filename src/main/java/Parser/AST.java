package Parser;

import java.util.ArrayList;
import java.util.List;
import Exception.NotRootException;

/**
 * Created by roy on 2017/7/29.
 */
public class AST {
    private SQLASTType astType;
    private ASTNode root;
    private String name;

    public AST () {

    }
    public AST(ASTNode root, String name) {
        this.root = root;
        this.name = name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setRoot(ASTNode root) {
        this.root = root;
    }

    public ASTNode getRoot() {
        return root;
    }

    public void setAstType(SQLASTType astType) {
        this.astType = astType;
    }

    public SQLASTType getAstType() {
        return astType;
    }
}
