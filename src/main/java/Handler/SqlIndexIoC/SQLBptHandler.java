package Handler.SqlIndexIoC;

import Models.Table;
import Parser.AST;
import Parser.Builder.SQLBuilderWraper;
import Parser.Builder.SQLCreateTabBuilder;
import Parser.SQLASTType;
import Parser.SQLParserUtils;
import Support.Logging.Log;
import Support.Logging.LogFactory;
import Utils.ASTTestUtils;

/**
 * Created by rx on 2017/8/25.
 * 语句handler
 */
public class SQLBptHandler implements SQLHandler {

    private final static Log LOG = LogFactory.getLog(ASTTestUtils.class);

    public void execute(String sql) {
        AST ast = SQLParserUtils.AssSQlASTgen(sql);
        SQLBuilderWraper builderWraper = new SQLBuilderWraper(sql);

        SQLASTType type = ast.getAstType();
        switch (type) {
            case SELECT_ONLY:
            case SELECT_WITH_WHERE:
                executeSelect(builderWraper);
                break;
            case DELETE_WITH_WHERE:
            case DELETE_ALL:
                executeDelete(builderWraper);
                break;
            case INSERT_MULT:
            case INSERT_MULT_DEFAULT:
            case INSERT_SINGLE:
            case INSERT_SINGLE_DEFAULT:
                executeInsert(builderWraper);
                break;
            case UPDATE_WITH_WHERE:
            case UPDATE_WITHOUT_WHERE:
                executeUpdate(builderWraper);
                break;
            case CREATE_DATABASE:
                executeCreateDB(builderWraper);
                break;
            case CREATE_TABLE:
                executeCreateTab(builderWraper);
                break;
            case SHOW_DATABASES:
                executeShowDB(builderWraper);
                break;
            case SHOW_TABLES:
                executeShowTable(builderWraper);
                break;
            default:
                LOG.error("暂且不支持该语句类型!");
                return;
        }
    }

    public void executeCreateTab(SQLBuilderWraper builderWraper) {
        try {
            SQLCreateTabBuilder createTabBuilder = (SQLCreateTabBuilder) builderWraper.getSQLBuilder();
            Table table = new Table();
            String tableName = createTabBuilder.tableName();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void executeInsert(SQLBuilderWraper builderWraper) {

    }

    public void executeDelete(SQLBuilderWraper builderWraper) {

    }

    public void executeSelect(SQLBuilderWraper builderWraper) {

    }

    public void executeShowDB(SQLBuilderWraper builderWraper) {

    }

    public void executeUpdate(SQLBuilderWraper builderWraper) {

    }

    public void executeUseDB(SQLBuilderWraper builderWraper) {

    }

    public void executeCreateDB(SQLBuilderWraper builderWraper) {

    }

    public void executeShowTable(SQLBuilderWraper builderWraper) {

    }
}
