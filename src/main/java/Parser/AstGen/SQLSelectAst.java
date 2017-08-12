package Parser.AstGen;

import Parser.ASTNode;
import Parser.Visitor.SQLASTVisitor;

import java.util.List;

/**
 * select ast
 *
 * Created by ruanxin on 2017/8/9.
 */
public class SQLSelectAst implements BaseAST{
    private ASTNode root;
    private SQLSelectAst parentSelect;// 父查询
    private SQLSelectAst[] subSelect;//子查询组，20170809:子查询只出现在where后

    public SQLSelectAst(ASTNode root) {
        this.root = root;
    }
    public void setRoot(ASTNode root) {
        this.root = root;
    }

    public ASTNode getRoot() {
        return root;
    }

    public void setParentSelect(SQLSelectAst parentSelect) {
    }

    public SQLSelectAst getParentSelect() {
        return parentSelect;
    }

    public void accept(SQLASTVisitor visitor) {

    }
}
