package Parser.Visitor;

import Parser.AstGen.*;

/**
 * Created by roy on 2017/8/7.
 */
public interface SQLASTVisitor {
    public void visit(SQLCreateTabAST ast) throws Exception;

    public void visit(SQLCreateDbAst ast) throws Exception;

    public void visit(SQLSelectAst ast) throws Exception;

    public void visit(SQLUseAst ast) throws Exception;

    public void visit(SQLInsertAst ast) throws Exception;

    public void visit(SQLUpdateAst ast) throws Exception;

    public void visit(SQLDeleteAst ast) throws Exception;

    public void visit(SQLShowDbAst ast) throws Exception;

    public void visit(SQLAlterAst ast) throws Exception;

    public void visit(SQLParaList ast) throws Exception;

    public void visit(SQLRltAst ast) throws Exception;

    public void visit(SQLCtnsAst ast) throws Exception;
}
