package models;

import parser.SQLDataType;
import parser.Token;
import utils.StringUtils;

import java.io.Serializable;

/**
 * Created by rx on 2017/8/26.
 */
public class Column implements Serializable {
    private String          table;
    private String          name;
    private Token token;//需要知道类型时候，注入token
    private boolean         where;
    private boolean         select;
    private boolean         insert;

    private boolean         primaryKey;
    private boolean         notNull;
    private boolean         unique;

    private SQLDataType dataType;
    private int             typeLength;

    public Column() {

    }
    public Column(String table, String name) {
        this.table = table;
        this.name = name;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDataType(SQLDataType dataType) {
        this.dataType = dataType;
    }

    public SQLDataType getDataType() {
        return dataType;
    }

    public void setIsWhere(boolean where) {
        this.where = where;
    }

    public boolean getWhere() {
        return where;
    }

    public void setIsSelect(boolean select) {
        this.select = select;
    }

    public boolean getSelect() {
        return select;
    }

    public void setIsPrimaryKey(boolean primaryKey) {
        this.primaryKey = primaryKey;
    }

    public boolean getPrimaryKey() {
        return primaryKey;
    }

    public void setIsNotNull(boolean notNull) {
        this.notNull = notNull;
    }

    public boolean getNotNull() {
        return notNull;
    }

    public void setIsUnique(boolean unique) {
        this.unique = unique;
    }

    public boolean getUnique() {
        return unique;
    }

    public void setTypeLength(int typeLength) {
        this.typeLength = typeLength;
    }

    public int getTypeLength() {
        return typeLength;
    }

    public void setInsert(boolean insert) {
        this.insert = insert;
    }
    public boolean getInsert() {
        return insert;
    }

    public void setToken(Token token) {
        this.token = token;
    }

    public Token getToken() {
        return token;
    }

    @Override
    public int hashCode() {
        int tableHashCode = table != null ? StringUtils.lowerHashCode(table) : 0;
        int nameHashCode = name != null ? StringUtils.lowerHashCode(name) : 0;

        return tableHashCode + nameHashCode;
    }

    @Override
    public String toString() {
        if (table != null) {
            return table + "." + name;
        }
        return name;
    }

//    @Override
//    public boolean equals(Object object) {
//        Column column = (Column) object;
//        if (!StringUtils.equals(column.getTable(), table)) {
//            return false;
//        }
//        if (!StringUtils.equals(column.getName(), name)) {
//            return false;
//        }
//        if (!TokenUtils.equals(token, column.getToken())) {
//            return false;
//        }
//        if (where != column.getWhere()) {
//            return false;
//        }
//        if (select != column.getSelect()) {
//            return false;
//        }
//
//        if (insert != column.getInsert()) {
//            return false;
//        }
//
//        if (primaryKey != column.getPrimaryKey()) {
//            return false;
//        }
//
//        if (notNull != column.getNotNull()) {
//            return false;
//        }
//
//        if (unique != column.getUnique()) {
//            return false;
//        }
//
//        if (dataType == null) {
//            return column.dataType == null;
//        }
//
//        if (dataType != column.getDataType()) {
//            return false;
//        }
//
//        if (typeLength != column.getTypeLength()) {
//            return false;
//        }
//        return true;
//    }
}
