package parser;

import support.logging.Log;
import support.logging.LogFactory;

import java.util.List;

/**
 * Created by roy on 2017/8/6.
 */
public class SQLParserUtils {
    String io_file;//file文件暂时内置

    private final static Log log = LogFactory.getLog(SQLParserUtils.class);
    /**
     * 获取token流
     * @return
     */
    public static List<Token> getToken(String sql) {
        Lexer lexer = new Lexer(sql);
        try {
            List<Token> tokens = lexer.getTokenStream();
            log.info(tokens);
            return tokens;
        } catch (Exception e) {
            log.error("getToken wrong sql=", sql, e);
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
//        } catch (exception e) {
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
            log.error("AssSQlASTgen failed sql = ", sql , e);
        }
        return null;
    }

    public static String getSQLType(String sql) {
        //作为表驱动的key
        List<Token> tokens = getToken(sql);
        SortCode sortCode = tokens.get(0).getSortCode();
        switch (sortCode) {
            case CREATE:
                SortCode sortCode_2 = tokens.get(1).getSortCode();
                if (sortCode_2 == SortCode.TABLE) {
                    return "CREATE_TABLE";
                } else if (sortCode_2 == SortCode.DATABASE) {
                    return "CREATE_DATABASE";
                }
                break;
            case SELECT:
                return "SELECT";
            case UPDATE:
                return "UPDATE";
            case DELETE:
                return "DELETE";
            case INSERT:
                return "INSERT";
            case SHOW:
                return "SHOW_DB";
            case USE:
                return "USE";
            default:
                break;
        }
        return null;
    }
}
