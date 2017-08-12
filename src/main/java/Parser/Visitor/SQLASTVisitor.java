package Parser.Visitor;

import Parser.AstGen.*;

/**
 * Created by roy on 2017/8/7.
 */
public interface SQLASTVisitor {
    public void visit(SQLCreateTabAST ast);

    public void visit(SQLCreateDbAst ast);

    public void visit(SQLSelectAst ast);

    public void visit(SQLUseAst ast);

    public void visit(SQLInsertAst ast);

    public void visit(SQLUpdateAst ast);

    public void visit(SQLDeleteAst ast);

    public void visit(SQLShowDbAst ast);

    public void visit(SQLAlterAst ast);

    public void visit(SQLParaList ast);

    public void visit(SQLRltAst ast);

    public void visit(SQLCtnsAst ast);
}
