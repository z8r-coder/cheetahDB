package Parser.AstGen;

import Log.CheetahASTLog;
import Parser.ASTNode;
import Parser.Visitor.SQLASTVisitor;

import java.util.List;

/**
 * select ast
 *
 * Created by ruanxin on 2017/8/9.
 */
public class SelectAst implements BaseAST{
    private ASTNode root;
    private SelectAst parentSelect;// 父查询
    private SelectAst[] subSelect;//子查询组，20170809:子查询只出现在where后

    public SelectAst(ASTNode root) {
        this.root = root;
    }
    public void setRoot(ASTNode root) {
        this.root = root;
    }

    public ASTNode getRoot() {
        return root;
    }

    public void setParentSelect(SelectAst parentSelect) {
        if (parentSelect == null) {
            CheetahASTLog.Info("set parentSelect fail! parentSelect=",parentSelect);
            return;
        }
        this.parentSelect = parentSelect;
    }

    public SelectAst getParentSelect() {
        return parentSelect;
    }

    public void accept(SQLASTVisitor visitor) {

    }
}
