package parser.astgen;

import parser.ASTNode;

/**
 * 约束 ast
 * Created by ruanxin on 2017/8/12.
 */
public class SQLCtnsAst {
    private ASTNode astNode;

    public SQLCtnsAst (ASTNode astNode) {
        this.astNode = astNode;
    }

    public ASTNode getAstNode() {
        return astNode;
    }

    public void setAstNode(ASTNode astNode) {
        this.astNode = astNode;
    }
}
