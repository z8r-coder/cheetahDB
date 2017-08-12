package Parser.Visitor;

import Parser.Token;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ruanxin on 2017/8/12.
 */
public class SchemaStatVisitor extends BaseASTVisitorAdapter {
    //目前只支持单表查询，向后兼容，此处用容器 20170812
    private List<String> table_name = new ArrayList<String>();

    //update中的赋值,拿到token资源可判断类型
    private Map<String, Token> assMap = new HashMap<String, Token>();
    //表中的row_name,select的行，insert的行
    private List<String> row_name = new ArrayList<String>();

    //insert的值
    private List<String> value_name = new ArrayList<String>();
    //

    public static class Column {
        private String          table;
        private String          name;
        private boolean         where;
        private boolean         select;

        private boolean         primaryKey;
        private boolean         notNull;
        private boolean         unique;

        private String          dataType;

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

        public void setDataType(String dataType) {
            this.dataType = dataType;
        }

        public String getDataType() {
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

        public boolean getNotNull(boolean notNull) {
            return notNull;
        }

        public void setIsUnique(boolean unique) {
            this.unique = unique;
        }

        public boolean getUnique() {
            return unique;
        }

//        public int hashCode() {
//            int tableHashCode = table != null ?
//        }
    }

    public static class Relationship {
        private Column left;
        private String right;
        private String operator;

        public Relationship() {

        }

        public Relationship(Column left, String right,String operator) {
            this.left = left;
            this.right = right;
            this.operator = operator;
        }

        public Column getLeft() {
            return left;
        }

        public void setLeft(Column left) {
            this.left = left;
        }

        public String getRight() {
            return right;
        }

        public void setRight(String right) {
            this.right = right;
        }

        public String getOperator() {
            return operator;
        }

        public void setOperator(String operator) {
            this.operator = operator;
        }

        @Override
        public boolean equals(Object object) {
            if (this == object) {
                return true;
            }

            if (object == null) {
                return false;
            }

            if (object.getClass() != object.getClass()) {
                return false;
            }

            Relationship other = (Relationship) object;
            if (left == null) {
                if (other.left != null) {
                    return false;
                }
            } else if (!left.equals(other.left)) {
                return false;
            }
            if (operator == null) {
                if (other.operator == null) {
                    return false;
                }
            } else if (!operator.equals(other.operator)) {
                return false;
            }
            if (right == null) {
                if (other.right != null) {
                    return false;
                }
            } else if (!right.equals(other.right)) {
                return false;
            }
            return true;
        }
        @Override
        public int hashCode() {
            final int prime = 31;
            int res = 1;
            res = prime * res + ((left == null) ? 0 : left.hashCode());
            res = prime * res + ((right == null) ? 0 : right.hashCode());
            res = prime * res + ((right == null) ? 0 : operator.hashCode());

            return res;
        }
        @Override
        public String toString() {
            return left + " " + operator + right;
        }
    }
    public static class condition {

    }
}
