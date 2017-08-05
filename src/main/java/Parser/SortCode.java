package Parser;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by roy on 2017/7/9.
 */
public enum SortCode {
    SELECT("SELECT"),
    FROM("FROM"),
    WHERE("WHERE"),
    AND("AND"),
    OR("OR"),
    INSERT("INSERT"),
    VALUES("VALUES"),
    INTO("INTO"),
    BETWEEN("BETWEEN"),
    LIKE("LIKE"),
    UPDATE("UPDATE"),
    SET("SET"),
    DELETE("DELETE"),
    CREATE("CREATE"),
    DATABASE("DATABASE"),
    TABLE("TABLE"),
    NOT("NOT"),
    NULL("NULL"),
    INT("INT"),
    VARCHAR("VARCHAR"),
    PRIMARY("PRIMARY"),
    KEY("KEY"),
    NUMBER("NUMBER"),
    ALERT("ALERT"),
    IDENTIFIED("IDENTIFIED"),
    STRING("STRING"),
    UNIQUE("UNIQUE"),
    INTEGER("INTEGER"),
    SMALLINT("SMALLINT"),
    TINYINT("TINYINT"),
    CHAR("CHAR"),
    FOREIGN("FOREIGN"),
    REFERENCES("REFERENCES"),
    CHECK("CHECK"),
    GROUP("GROUP"),
    BY("BY"),
    HAVING("HAVING"),
    ORDER("ORDER"),
    ASC("ASC"),
    DESC("DESC"),
    AVG("AVG"),
    COUNT("COUNT"),
    MAX("MAX"),
    MIN("MIN"),
    SUM("SUM"),
    COMMA(","),
    LPARENT("("),
    RPARENT(")"),
    SEMICOLON(";"),
    LT("<"),
    GT(">"),
    EQ("="),
    NEQ("!="),
    GTET(">="),
    LTET("<="),
    STAR("*"),
    BLP("{"),
    BRP("}"),
    RS("<|>|==|=|!=|>=|<="),
    ;
    static Map<String, SortCode> tokenMap = new HashMap<String, SortCode>();
    static {
        for (SortCode sortCode : SortCode.values()) {
            tokenMap.put(sortCode.getName(), sortCode);
        }
    }

    public final String name;

    SortCode(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
