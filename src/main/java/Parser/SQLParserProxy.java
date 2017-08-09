package Parser;

import Log.CheetahASTLog;
import Parser.AstGen.BaseAST;

import java.util.List;
import java.util.Map;

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
            List<Token> tokens = lexer.generateTokenStream();
            CheetahASTLog.Info("Lexer", tokens);
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
        try {
            sqlParser.managerSQL();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sqlParser.getAst();
    }
}
