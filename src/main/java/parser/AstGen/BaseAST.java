package parser.AstGen;

import parser.Visitor.SQLASTVisitor;

/**
 * Created by ruanxin on 2017/8/9.
 */
public interface BaseAST extends SQLAst {
    public void accept(SQLASTVisitor visitor) throws Exception;
}
