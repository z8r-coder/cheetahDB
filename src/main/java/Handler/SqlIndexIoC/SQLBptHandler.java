package Handler.SqlIndexIoC;

import Engine.BPlusTree.BplustreeImpl;
import Engine.BPlusTree.Node;
import Engine.Bplustree;
import Models.Column;
import Models.DataBase;
import Models.Row;
import Models.Table;
import Parser.AST;
import Parser.Builder.SQLBuilderWraper;
import Parser.Builder.SQLCreateTabBuilder;
import Parser.SQLASTType;
import Parser.SQLParserUtils;
import Support.Logging.Log;
import Support.Logging.LogFactory;
import Utils.ASTTestUtils;

import java.util.List;

/**
 * Created by rx on 2017/8/25.
 * 语句handler
 */
public class SQLBptHandler implements SQLHandler {
    private DataBase db;
    private final static Log LOG = LogFactory.getLog(ASTTestUtils.class);

    public SQLBptHandler(DataBase db) {
        this.db = db;
    }
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
            String tableName = createTabBuilder.tableName();
            List<Column> columns = createTabBuilder.Columns();
            //生成表空间
            Table table = new Table(tableName, columns);

            for (Column column : columns) {
                if (column.getPrimaryKey()) {
                    //主键默认不可空，默认唯一，目前支持一个主键
                    table.addPrimaryKey(column);
                    table.addIndex(column);
                } else if (column.getNotNull()) {
                    //保持列和主键的唯一不重复
                    if (!table.primaryContain(column)) {
                        table.addNotNull(column);
                    }

                } else if (column.getUnique()) {
                    if (!table.primaryContain(column)) {
                        table.addUnique(column);
                    }
                }
            }
            //建立主键索引树
            Node<List<Row>> root = new Node(true, true);
            Bplustree bpt = new BplustreeImpl(6, root);

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
