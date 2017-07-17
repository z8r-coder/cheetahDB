package Parser;

/**
 * Created by roy on 2017/7/17.
 */
public class Token {
    private SortCode sortCode;//种别码
    private String value;

    public Token(SortCode sortCode, String value) {
        this.value = value;
        this.sortCode = sortCode;
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
}
