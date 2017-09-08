package parser.Builder;

import parser.SQLParserUtils;
import support.logging.Log;
import support.logging.LogFactory;
import exception.NotSupportTheASTType;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by ruanxin on 2017/8/17.
 */
public class SQLBuilderWraper {
    private String sql;

    private static Map<String, SQLBuilder> builderMap = new HashMap<String, SQLBuilder>();

    private final static Log log = LogFactory.getLog(SQLBuilderWraper.class);
    static {
        builderMap.put("CREATE_TABLE", SQLBuilderFactory.createCrtTabBuilder());
        builderMap.put("CREATE_DATABASE", SQLBuilderFactory.createDbBuilder());
        builderMap.put("SELECT", SQLBuilderFactory.createSelectBuilder());
        builderMap.put("UPDATE", SQLBuilderFactory.createUpdateBuilder());
        builderMap.put("DELETE", SQLBuilderFactory.createDeleteBuilder());
        builderMap.put("INSERT", SQLBuilderFactory.createInsertBuilder());
        builderMap.put("SHOW_DB", SQLBuilderFactory.createShowBuilder());
        builderMap.put("USE", SQLBuilderFactory.createUseDbBuilder());
    }
    public SQLBuilderWraper (String sql) {
        this.sql = sql;
    }

    public String getAstType () {
        return SQLParserUtils.getSQLType(sql);
    }

    public SQLBuilder getSQLBuilder() throws Exception {
        String astType = getAstType();

        if (builderMap.get(astType) == null) {
            log.error("don't support this type, please check the sql!");
            throw new NotSupportTheASTType(getClass().toString());
        }

        builderMap.get(astType).build(sql);
        return builderMap.get(astType);
    }
    
}
