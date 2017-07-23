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
        keywords.put("ALERT", SortCode.ALERT);

    }

    public static SortCode getValue(String key) {
        key = key.toUpperCase();
        return keywords.get(key);
    }
}
