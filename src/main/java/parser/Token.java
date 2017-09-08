package parser;

/**
 * Created by roy on 2017/7/17.
 */
public class Token {
    private SortCode sortCode;//种别码
    private String value;
    private int line;//所在行

    public Token(SortCode sortCode, String value, int line) {
        this.value = value;
        this.sortCode = sortCode;
        this.line = line;
    }

    public Token() {
    }

    public SortCode getSortCode() {
        return sortCode;
    }

    public void setSortCode(SortCode sortCode) {
        this.sortCode = sortCode;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public int getLine() {
        return line;
    }
}
