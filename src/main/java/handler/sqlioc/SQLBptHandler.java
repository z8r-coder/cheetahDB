package handler.sqlioc;

import engine.membplustree.BplustreeImpl;
import engine.membplustree.Node;
import engine.Bplustree;
import models.Column;
import models.DataBase;
import models.Row;
import models.Table;
import parser.AST;
import parser.builder.*;
import parser.SQLASTType;
import parser.SQLParserUtils;
import executor.sqlexecutor.deleteexecutor.DeleteAllService;
import executor.sqlexecutor.deleteexecutor.DeleteService;
import executor.sqlexecutor.deleteexecutor.DeleteWithWhereService;
import executor.sqlexecutor.insertexecutor.*;
import executor.sqlexecutor.selectexecutor.SelectOnly;
import executor.sqlexecutor.selectexecutor.SelectService;
import executor.sqlexecutor.selectexecutor.SelectWithWhereService;
import executor.sqlexecutor.updateservice.UpdateService;
import executor.sqlexecutor.updateservice.UpdateWithWhereService;
import executor.sqlexecutor.updateservice.UpdateWithoutWhereService;
import support.logging.Log;
import support.logging.LogFactory;
import utils.ASTTestUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by rx on 2017/8/25.
 * 语句handler
 */
public class SQLBptHandler implements SQLHandler {
    /**
     * 插入服务
     */
    private Map<SQLASTType, InsertService> insertServiceMap = new HashMap<SQLASTType, InsertService>();

    /**
     * 删除服务
     */
    private Map<SQLASTType, DeleteService> deleteServiceMap = new HashMap<SQLASTType, DeleteService>();

    /**
     * 更新服务
     */
    private Map<SQLASTType, UpdateService> updateServiceMap = new HashMap<SQLASTType, UpdateService>();

    /**
     * 查询服务
     */
    private Map<SQLASTType, SelectService> selectServiceMap = new HashMap<SQLASTType, SelectService>();

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
                    if (table.getPRIMARY_KEY() == null) {
                        table.setPRIMARY_KEY(column);
                        table.addIndex(column);
                    }
                } else if (column.getNotNull() && !column.getPrimaryKey()) {
                    //保持不可空列和主键的唯一不重复
                    table.addNotNull(column);
                } else if (column.getUnique() && !column.getPrimaryKey()) {
                    table.addUnique(column);
                }
            }
            //建立主键索引树
            for (Column column : table.getINDEX()) {
                Node<Row> root = new Node(true, true);
                Bplustree bpt = new BplustreeImpl(6, root);

                table.putIndexTree(column.getName(), bpt);
            }

        } catch (Exception e) {
            LOG.error("Create Table ERROR", e);
        }
    }

    public void executeInsert(SQLBuilderWraper builderWraper) {
        try {
            SQLInsertBuilder insertBuilder = (SQLInsertBuilder) builderWraper.getSQLBuilder();
            String tableName = insertBuilder.from();

            Table table = db.getTable(tableName);

            SQLASTType type = insertBuilder.grammerType();

            //初始化
            initInsert();

            LOG.info("开始执行:" + type.toString() + ":语句");
            insertServiceMap.get(type).invoke(table, insertBuilder);
            LOG.info(type + ":执行完毕");
        } catch (Exception e) {
            LOG.error("INSERT ERROR", e);
        }
    }

    public void executeDelete(SQLBuilderWraper builderWraper) {
        try {
            SQLDeleteBuilder deleteBuilder = (SQLDeleteBuilder) builderWraper.getSQLBuilder();
            String tableName = deleteBuilder.from();

            Table table = db.getTable(tableName);

            SQLASTType type = deleteBuilder.grammerType();

            //初始化
            initDelete();
            LOG.info("开始执行:" + type.toString() + ":语句");
            deleteServiceMap.get(type).invoke(table, deleteBuilder);
            LOG.info(type + ":执行完毕");
        } catch (Exception e) {
            LOG.error("DELETE ERROR", e);
        }
    }

    public void executeSelect(SQLBuilderWraper builderWraper) {
        try {
            SQLSelectBuilder selectBuilder = (SQLSelectBuilder) builderWraper.getSQLBuilder();
            String tableName = selectBuilder.from();

            Table table = db.getTable(tableName);

            SQLASTType type = selectBuilder.grammerType();

            //初始化
            initSelect();
            LOG.info("开始执行:" + type.toString() + ":语句");
            selectServiceMap.get(type).invoke(table, selectBuilder);
            LOG.info(type + ":执行完毕");
        } catch (Exception e) {
            LOG.error("SELECT ERROR",e);
        }
    }

    public void executeUpdate(SQLBuilderWraper builderWraper) {
        try {
            SQLUpdateBuilder updateBuilder = (SQLUpdateBuilder) builderWraper.getSQLBuilder();
            String tableName = updateBuilder.from();

            Table table = db.getTable(tableName);

            SQLASTType type = updateBuilder.grammerType();

            //初始化
            initUpdate();
            LOG.info("开始执行:" + type.toString() + ":语句");
            updateServiceMap.get(type).invoke(table, updateBuilder);
            LOG.info(type + ":执行完毕");
        } catch (Exception e) {
            LOG.error("UPDATE ERROR", e);
        }
    }

    public void executeShowDB(SQLBuilderWraper builderWraper) {

    }

    public void executeUseDB(SQLBuilderWraper builderWraper) {

    }

    public void executeCreateDB(SQLBuilderWraper builderWraper) {

    }

    public void executeShowTable(SQLBuilderWraper builderWraper) {

    }

    /**
     * 初始化表驱动
     */
    private void initInsert() {
        insertServiceMap.put(SQLASTType.INSERT_MULT_DEFAULT, new InsertMultiDefaultService());
        insertServiceMap.put(SQLASTType.INSERT_MULT, new InsertMultiService());
        insertServiceMap.put(SQLASTType.INSERT_SINGLE, new InsertSingleService());
        insertServiceMap.put(SQLASTType.INSERT_SINGLE_DEFAULT, new InsertSingleDefaultService());
    }

    private void initDelete() {
        deleteServiceMap.put(SQLASTType.DELETE_ALL, new DeleteAllService());
        deleteServiceMap.put(SQLASTType.DELETE_WITH_WHERE, new DeleteWithWhereService());
    }

    private void initSelect() {
        selectServiceMap.put(SQLASTType.SELECT_ONLY, new SelectOnly());
        selectServiceMap.put(SQLASTType.SELECT_WITH_WHERE, new SelectWithWhereService());
    }

    private void initUpdate() {
        updateServiceMap.put(SQLASTType.UPDATE_WITH_WHERE, new UpdateWithWhereService());
        updateServiceMap.put(SQLASTType.UPDATE_WITHOUT_WHERE, new UpdateWithoutWhereService());
    }
}
