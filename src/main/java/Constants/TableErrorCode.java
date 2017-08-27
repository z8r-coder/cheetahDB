package Constants;

/**
 * 表空间错误码
 * Created by rx on 2017/8/27.
 */
public enum TableErrorCode {
    TABLE00000("TABLE00000", "表操作成功"),
    TABLE00001("TABLE00001", "主键冲突"),
    TABLE00002("TABLE00002", "UNIQUE冲突"),
    TABLE00003("TABLE00003", "NOTNULL ERROR")
    ;

    /**
     * 表空间错误码
     */
    private String tableErrorCode;

    /**
     * 表空间错误描述
     */
    private String tableErrorDes;

    TableErrorCode(String tableErrorCode, String des) {
        this.tableErrorCode = tableErrorCode;
        this.tableErrorDes = des;
    }

    public String getTableErrorCode() {
        return tableErrorCode;
    }

    public void setTableErrorCode(String tableErrorCode) {
        this.tableErrorCode = tableErrorCode;
    }

    public String getTableErrorDes() {
        return tableErrorDes;
    }

    public void setTableErrorDes(String tableErrorDes) {
        this.tableErrorDes = tableErrorDes;
    }
}
