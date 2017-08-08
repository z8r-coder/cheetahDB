package Parser;

import Parser.AstGen.BaseAST;

import java.util.List;

/**
 * Created by roy on 2017/8/6.
 */
public class SQLParserProxy {
    String io_file;//file文件暂时内置

    /**
     * 获取token流
     * @return
     */
    public static List<Token> getToken() {
        Lexer lexer = new Lexer();
        try {
            return lexer.generateTokenStream();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取所有的AST
     * @return
     */
    public static AST ASTgen() {

        List<Token> tokens = getToken();
        AST ast = new AST();
        SQLParser sqlParser = new SQLParser(tokens, ast);
        sqlParser.managerSQL();
        return sqlParser.getAst();
    }

    /**
     * 向ast中注入结点集合
     * @param baseAST
     * @param astNode
     */
    public static void InjectCollection2Ast(BaseAST baseAST,ASTNode astNode) {
        baseAST.setCollection(astNode.getChildSet());
    }

}
