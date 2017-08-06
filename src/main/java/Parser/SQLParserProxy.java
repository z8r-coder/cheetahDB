package Parser;

import java.util.List;

/**
 * Created by roy on 2017/8/6.
 */
public class SQLParserProxy {
    String io_file;//file文件暂时内置

    public AST generaterAST() throws Exception {
        Lexer lexer = new Lexer();
        List<Token> tokens = lexer.generateTokenStream();
        AST ast = new AST();
        SQLParser sqlParser = new SQLParser(tokens, ast);
        sqlParser.managerSQL();

        return sqlParser.getAst();
    }
}
