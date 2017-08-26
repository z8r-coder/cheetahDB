package Models;

/**
 * Created by rx on 2017/8/26.
 */
public class Value {
    private String          val;
    private String          dataType;
    private boolean         in;
    private boolean         insert;

    public Value() {

    }

    public Value(String val, String dataType) {
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

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getDataType() {
        return dataType;
    }
}
