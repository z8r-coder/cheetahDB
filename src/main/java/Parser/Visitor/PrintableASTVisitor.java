package Parser.Visitor;

import Log.CheetahASTLog;
import Parser.ASTNode;
import Parser.AstGen.ASTContext;;
import Parser.AstGen.BaseAST;
import Utils.ASTUtils;

/**
 * Created by ruanxin on 2017/8/8.
 */
public class PrintableASTVisitor implements SQLASTVisitor {

    /**
     * 输出整个语法树
     */
    public static void ALLASTPrint(ASTNode root) {
        if (root == null) {
            CheetahASTLog.Info("InsertSingleRowDefaultASTPrint: root=", root);
            return;
        }
        StringBuilder sb = new StringBuilder();
        String res = ASTUtils.TreeNodeDfsPrintUtils(root, sb);
        CheetahASTLog.Info("InsertSingleRowDefaultAST:", res);
    }

    /**
     * 输出Insert语句
     * @param context
     */
    public static void InsertASTPrint(ASTContext context) {
        if (!context.getName().equals("INSERT")) {
            CheetahASTLog.Info("Not Insert,It's", context.getName());
            return;
        }
        BaseAST baseAST = context.getAst();
        if (baseAST == null) {
            CheetahASTLog.Info("InsertSingleRowDefaultASTPrint: bastAST=", baseAST);
            return;
        }
        ASTNode root = baseAST.getRoot();
        if (root == null) {
            CheetahASTLog.Info("InsertSingleRowDefaultASTPrint: root=", root);
            return;
        }
        StringBuilder sb = new StringBuilder();
        String res = ASTUtils.TreeNodeDfsPrintUtils(root, sb);
        CheetahASTLog.Info("InsertSingleRowDefaultAST:", res);
    }
}
