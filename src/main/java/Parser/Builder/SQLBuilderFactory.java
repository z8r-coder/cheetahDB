package Parser.Builder;

import Parser.Builder.impl.SQLCreateTabBuilderImpl;

/**
 * Created by ruanxin on 2017/8/16.
 */
public class SQLBuilderFactory {
    private final static SQLBuilderFactory sbf = new SQLBuilderFactory();
    private SQLBuilderFactory(){}
    public static SQLBuilderFactory getInstance() {
        return sbf;
    }

    public SQLCreateTabBuilder createCrtTabBuilder(String sql) {
        return new SQLCreateTabBuilderImpl();
    }
}
