package Parser;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by roy on 2017/7/9.
 */
public class Keywords {
    private static Map<String, SortCode> keywords = new HashMap<String, SortCode>();
    static {
        keywords.put("SELECT",SortCode.SELECT);
        keywords.put("FROM", SortCode.FROM);
        keywords.put("WHERE", SortCode.WHERE);
        keywords.put("AND", SortCode.AND);
        keywords.put("OR", SortCode.OR);
        keywords.put("INSERT",SortCode.INSERT);
        keywords.put("VALUES",SortCode.VALUES);
        keywords.put("INTO", SortCode.INTO);
        keywords.put("BETWEEN", SortCode.BETWEEN);
        keywords.put("LIKE", SortCode.LIKE);
        keywords.put("UPDATE", SortCode.UPDATE);
        keywords.put("SET", SortCode.SET);
        keywords.put("DELETE", SortCode.DELETE);
        keywords.put("CREATE",SortCode.CREATE);
        keywords.put("DATABASE", SortCode.DATABASE);
        keywords.put("TABLE", SortCode.TABLE);
        keywords.put("NOT", SortCode.NOT);
        keywords.put("NULL", SortCode.NULL);
        keywords.put("INT", SortCode.INT);
        keywords.put("VARCHAR", SortCode.VARCHAR);
        keywords.put("PRIMARY", SortCode.PRIMARY);
        keywords.put("KEY", SortCode.KEY);
        keywords.put("ALERT", SortCode.ALTER);
        keywords.put("UNIQUE", SortCode.UNIQUE);
        keywords.put("INTEGER", SortCode.INTEGER);
        keywords.put("SMALLINT", SortCode.SMALLINT);
        keywords.put("TINYINT", SortCode.TINYINT);
        keywords.put("CHAR", SortCode.CHAR);
        keywords.put("REFERENCES", SortCode.REFERENCES);
        keywords.put("PRIMARY", SortCode.PRIMARY);
        keywords.put("CHECK", SortCode.CHECK);
        keywords.put("GROUP", SortCode.GROUP);
        keywords.put("BY",SortCode.BY);
        keywords.put("HAVING", SortCode.HAVING);
        keywords.put("ORDER", SortCode.ORDER);
        keywords.put("ASC", SortCode.ASC);
        keywords.put("DESC", SortCode.DESC);
        keywords.put("AVG", SortCode.AVG);
        keywords.put("COUNT", SortCode.COUNT);
        keywords.put("MAX", SortCode.MAX);
        keywords.put("MIN", SortCode.MIN);
        keywords.put("SUM", SortCode.SUM);
        keywords.put("IN", SortCode.IN);
        keywords.put("DROP", SortCode.DROP);
        keywords.put("DATABASES", SortCode.DATABASES);
    }

    public static SortCode getValue(String key) {
        key = key.toUpperCase();
        return keywords.get(key);
    }
}
