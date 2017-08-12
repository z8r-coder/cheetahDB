package Parser.Visitor;

import Parser.AstGen.SQLCreateTabAST;

/**
 * Created by roy on 2017/8/7.
 */
public interface SQLASTVisitor {
    public void visit(SQLCreateTabAST crtAst);
}
