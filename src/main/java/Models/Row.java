package Models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 行描述
 * Created by rx on 2017/8/26.
 */
public class Row {
    /**
     * 该行的主键
     */
    private Value PRIMARY_KEY;
    /**
     * 列名与值映射
     */
    private Map<String, Value> valueMap = new HashMap<String, Value>();
    /**
     * 该行的所有值
     */
    private List<Value> values;

    public Row() {

    }
    public Row(Value PRIMARY_KEY) {
        this.PRIMARY_KEY = PRIMARY_KEY;
    }

    public void setPRIMARY_KEY(Value PRIMARY_KEY) {
        this.PRIMARY_KEY = PRIMARY_KEY;
    }

    public Value getPRIMARY_KEY() {
        return PRIMARY_KEY;
    }

    public void setValues(List<Value> values) {
        this.values = values;
    }

    public List<Value> getValues() {
        return values;
    }

    public Value getValue(String name) {
        return valueMap.get(name);
    }

    public void putValue(String name, Value value) {
        valueMap.put(name, value);
    }

    public void putAllValue(List<Value> values) {

    }

    /**
     * 通过比较某一列的值
     * @param row
     * @param columnName
     * @return
     */
    public int compareTo(Row row, String columnName) {
        return valueMap.get(columnName).compareTo(row.getValue(columnName));
    }

    /**
     * 比较两行的主键是否相同
     * @param row
     * @return
     */
    public boolean equals(Row row) {
        return PRIMARY_KEY.equals(row.getPRIMARY_KEY());
    }
}
