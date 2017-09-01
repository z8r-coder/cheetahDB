package Parser.Visitor;

import Models.Column;
import Models.Relationship;
import Models.Value;
import Parser.*;
import Parser.AstGen.*;

import Support.Logging.Log;
import Support.Logging.LogFactory;
import Utils.StringUtils;

import Exception.SytaxErrorsException;

import java.util.*;

/**
 * 解析语法树
 * Created by ruanxin on 2017/8/12.
 */
public class SchemaStatVisitor extends BaseASTVisitorAdapter {

    private final Set<Column> columns = new HashSet<Column>();

    private final Map<String, Column> str2Col = new HashMap<String, Column>();

    private final Set<Relationship> relationships = new LinkedHashSet<Relationship>();

    private final List<Value> values = new ArrayList<Value>();

    private final List<String> and_or = new ArrayList<String>();

    private final static Log logger = LogFactory.getLog(SchemaStatVisitor.class);

    // TODO: 2017/8/15 暂且不支持，先写索引引擎 
    @Override
    public void visit(SQLAlterAst ast) throws Exception {
        logger.error("暂且不支持");
        super.visit(ast);
    }

    @Override
    public void visit(SQLShowDbAst ast) throws Exception {
        //showDb 无需处理
    }

    @Override
    public void visit(SQLDeleteAst ast) throws Exception {
        AST past = ast.getAst();
        ASTNode root = past.getRoot();
        if (past.getAstType() != SQLASTType.DELETE_WITH_WHERE &&
                past.getAstType() != SQLASTType.DELETE_ALL) {
            logger.error("The ast is not delete");
            return;
        }
        ASTNode delete_node = root.getChildSet().get(0);
        String tableName = delete_node.getChildSet().get(2).getValue();

        ast.setTableName(tableName);

        if (past.getAstType() == SQLASTType.DELETE_WITH_WHERE) {
            visitWhere(delete_node, tableName,4);
        }

        ast.setRs(and_or);
        ast.setRls(relationships);
    }

    @Override
    public void visit(SQLUpdateAst ast) throws Exception {
        AST past = ast.getAst();
        ASTNode root = past.getRoot();//sql_node
        if (past.getAstType() != SQLASTType.UPDATE_WITH_WHERE &&
                past.getAstType() != SQLASTType.UPDATE_WITHOUT_WHERE) {
            logger.error("The ast is not update");
            return;
        }

        ASTNode u_node = root.getChildSet().get(0);//UPDATE_NODE
        String tableName = u_node.getChildSet().get(1).getValue();
        ASTNode set_list = u_node.getChildSet().get(3);
        Map<String, Value> paraMap = new HashMap<String, Value>();
        visitAss(set_list, tableName, paraMap);

        ast.setTableName(tableName);
        ast.setAssMap(paraMap);

        if (past.getAstType() == SQLASTType.UPDATE_WITH_WHERE) {
            visitWhere(u_node,tableName,5);
            ast.setRls(relationships);
            ast.setRs(and_or);
        }
    }

    private void visitAss(ASTNode astNode, String tableName,Map<String, Value> paraMap) {
        List<ASTNode> assList = astNode.getChildSet();
        for (int i = 0; i < assList.size();) {
            ASTNode id_node = assList.get(i++);
            //只需要获取名字
            String columnName = id_node.getValue();

            ASTNode value_node = assList.get(++i);
            Value value;
            if (value_node.getSortCode() == SortCode.NUMBER) {
                value = new Value(value_node.getValue(), SQLDataType.INTEGER);
            } else if (value_node.getSortCode() == SortCode.STRING) {
                value = new Value(value_node.getValue(), SQLDataType.VARCHAR);
            } else {
                logger.error("visitAss don't support the data type " + value_node.getSortCode().toString());
                return;
            }

            paraMap.put(columnName, value);
            i++;
            if (i < assList.size() &&
                    assList.get(i).getSortCode() == SortCode.COMMA) {
                i++;
            }
        }
    }
    @Override
    public void visit(SQLCreateDbAst ast) throws Exception {
        AST past = ast.getAst();
        ASTNode root = past.getRoot();//sql_node
        ASTNode cd_node = root.getChildSet().get(0);
        if (past.getAstType() != SQLASTType.CREATE_DATABASE) {
            logger.error("The ast is not create database");
            return;
        }

        String DatabaseName = cd_node.getChildSet().get(2).getValue();
        ast.setDatabaseName(DatabaseName);
    }

    @Override
    public void visit(SQLInsertAst ast) throws Exception {
        AST past = ast.getAst();
        ASTNode root = past.getRoot();//sql_node
        ASTNode i_node = root.getChildSet().get(0);//INSERT_NODE
        if (!StringUtils.equals(i_node.getValue(), "INSERT_NODE")) {
            logger.error("The ast is not insert");
            return;
        }
        String table_name = i_node.getChildSet().get(2).getValue();

        switch (past.getAstType()) {
            case INSERT_SINGLE_DEFAULT:
                //单行缺省插入
                ASTNode values_list_node = i_node.getChildSet().get(5);

                logger.info(values_list_node.getValue());

                List<Value> values_list = visitValue(values_list_node,"INSERT");
                List<List<Value>> mutil_value = new ArrayList<List<Value>>();

                mutil_value.add(values_list);

                ast.setValues(mutil_value);
                ast.setTableName(table_name);
                break;
            case INSERT_SINGLE:
                //单行非缺省插入
                ASTNode column_list_node = i_node.getChildSet().get(4);
                logger.info("INSERT_SINGLE,insert column_list_node:" + column_list_node.getValue());

                List<String> columnName = visitInsertColumn(column_list_node, "INSERT", table_name);

                ASTNode value_list_node = i_node.getChildSet().get(8);

                List<List<Value>> mutil_list = new ArrayList<List<Value>>();
                List<Value> value_list = visitValue(value_list_node,"INSERT");
                mutil_list.add(value_list);

                ast.setTableName(table_name);
                ast.setValues(mutil_list);
                ast.setColumnNames(columnName);
                break;
            case INSERT_MULT:
                //多行非缺省插入
                ASTNode column_list_node_mult = i_node.getChildSet().get(4);
                logger.info("INSERT_MULT,insert column_list_node:" + column_list_node_mult.getValue());

                List<String> mult_columnName = visitInsertColumn(column_list_node_mult, "INSERT", table_name);

                List<List<Value>> mults_values = new ArrayList<List<Value>>();
                List<ASTNode> nodes = i_node.getChildSet();

                for (ASTNode node : nodes) {
                    if (StringUtils.equals(node.getValue(), "values_list")) {
                        List<Value> values = visitValue(node, "INSERT");
                        mults_values.add(values);
                    }
                }
                ast.setColumnNames(mult_columnName);
                ast.setValues(mults_values);
                ast.setTableName(table_name);
                break;
            case INSERT_MULT_DEFAULT:
                //多行非缺省插入
                List<List<Value>> mult_def_values = new ArrayList<List<Value>>();
                List<ASTNode> mtDef_nodes = i_node.getChildSet();
                for (ASTNode node : mtDef_nodes) {
                    if (StringUtils.equals(node.getValue(), "values_list")) {
                        List<Value> values = visitValue(node, table_name);
                        mult_def_values.add(values);
                    }
                }

                ast.setTableName(table_name);
                ast.setValues(mult_def_values);
                break;
            default:
                logger.error("wrong with past' ast type = " + past.getAstType());
                return;
        }

    }
    private List<String> visitInsertColumn(ASTNode astNode, String opname, String tableName) {
        List<ASTNode> nodes = astNode.getChildSet();

        List<String> columnName = new ArrayList<String>();
        for (ASTNode node : nodes) {
            if (node.getSortCode() == SortCode.IDENTIFIED) {
                //column name
                if (StringUtils.equals(opname, "INSERT")) {
                    columnName.add(node.getValue());
                }
            }
        }
        return columnName;
    }
    private List<Value> visitValue(ASTNode astNode, String opname) {
        List<ASTNode> nodes = astNode.getChildSet();

        List<Value> values = new ArrayList<Value>();
        for (ASTNode node : nodes) {
            if (node.getSortCode() == SortCode.NUMBER) {
                Value value = new Value(Integer.parseInt(node.getValue()), SQLDataType.INTEGER);
                if (StringUtils.equals(opname, "INSERT")) {
                    value.setIsInsert(true);
                } else if (StringUtils.equals(opname, "IN")){
                    value.setIsIn(true);
                }
                values.add(value);
            } else if (node.getSortCode() == SortCode.STRING) {
                Value value = new Value(node.getValue(), SQLDataType.VARCHAR);
                values.add(value);
            }
        }
        return values;
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

        //列不允许重复
        Set<String> columns = new HashSet<String>();

        //select * pat,单表插入
        if (slt_list.size() == 1 && slt_list.get(0).getValue().equals("*")) {
            columns.add("*");
        } else {
            for (ASTNode node : slt_list) {
                if (node.getSortCode() == SortCode.IDENTIFIED) {
                    columns.add(node.getValue());
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
        ASTNode colNode = crt_tab.getChildSet().get(4);//column

        String table_name = crt_tab.getChildSet().get(2).getValue();//table name

        ast.setTableName(table_name);

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
                if (StringUtils.equals(node.getValue(), "ParamsList")) {
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
}
