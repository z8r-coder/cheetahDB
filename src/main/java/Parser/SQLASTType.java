package Parser;

/**
 * Created by royruan on 2017/8/11.
 */
public enum SQLASTType {
    //DQL，带group等的先不考虑
    SELECT_ONLY("SELECT_ONLY", "不带任何语句的SELECT"),
    SELECT_WITH_WHERE("SELECT_WITH_WHERE","带where简单基于关系的select语句"),
    SELECT_WITH_WHERE_IN("SELECT_WITH_WHERE_IN","带where简单基于in的select语句"),
    SELECT_WITH_SUB("SELECT_WITH_SUB", "带子查询的select语句"),

    //DDL
    //缺省和非缺省的预分类,中间态
    INSERT_DEFAULT("INSERT_DEFAULT", "缺省"),
    INSERT_NONE_DEFAULT("INSERT_NONE_DEFAULT", "非缺省"),
    //插入四状态
    INSERT_SINGLE_DEFAULT("INSERT_SINGLE_DEFAULT","单行缺省插入"),
    INSERT_SINGLE("INSERT_SINGLE", "单行非缺省插入"),
    INSERT_MULT_DEFAULT("INSERT_MULT_DEFAULT","多行缺省插入"),
    INSERT_MULT("INSERT_MULT", "多行非缺省插入"),

    //update和delete前期先不考虑复合查询
    UPDATE_WITHOUT_WHERE("UPDATE_WTHOUT_WHERE","不带where的更新"),
    UPDATE_WITH_WHERE("UPDATE_WITH_WHERE","带where，无in的更新"),
    UPDATE_WITH_WHERE_IN("UPDATE_WITH_WHERE_IN", "带where,in,不带sub的更新"),
    UPDATE_WITH_SUB("UPDATE_WITH_SUB","带where,带sub的更新"),

    DELETE_ALL("DELETE_ALL", "删除所有行，不带where"),
    DELETE_WITH_WHERE("DELETE_WITH_WHERE", "删除筛选后的行"),
    DELETE_WITH_WHERE_IN("DELETE_WITH_WHERE_IN", "删除筛选后的行，带in,不带sub"),
    DELETE_WITH_SUB("DELETE_WITH_SUB", "删除选后的行，带sub"),

    //DML
    CREATE_DATABASE("CREATE_DATABASE","创建数据库"),
    CREATE_TABLE("CREATE_TABLE","创建表"),
    SHOW_DATABASES("SHOW_DATABASES", "show所有存在的数据库"),
    SHOW_TABLES("SHOW_TABLES", "show所有的存在该数据库中的表"),
    USE_DATABASE("USE_DATABASE", "使用某个数据库"),
    DROP_TABLE("DROP_TABLE", "删除某个表"),
    DROP_DATABASE("DROP_DATABASE","删除某个数据库"),
    ALTER_TABLE_ADD("ALTER_TABLE_ADD", "给表增加行"),
    ALTER_TABLE_DROP("ALTER_TABLE_DROP", "给表删除某行"),
    ALTER_TABLE_ALTER("ALTER_TABLE_ALTER", "给表修改行"),
    ;
    /**
     * 该语句的类型
     */
    String type;
    /**
     * 描述
     */
    String desc;

    SQLASTType(String type, String desc) {
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
