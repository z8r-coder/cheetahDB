package Parser;

import Log.CheetahASTLog;
import Parser.AstGen.BaseAST;

import java.util.List;
import java.util.Map;

/**
 * Created by roy on 2017/8/6.
 */
public class SQLParserUntil {
    String io_file;//file文件暂时内置

    /**
     * 获取token流
     * @return
     */
    public static List<Token> getToken(String sql) {
        Lexer lexer = new Lexer(sql);
        try {
            List<Token> tokens = lexer.getTokenStream();
            CheetahASTLog.Info("Lexer", tokens, ' ');
            return tokens;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 从文件获取sql并生成获取所有的AST
     * @return
     */
//    public static AST ASTgen() {
//
//        List<Token> tokens = getToken();
//        AST ast = new AST();
//        SQLParser sqlParser = new SQLParser(tokens, ast);
//        try {
//            sqlParser.managerSQL();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return sqlParser.getAst();
//    }

    /**
     * 通过外部输入sql语句
     * @param sql
     * @return
     */
    public static AST AssSQlASTgen(String sql) {
        try {
            List<Token> tokens = getToken(sql);
            AST ast = new AST();
            SQLParser sqlParser = new SQLParser(tokens, ast);
            sqlParser.managerSQL();
            return sqlParser.getAst();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
