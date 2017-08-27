package Parser;

/**
 * 20170813 暂且支持varchar 和 integer
 * Created by ruanxin on 2017/8/13.
 */
public enum SQLDataType {
    VARCHAR("VARCHAR","变长字符串"),
    INTEGER("INTEGER","整型"),
    NULL("NULL", "空类型"),
    ;
    /**
     * 类型
     */
    private String type;

    /**
     * 描述
     */
    private String desc;
    SQLDataType(String type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    public void setType(String type) {
        this.type = type;

    }

    public String getType() {
        return type;
    }
}
