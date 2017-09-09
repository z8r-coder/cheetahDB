package parser.astgen;

import parser.visitor.SQLASTVisitor;

/**
 * Created by ruanxin on 2017/8/9.
 */
public interface BaseAST extends SQLAst {
    public void accept(SQLASTVisitor visitor) throws Exception;
}
