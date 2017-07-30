package Parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by roy on 2017/7/29.
 */
public enum SQLDDLPattern {
    COLUMNNUMPattern("INT", SortCode.IDENTIFIED, SortCode.INT, SortCode.LPARENT, SortCode.NUMBER,SortCode.RPARENT),
    COLUMNVARPattern("VARCHAR", SortCode.IDENTIFIED, SortCode.VARCHAR, SortCode.LPARENT, SortCode.NUMBER, SortCode.RPARENT),
    COLUMNINTEGERPattern("INTEGER", SortCode.IDENTIFIED, SortCode.INTEGER, SortCode.LPARENT, SortCode.NUMBER, SortCode.RPARENT),
    COLUMNCHARPattern("CHAR", SortCode.IDENTIFIED, SortCode.CHAR, SortCode.LPARENT, SortCode.NUMBER, SortCode.RPARENT),

    ;
    /**
     * 该枚举类的描述
     */
    String name;
    /**
     * 标识符
     */
    SortCode identified;
    /**
     * 类型
     */
    SortCode type;
    /**
     * 左括号
     */
    SortCode lp;
    /**
     * 数字
     */
    SortCode num;
    /**
     * 右括号
     */
    SortCode rp;

    static Map<String, List<SortCode>> pattern = new HashMap<String, List<SortCode>>();
    
    /**
     * 将固定句型存储起来
     */
    static {
        for (SQLDDLPattern ddl : SQLDDLPattern.values()) {
            List<SortCode> list = new ArrayList<SortCode>();
            list.add(ddl.getIdentified());
            list.add(ddl.getType());
            list.add(ddl.getLp());
            list.add(ddl.getNum());
            list.add(ddl.getRp());
            pattern.put(ddl.getName(), list);
        }
    }

    SQLDDLPattern(String name, SortCode identified, SortCode type, SortCode lparent, SortCode number, SortCode rparent) {
        this.name = name;
        this.lp = lparent;
        this.rp = rparent;
        this.type = type;
        this.num = number;
        this.identified = identified;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SortCode getIdentified() {
        return identified;
    }

    public void setIdentified(SortCode identified) {
        this.identified = identified;
    }

    public SortCode getType() {
        return type;
    }

    public void setType(SortCode type) {
        this.type = type;
    }

    public SortCode getLp() {
        return lp;
    }

    public void setLp(SortCode lp) {
        this.lp = lp;
    }

    public SortCode getNum() {
        return num;
    }

    public void setNum(SortCode num) {
        this.num = num;
    }

    public SortCode getRp() {
        return rp;
    }

    public void setRp(SortCode rp) {
        this.rp = rp;
    }
}
