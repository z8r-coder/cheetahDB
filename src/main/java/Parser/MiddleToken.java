package Parser;

/**
 * 中间节点
 * Created by ruanxin on 2017/8/12.
 */
public class MiddleToken extends Token {
    private String value;
    public MiddleToken (String value) {
        this.value = value;
    }
    public MiddleToken(SortCode sortCode, String value, int line) {
        super(sortCode, value, line);
    }

    @Override
    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }
}
