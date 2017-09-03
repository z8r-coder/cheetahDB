package Models;

import Parser.SQLDataType;
import Utils.StringUtils;

import java.io.Serializable;

/**
 * Created by rx on 2017/8/26.
 */
public class Value implements Comparable, Serializable {
    /**
     * 字符串值
     */
    private String          val;
    /**
     * 整形值
     */
    private Integer         intVal;
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

    //字符串类型构造
    public Value(String val, SQLDataType dataType) {
        this.val = val;
        this.dataType = dataType;
    }

    //整数类型构造
    public Value(Integer intVal, SQLDataType dataType) {
        this.intVal = intVal;
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

    public void setIntVal(Integer intVal) {
        this.intVal = intVal;
    }

    public Integer getIntVal() {
        return intVal;
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
        return true;
    }

    public int compareTo(Object object) {
        Value value = (Value) object;
        if (dataType == SQLDataType.INTEGER) {
            //此处默认比较类型相同
            return intVal.compareTo(value.getIntVal());
        } else if (dataType == SQLDataType.VARCHAR){
            return val.compareTo(value.getVal());
        } else {
            //null类型,null默认最小
            return 1;
        }
    }
}
