package models;

import java.util.List;

/**
 * Created by rx on 2017/8/30.
 * 执行选择
 */
public class SimpleRow {
    /**
     * 筛选出来的值
     */
    List<Value> values;

    public SimpleRow () {

    }

    public SimpleRow(List<Value> values) {
        this.values = values;
    }

    public void setValues(List<Value> values) {
        this.values = values;
    }

    public List<Value> getValues() {
        return values;
    }
}
