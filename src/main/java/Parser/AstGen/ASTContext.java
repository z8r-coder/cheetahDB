package Parser.AstGen;

/**
 * Created by ruanxin on 2017/8/9.
 */
public class ASTContext {
    /**
     * 语句名字
     */
    private String name;
    /**
     * 语法树
     */
    private BaseAST ast;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BaseAST getAst() {
        return ast;
    }

    public void setAst(BaseAST ast) {
        this.ast = ast;
    }
}
