package Models;

import Parser.SQLDataType;
import Utils.StringUtils;

/**
 * Created by rx on 2017/8/26.
 */
public class Value implements Comparable {
    /**
     * 值
     */
    private String          val;
    /**
     * 数据类型
     */
    private SQLDataType     dataType;
    /**
     * 对应的列名
     */
    private String          columName;
    private boolean         in;
    private boolean         insert;

    public Value() {

    }

    public Value(String val, SQLDataType dataType) {
        this.val = val;
        this.dataType = dataType;
    }

    public void setIsIn(boolean in) {
        this.in = in;
    }

    public boolean getIsIn() {
        return in;
    }

    public String getVal() {
        return val;
    }

    public void setVal(String val) {
        this.val = val;
    }

    public void setIsInsert(boolean insert) {
        this.insert = insert;
    }

    public boolean getIsInsert() {
        return insert;
    }

    public void setColumName(String columName) {
        this.columName = columName;
    }

    public String getColumName() {
        return columName;
    }

    public void setDataType(SQLDataType dataType) {
        this.dataType = dataType;
    }

    public SQLDataType getDataType() {
        return dataType;
    }

    @Override
    public boolean equals(Object object) {
        Value value = (Value) object;
        if (!StringUtils.equals(val, value.getVal())) {
            return false;
        }

        if (!StringUtils.equals(columName, value.getColumName())) {
            return false;
        }

        if (in != value.getIsIn()) {
            return false;
        }

        if (insert != value.getIsInsert()) {
            return false;
        }
        return true;
    }

    public int compareTo(Object object) {
        Value value = (Value) object;
        return val.compareTo(value.getVal());
    }
}
