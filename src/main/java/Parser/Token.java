package Parser;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by roy on 2017/7/9.
 */
public enum Token {
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
    COMMA(","),
    LPARENT("("),
    RPARENT(")"),
    COLON(";"),
    LT("<"),
    GT(">"),
    EQ("="),
    NEQ("!="),
    GTET(">="),
    LTET("<="),
    ;
    static Map<String, Token> tokenMap = new HashMap<String, Token>();
    static {
        tokenMap.put("SELECT",Token.SELECT);
        
    }

    public final String name;
    Token(){
        this(null);
    }
    Token(String name) {
        this.name = name;
    }
}
