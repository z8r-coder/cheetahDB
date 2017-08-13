package Parser.Visitor;

import Parser.*;
import Parser.AstGen.*;

import Support.Logging.Log;
import Support.Logging.LogFactory;
import Utils.StringUtils;

import Exception.SytaxErrorsException;

import java.util.*;

/**
 * Created by ruanxin on 2017/8/12.
 */
public class SchemaStatVisitor extends BaseASTVisitorAdapter {

    private final Set<Column> columns = new HashSet<Column>();

    private final Map<String, Column> str2Col = new HashMap<String, Column>();

    private final Set<Relationship> relationships = new LinkedHashSet<Relationship>();

    private final List<Value> values = new ArrayList<Value>();

    private final List<String> and_or = new ArrayList<String>();

    private final static Log logger = LogFactory.getLog(SchemaStatVisitor.class);

    @Override
    public void visit(SQLInsertAst ast) throws Exception {
        AST past = ast.getAst();
        ASTNode root = past.getRoot();
        ASTNode i_node = root.getChildSet().get(0);
        if (StringUtils.equals(i_node.getValue(), "INSERT_NODE")) {
            logger.error("The ast is not insert");
            return;
        }

        switch (past.getAstType()) {
            case INSERT_SINGLE_DEFAULT:
                break;
            case INSERT_SINGLE:
                break;
            case INSERT_MULT:
                break;
            case INSERT_MULT_DEFAULT:
                break;
            default:
                logger.error("wrong with past' ast type = " + past.getAstType());
                return;
        }

    }

    @Override
    public void visit(SQLUseAst ast) throws Exception {
        AST past = ast.getAst();
        ASTNode root = past.getRoot();
        if (past.getAstType() != SQLASTType.USE_DATABASE) {
            logger.error("The ast is not use_database");
            return;
        }

        ASTNode udn = root.getChildSet().get(0);//USE_DB_NODE

        String dataName = udn.getChildSet().get(1).getValue();

        ast.setDataName(dataName);
    }

    @Override
    public void visit(SQLSelectAst ast) throws Exception {
        AST past = ast.getAst();
        ASTNode root = past.getRoot();
        if (past.getAstType() != SQLASTType.SELECT_WITH_WHERE &&
                past.getAstType() != SQLASTType.SELECT_ONLY) {
            logger.error("The ast is not select");
            return;
        }

        ASTNode slt_node = root.getChildSet().get(0); //SELECT_NODE
        ASTNode slt_list_node = slt_node.getChildSet().get(1); //SELECT_LIST
        List<ASTNode> slt_list = slt_list_node.getChildSet();

        String table_name = slt_node.getChildSet().get(3).getValue();

        List<Column> columns = new ArrayList<Column>();

        //select * pat,单表插入
        if (slt_list.size() == 1 && slt_list.get(0).equals("*")) {
            Column column = new Column(table_name, "*");
            column.setIsSelect(true);
            columns.add(column);
        } else {
            for (ASTNode node : slt_list) {
                if (node.getSortCode() == SortCode.IDENTIFIED) {
                    Column column = new Column(table_name, node.getValue());
                    column.setIsSelect(true);
                    columns.add(column);
                }
            }
        }
        ast.setSlt_col(columns);//查询的column
        ast.setTable_name(table_name);//表名
        if (past.getAstType() == SQLASTType.SELECT_ONLY) {
            return;
        } else if (past.getAstType() == SQLASTType.SELECT_WITH_WHERE) {
            visitWhere(slt_node, table_name, 5);
            ast.setRs(and_or);
            ast.setRls(relationships);
        }
    }

    public void visitWhere(ASTNode astNode, String table_name, int where_index) {
        ASTNode where_condition = astNode.getChildSet().get(where_index);
        List<ASTNode> con_list = where_condition.getChildSet();

        for (int i = 0; i < con_list.size();) {
            Column left = new Column(table_name, con_list.get(i).getValue());
            left.setIsWhere(true);
            String opS = con_list.get(++i).getValue();
            Token right = con_list.get(++i).getToken();
            Relationship rps = new Relationship(left, right, opS);

            relationships.add(rps);
            if (++i < con_list.size()) {
                String oper = con_list.get(i).getValue();
                and_or.add(oper);//存放and or关系
            }
            ++i;
        }
    }

    @Override
    public void visit(SQLCreateTabAST ast) throws Exception {
        AST past = ast.getAst();
        ASTNode root = past.getRoot();
        if (past.getAstType() != SQLASTType.CREATE_TABLE) {
            logger.error("The ast is not create_table!");
            return;
        }
        ASTNode crt_tab = root.getChildSet().get(0);//create_table_node
        ASTNode colNode = crt_tab.getChildSet().get(0);//column

        String table_name = root.getChildSet().get(2).getValue();//table name


        List<ASTNode> nodes = colNode.getChildSet();

        for (ASTNode node : nodes) {
            //先生成column,再做约束
            if (StringUtils.equals(node.getValue(), "column_node")) {
                visitColumn(node, table_name);
            }
        }

        for (ASTNode node : nodes) {
            if (StringUtils.equals(node.getValue(), "unique_node")) {
                visitConstrains(node);
            } else if (StringUtils.equals(node.getValue(),"prim_node")) {
                visitConstrains(node);
            }
        }
        List<Column> columns = new ArrayList<Column>();
        for (String str : str2Col.keySet()) {
            Column column = str2Col.get(str);
            columns.add(column);
        }

        ast.setColumns(columns);
    }

    private void visitConstrains(ASTNode astNode) throws Exception {
        List<ASTNode> childSet = astNode.getChildSet();

        ASTNode para_node = null;
        if (StringUtils.equals(astNode.getValue(), "unique_node") ||
                StringUtils.equals(astNode.getValue(), "prim_node")) {
            for (ASTNode node : childSet) {
                if (StringUtils.equals(astNode.getValue(), "ParamsList")) {
                    para_node = node;
                    break;
                }
            }

            if (para_node == null) {
                throw new SytaxErrorsException(astNode.getValue() + " Parameter missing");
            }

        }
        List<ASTNode> paralist = para_node.getChildSet();
        for (ASTNode node : paralist) {
            if (node.getSortCode() == SortCode.IDENTIFIED) {
                Column column = str2Col.get(node.getValue());
                if (column == null) {
                    throw new SytaxErrorsException(astNode.getValue() + " constrains error,not exist the column");
                } else {
                    if (StringUtils.equals(astNode.getValue(), "unique_node")) {
                        column.setIsUnique(true);
                    } else if (StringUtils.equals(astNode.getValue(), "prim_node")) {
                        column.setIsPrimaryKey(true);
                    }
                }
            }
        }
    }

    private void visitColumn(ASTNode astNode, String table_name) throws Exception {
        List<ASTNode> col_list = astNode.getChildSet();
        String col_name = col_list.get(0).getValue();
        String col_dataType = col_list.get(1).getValue().toUpperCase();
        String dataLength = col_list.get(3).getValue();

        //创建column
        Column column = new Column(table_name, col_name);
        if (col_list.size() > 5) {
            if (col_list.get(5).getSortCode() == SortCode.NOT) {
                column.setIsNotNull(true);
            }
        }
        if (StringUtils.equals("VARCHAR", col_dataType)) {
            column.setDataType(SQLDataType.VARCHAR);
        } else if (StringUtils.equals("INTEGER", col_dataType)) {
            column.setDataType(SQLDataType.INTEGER);
        } else {
            throw new SytaxErrorsException(astNode.getValue() + "now don't support the data type " + col_dataType);
        }
        try {
            int length = Integer.parseInt(dataLength);
            column.setTypeLength(length);
            str2Col.put(col_name, column);

        } catch (NumberFormatException ex) {
            throw ex;
        }
    }

    public static class Value {
        private String          val;
        private boolean         in;
        private boolean         insert;

        public Value() {

        }

        public Value(String val) {
            this.val = val;
        }

        public void setIsIn(boolean in) {
            this.in = in;
        }

        public boolean getIsIn() {
            return in;
        }

        public String getVal() {
            return val;
        }

        public void setVal(String val) {
            this.val = val;
        }

        public void setIsInsert(boolean insert) {
            this.insert = insert;
        }

        public boolean getIsInsert() {
            return insert;
        }


    }
    public static class Column {
        private String          table;
        private String          name;
        private boolean         where;
        private boolean         select;

        private boolean         primaryKey;
        private boolean         notNull;
        private boolean         unique;

        private SQLDataType     dataType;
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

    }

    public static class Relationship {
        private Column left;
        private Token right;
        private String operator;

        public Relationship() {

        }

        public Relationship(Column left, Token right,String operator) {
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

        public Token getRight() {
            return right;
        }

        public void setRight(Token right) {
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
}
