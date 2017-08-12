package Parser;
import Exception.*;
import Support.Logging.Log;
import Support.Logging.LogFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by roy on 2017/7/9.
 */
public class Lexer {
    private int pos;//位置
    private String sql;// 输入sql语句
    private char ch;//当前字符
    private  String str;//当前串
    private int line = 0;//行
    private List<Token> tokenStream = new ArrayList<Token>();
    private final byte ICMask = (byte) 0xDF;

    private final static Log LOG = LogFactory.getLog(Lexer.class);

    //从文件读入sql语句
    public Lexer() {
        String url = Lexer.class.getClassLoader().getResource("testSql.txt").getFile();
        File file = new File(url);
        IOSystem io = new IOSystem();
        try {
            sql = io.readFromFile(file);
        } catch (IOException e) {
            LOG.error("readFrom File failed", e);
        }
    }
    //赋值sql语句
    public Lexer(String sql) {
        this.sql = sql;
    }
    public final char charAt(int pos) {
        if (pos >= sql.length()) {
            return LayoutCharacters.EOI;
        }
        return sql.charAt(pos);
    }

    protected final void scanChar() {
        ch = charAt(++pos);
    }
    protected final void unscanChar() {
        ch = charAt(--pos);
    }
    protected final char getCurrentChar() {
        return ch;
    }

    private void plusLine() {
        line++;
    }
    private void subLine() {
        line--;
    }
    private int getLine() {
        return line;
    }
    private final boolean icNextCharIs(char c) {
        byte s = (byte) (charAt(++pos) & ICMask);
        return s == c;
    }
    private final boolean isNextLP() {
        int tmp = pos;
        return charAt(++tmp) == '(';
    }
    private final boolean isNextBlank() {
        int tmp = pos;
        return charAt(++tmp) == ' ';
    }
    private final boolean readNum() {
        if (charAt(pos) >= '0' && charAt(pos) <='9') {
            return true;
        }
        return false;
    }

    private final boolean readStr() {
        if (charAt(pos) != '\'') {
            return true;
        }
        return false;
    }

    private final boolean readch() {
        if (isChar() || isUnderline()) {
            return true;
        }
        return false;
    }
    private final boolean isChar() {
        if ((charAt(pos) >= 'a' && charAt(pos) <= 'z')
                || (charAt(pos) >= 'A' && charAt(pos) <= 'Z')) {
            return true;
        }
        return false;
    }
    private final boolean isUnderline() {
        if (charAt(pos) == '_') {
            return true;
        }
        return false;
    }
    private final boolean isEOF() {
        if (pos < sql.length()) {
            return true;
        }
        return false;
    }

    /**
     * 生成token流
     */
    public List<Token> generateTokenStream() throws Exception {
        for (; pos < sql.length();pos++) {
            char tmp = charAt(pos);
            if (tmp == ' ' || charAt(pos) == '\t') {
                continue;
            }
            char tmp1 = charAt(pos);
            if (tmp1 == '\n') {
                plusLine();
                continue;
            }
            Token token = scan();
            tokenStream.add(token);
        }
        return tokenStream;
    }
    public Token scan() throws Exception {
        //接受关键词，id或数字,字符串
        StringBuilder sb = new StringBuilder();
        //接受字符串
        if (charAt(pos) == '\'') {
            pos++;
            while (isEOF() && readStr()) {
                sb.append(charAt(pos));
                pos++;
            }
        }
        if (!isEOF()) {
            throw new SytaxErrorsException("There is sytax errors in " + line + " !");
        }
        if (sb.length() != 0) {
            return new Token(SortCode.STRING, sb.toString(), line);
        }

        //第一个字符
        if (Op(charAt(pos)) != null) {
            return Op(charAt(pos));
        }
        //数字
        while(isEOF() && readNum()) {
            sb.append(charAt(pos));
            pos++;
        }
        if (sb.length() != 0) {
            pos--;
            return new Token(SortCode.NUMBER, sb.toString(), line);
        }
        //id和关键字
        while (isEOF() && readch()) {
            sb.append(charAt(pos));
            pos++;
        }
        if (sb.length() != 0) {
            if (Keywords.getValue(sb.toString()) != null) {
                pos--;
                return new Token(Keywords.getValue(sb.toString()), sb.toString(), line);
            }
            pos--;
            return new Token(SortCode.IDENTIFIED, sb.toString(), line);
        }
        throw new SytaxErrorsException("There is sytax errors in " + line + " !");
    }
    /**
     *  是否是运算符
     * @return
     */
    private Token Op(char ch) throws Exception {
        int tmp = pos;
        switch (ch) {
            case ',':
                return new Token(SortCode.COMMA, ",", line);
            case '(':
                return new Token(SortCode.LPARENT, "(", line);
            case ')':
                return new Token(SortCode.RPARENT, ")", line);
            case ';':
                return new Token(SortCode.SEMICOLON, ";", line);
            case '<':
                tmp = pos;
                if (charAt(++tmp) == '=') {
                    pos++;
                    return new Token(SortCode.LTET,"<=", line);
                }else {
                    return new Token(SortCode.LT, "<", line);
                }
            case '>':
                tmp = pos;
                if (charAt(++tmp) == '=') {
                    pos++;
                    return new Token(SortCode.GTET, ">=", line);
                }else {
                    return new Token(SortCode.GT, ">", line);
                }
            case '=':
                return new Token(SortCode.EQ, "=", line);
            case '!':
                tmp = pos;
                if (charAt(++tmp) == '=') {
                    pos++;
                    return new Token(SortCode.NEQ, "!=", line);
                }else {
                    throw new SytaxErrorsException("There is sytax error in " + line + " !");
                }
            case '*':
                return new Token(SortCode.STAR, "*", line);
            case '{':
                return new Token(SortCode.BLP, "{", line);
            case '}':
                return new Token(SortCode.BRP, "}", line);
            default:
                return null;
        }
    }
    public List<Token> getTokenStream() throws Exception {
        return generateTokenStream();
    }
}
