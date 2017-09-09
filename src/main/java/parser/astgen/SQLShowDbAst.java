package parser.astgen;

import parser.AST;
import parser.visitor.SQLASTVisitor;

/**
 * Created by ruanxin on 2017/8/12.
 */
public class SQLShowDbAst implements BaseAST{
    private AST ast;

    public SQLShowDbAst (AST ast) {
        this.ast = ast;
    }

    public void accept(SQLASTVisitor visitor) throws Exception {
        visitor.visit(this);
    }
}
