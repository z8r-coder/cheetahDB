package Parser.Builder.impl;

import Parser.Builder.SQLBuilder;
import Parser.Builder.SQLShowDbBuilder;
/**
 * Created by ruanxin on 2017/8/16.
 */
public class SQLShowDbBuilderImpl implements SQLShowDbBuilder {


    public void build(String sql) throws Exception {

    }

    public String grammerType() {
        return "ShowDb";
    }
}
