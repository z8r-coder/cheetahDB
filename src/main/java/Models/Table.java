package Models;

import Parser.Visitor.SchemaStatVisitor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by rx on 2017/8/19.
 * 表bean
 */
public class Table {
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
    private List<Column> list = new ArrayList<Column>();

    public Table(String tableName) {
        this.tableName = tableName;
    }

    public void setList(List<Column> list) {
        this.list = list;
    }

    public List<Column> getList() {
        return list;
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


}
