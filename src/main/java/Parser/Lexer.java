package Parser;

import javafx.css.StyleOrigin;
import org.omg.PortableServer.POA;

/**
 * Created by roy on 2017/7/9.
 */
public class Lexer {
    private int pos;//位置
    private String sql;// 输入sql语句
    private char ch;//当前字符
    private  String str;//当前串
    private int line;//行
    private final byte ICMask = (byte) 0xDF;
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

    public boolean isEOF() {
        return pos >= sql.length();
    }

    /**
     * 接受关键字
     */
    public Token acceptKeyWords() {
        char currentChar = charAt(pos);
        int savePoint = pos;
        switch (currentChar) {
            case 'S':
            case 's':
                if (icNextCharIs('E') & icNextCharIs('T') & isNextBlank()) {
                    pos++;
                    return new Token(SortCode.SET, "SET");
                }
                pos = savePoint;
                if (icNextCharIs('E') & icNextCharIs('L')
                        & icNextCharIs('E') & icNextCharIs('C')
                        & icNextCharIs('T') & (isNextBlank() || isNextLP())) {
                    pos++;
                    return new Token(SortCode.SELECT, "SELECT");
                }
                pos = savePoint;
                return null;
            case 'I':
            case 'i':
                if (icNextCharIs('N') & icNextCharIs('T')
                        & isNextLP()) {
                    pos++;
                    return new Token(SortCode.INT, "INT");
                }
                pos = savePoint;
                if (icNextCharIs('N') & icNextCharIs('T')
                        & icNextCharIs('O') & isNextBlank()) {
                    pos++;
                    return new Token(SortCode.INTO, "INTO")
                }
                pos = savePoint;
                if (icNextCharIs('N') & icNextCharIs('S')
                        & icNextCharIs('E') & icNextCharIs('R')
                        & icNextCharIs('T') & isNextBlank()) {
                    pos++;
                    return new Token(SortCode.INSERT, "INSERT");
                }
                 pos = savePoint;
                return null;
            case 'D':
            case 'd':
                if (icNextCharIs('E') & icNextCharIs('L')
                        & icNextCharIs('E') & icNextCharIs('T')
                        & isNextBlank()) {
                    pos++;
                    return new Token(SortCode.DELETE, "DELETE");
                }
                pos = savePoint;
                return null;
            case 'U':
            case 'u':
                if (icNextCharIs('U') & icNextCharIs('P')
                        & icNextCharIs('D') & icNextCharIs('A')
                        & icNextCharIs('T') & icNextCharIs('E')
                        & isNextBlank()) {
                    pos++;
                    return new Token(SortCode.UPDATE, "UPDATE");
                }
                pos = savePoint;
                return null;
            case 'C':
            case 'c':
                if (icNextCharIs('R') & icNextCharIs('E')
                        & icNextCharIs('A') & icNextCharIs('T')
                        & icNextCharIs('E') & isNextBlank()) {
                    pos++;
                    return new Token(SortCode.CREATE, "CREATE");
                }
                pos = savePoint;
                return null;
            case 'F':
            case 'f':
                if (icNextCharIs('R') & icNextCharIs('O')
                        & icNextCharIs('M') & (isNextBlank() || isNextLP())) {
                    pos++;
                    return new Token(SortCode.FROM, "FROM");
                }
                pos = savePoint;
                return null;
            case 'W':
            case 'w':
                if (icNextCharIs('H') & icNextCharIs('E')
                        & icNextCharIs('R') & icNextCharIs('E')
                        & isNextBlank()) {
                    pos++;
                    return new Token(SortCode.WHERE, "WHERE");
                }
                pos = savePoint;
                return null;
            case 'A':
            case 'a':
                if (icNextCharIs('N') & icNextCharIs('D') & isNextBlank()) {
                    pos++;
                    return new Token(SortCode.AND, "AND");
                }
                pos = savePoint;
                return null;
            case 'O':
            case 'o':
                if (icNextCharIs('R') & isNextBlank()) {
                    pos++;
                    return new Token(SortCode.OR, "OR");
                }
                pos = savePoint;
                return null;
            case 'V':
            case 'v':
                if (icNextCharIs('A') & icNextCharIs('R')
                        & icNextCharIs('C') & icNextCharIs('H')
                        & icNextCharIs('A') & icNextCharIs('R')
                        & (isNextLP() || isNextBlank())) {
                    pos++;
                    return new Token(SortCode.VARCHAR, "VARCHAR");
                }
                pos = savePoint;
                if (icNextCharIs('A') & icNextCharIs('L')
                        & icNextCharIs('U') & icNextCharIs('E')
                        & icNextCharIs('S') & isNextBlank()) {
                    pos++;
                    return new Token(SortCode.VALUES, "VALUES");
                }
                pos = savePoint;
                return null;

            default:
                return null;
        }
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

    /**
     *  是否是运算符
     * @param ch
     * @return
     */
    private boolean isOp(char ch) {
        switch (ch) {
            case ',':
            case '(':
            case ')':
            case ';':
            case '<':
            case '>':
            case '=':
            case '!':
                return true;
            default:
                return false;
        }
    }
}
