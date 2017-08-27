package Constants;

/**
 * 操作错误码
 * Created by rx on 2017/8/27.
 */
public enum SQLErrorCode {
    SQL00000("SQL00000", "操作成功"),
    SQL00001("SQL00001", "单行缺省值插入失败"),
    SQL00002("SQL00002", "单行非缺省值插入失败"),
    SQL00003("SQL00003", "多行缺省值插入失败"),
    SQL00004("SQL00004", "多行非缺省值插入失败"),
    SQL00005("SQL00005", "带where的更新失败"),
    SQL00006("SQL00006", "不带where的更新失败"),
    SQL00007("SQL00007", "删除所有行失败"),
    SQL00008("SQL00008", "带where的删除失败"),
    SQL00009("SQL00010", "不带where的查询失败"),
    SQL00010("SQL00011", "带where的查询失败")
    ;

    /**
     * 错误码
     */
    private String errorCode;
    /**
     * 错误描述
     */
    private String errorDes;

    SQLErrorCode(String errorCode, String errorDes) {
        this.errorCode = errorCode;
        this.errorDes = errorDes;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorDes() {
        return errorDes;
    }

    public void setErrorDes(String errorDes) {
        this.errorDes = errorDes;
    }
}
