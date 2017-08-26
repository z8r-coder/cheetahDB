package Models;

import Engine.Bplustree;
import Parser.Visitor.SchemaStatVisitor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by rx on 2017/8/19.
 * 表bean
 */
public class Table implements Serializable{
    /**
     * 表名
     */
    private String tableName;
    /**
     * 主键
     */
    private List<Column> PRIMARY_KEY = new ArrayList<Column>(4);
    /**
     * 索引
     */
    private List<Column> INDEX = new ArrayList<Column>(4);
    /**
     * 列
     */
    private List<Column> columns = new ArrayList<Column>();

    /**
     * 行
     */
    private List<Row> rows = new ArrayList<Row>();
    /**
     * 不可空的列
     */
    private List<Column> notNull = new ArrayList<Column>();

    /**
     * 唯一的列
     */
    private List<Column> unique = new ArrayList<Column>();
    /**
     * 索引树
     */
    private Map<String, Bplustree> INDEX_TREE = new HashMap<String, Bplustree>();

    /**
     * 名字映射对应的列
     */
    private Map<String, Column> columnMap = new HashMap<String, Column>();

    public Table(String tableName, List<Column> columns) {
        this.tableName = tableName;
        this.columns = columns;

        for (Column column : columns) {
            columnMap.put(column.getName(), column);
        }
    }

    public void setColumns(List<Column> columns) {
        this.columns = columns;
    }

    public List<Column> getColumns() {
        return columns;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setINDEX(List<Column> INDEX) {
        this.INDEX = INDEX;
    }

    public List<Column> getINDEX() {
        return INDEX;
    }

    public void setPRIMARY_KEY(List<Column> PRIMARY_KEY) {
        this.PRIMARY_KEY = PRIMARY_KEY;
    }

    public List<Column> getPRIMARY_KEY() {
        return PRIMARY_KEY;
    }

    public void setINDEX_TREE(Map<String, Bplustree> INDEX_TREE) {
        this.INDEX_TREE = INDEX_TREE;
    }

    public void setNotNull(List<Column> notNull) {
        this.notNull = notNull;
    }

    public List<Column> getNotNull() {
        return notNull;
    }

    public Map<String, Bplustree> getINDEX_TREE() {
        return INDEX_TREE;
    }

    public void setUnique(List<Column> unique) {
        this.unique = unique;
    }

    public List<Column> getUnique() {
        return unique;
    }

    /**
     * 主键集合中是否包含该列
     * @param column
     * @return
     */
    public boolean primaryContain(Column column) {
        for (Column col : PRIMARY_KEY) {
            if (col.equals(column)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 添加主键
     * @param pri_key
     */
    public void addPrimaryKey(Column pri_key) {
        PRIMARY_KEY.add(pri_key);
    }

    /**
     * 删除主键
     */
    public void removePrimaryKey(Column pri_key) {
        PRIMARY_KEY.remove(pri_key);
    }

    /**
     * 添加索引
     * @param index
     */
    public void addIndex(Column index) {
        INDEX.add(index);
    }

    /**
     * 删除索引
     * @param index
     */
    public void removeIndex(Column index) {
        INDEX.remove(index);
    }

    /**
     * 添加索引树
     * @param bplustree
     */
    public void putIndexTree(String name,Bplustree bplustree) {
        INDEX_TREE.put(name, bplustree);
    }

    /**
     * 添加不可空的列
     * @param column
     */
    public void addNotNull (Column column) {
        notNull.add(column);
    }

    /**
     * 添加行空间
     * @param row
     */
    public void addRow(Row row) {
        rows.add(row);
    }

    /**
     * 添加唯一约束的列
     * @param column
     */
    public void addUnique(Column column) {
        unique.add(column);
    }
}
