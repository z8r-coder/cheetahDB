package Utils;

import Models.Column;
import Parser.AST;
import Parser.ASTNode;
import Parser.Builder.*;
import Parser.Builder.impl.*;
import Parser.SQLParserUtils;
import Parser.Visitor.SchemaStatVisitor;
import Support.Logging.Log;
import Support.Logging.LogFactory;
import com.sun.org.apache.xpath.internal.operations.And;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by ruanxin on 2017/8/10.
 */
public class ASTTestUtils {
    private final static Log LOG = LogFactory.getLog(ASTTestUtils.class);
    public static void ASTTestPrint(ASTNode root) {
        if (root == null) {
            return;
        }
        if (root.get_isLeaf()) {
            System.out.print(root.getValue() + " ");
        } else {
            System.out.print(root.getValue() + " ");
            List<ASTNode> list = root.getChildSet();
            for (ASTNode astNode : list) {
                ASTTestPrint(astNode);
            }
        }
    }

    public static void main(String args[]) {
        /**
         * create语句测试
         */
        //create table语句测试,无约束语句，通过测试
        String sql = "CREATE TABLE Persons" +
                "         (" +
                "         Id_P integer(10)," +
                "         LastName varchar(255)," +
                "         FirstName varchar(255)," +
                "         Address varchar(255)," +
                "         City varchar(255)" +
                "         );";
        AST ast = SQLParserUtils.AssSQlASTgen(sql);
        ASTTestPrint(ast.getRoot());
        System.out.println();
        System.out.println(ast.getAstType());
        System.out.println("----------------------------------------------");
        SQLBuilderWraper sbw = new SQLBuilderWraper(sql);
        try {
            SQLCreateTabBuilderImpl scb = (SQLCreateTabBuilderImpl) sbw.getSQLBuilder();
            String tableName = scb.tableName();
            List<Column> columns = scb.Columns();
            String gt = scb.grammerType().toString();

            System.out.println("tableName: " + tableName);
            System.out.println("grammer type: " + gt);
            for (Column column : columns) {
                System.out.println(" column table name=" + column.getTable());
                System.out.print(" column value=" + column.getName());
                System.out.print(" column type=" + column.getDataType());
                System.out.print(" column type length=" + column.getTypeLength());
                System.out.print(" column not null=" + column.getNotNull());
                System.out.print(" column is primaryKey=" + column.getPrimaryKey());
                System.out.print(" column is unique=" + column.getUnique());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        //create table语句测试,含not null约束语句,通过测试
//        String sql = "CREATE TABLE Persons" +
//                "         (" +
//                "         Id_P integer(10) NOT NULL," +
//                "         LastName varchar(255) not null," +
//                "         FirstName varchar(255)," +
//                "         Address varchar(255)," +
//                "         City varchar(255)" +
//                "         );";
//        AST ast = SQLParserUtils.AssSQlASTgen(sql);
//        ASTTestPrint(ast.getRoot());
//        System.out.println();
//        System.out.println(ast.getAstType());
//        System.out.println("----------------------------------------------");
//        SQLBuilderWraper sbw = new SQLBuilderWraper(sql);
//        try {
//            SQLCreateTabBuilderImpl scb = (SQLCreateTabBuilderImpl) sbw.getSQLBuilder();
//            String tableName = scb.tableName();
//            List<SchemaStatVisitor.Column> columns = scb.Columns();
//            String gt = scb.grammerType().toString();
//
//            System.out.println("tableName: " + tableName);
//            System.out.println("grammer type: " + gt);
//            for (SchemaStatVisitor.Column column : columns) {
//                System.out.println(" column table name=" + column.getTable());
//                System.out.print(" column value=" + column.getName());
//                System.out.print(" column type=" + column.getDataType());
//                System.out.print(" column type length=" + column.getTypeLength());
//                System.out.print(" column not null=" + column.getNotNull());
//                System.out.print(" column is primaryKey=" + column.getPrimaryKey());
//                System.out.print(" column is unique=" + column.getUnique());
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        //create table语句测试,含not null约束语句,含unique,check,primary,foreign约束通过测试
//            String sql = "CREATE TABLE Persons" +
//                    "         (" +
//                    "         Id_P integer(10) NOT NULL," +
//                    "         LastName varchar(255) not null," +
//                    "         FirstName varchar(255)," +
//                    "         Address varchar(255)," +
//                    "         UNIQUE (Id_P)," +
//                    "         PRIMARY KEY (Id_P)," +
////                    "         FOREIGN KEY (Id_P) REFERENCES Persons(Id_P)," +
//                    "         City varchar(255)" +
////                    "         CHECK (Id_P>0 AND ID_P < 1)" +
//                    "         );";
//        AST ast = SQLParserUtils.AssSQlASTgen(sql);
//        ASTTestPrint(ast.getRoot());
//        System.out.println();
//        System.out.println(ast.getAstType());
//        System.out.println("----------------------------------------------");
//        SQLBuilderWraper sbw = new SQLBuilderWraper(sql);
//        try {
//            SQLCreateTabBuilderImpl scb = (SQLCreateTabBuilderImpl) sbw.getSQLBuilder();
//            String tableName = scb.tableName();
//            List<SchemaStatVisitor.Column> columns = scb.Columns();
//            String gt = scb.grammerType().toString();
//
//            System.out.println("tableName: " + tableName);
//            System.out.println("grammer type: " + gt);
//            for (SchemaStatVisitor.Column column : columns) {
//                System.out.println(" column table name=" + column.getTable());
//                System.out.print(" column value=" + column.getName());
//                System.out.print(" column type=" + column.getDataType());
//                System.out.print(" column type length=" + column.getTypeLength());
//                System.out.print(" column not null=" + column.getNotNull());
//                System.out.print(" column is primaryKey=" + column.getPrimaryKey());
//                System.out.print(" column is unique=" + column.getUnique());
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        /**
         * select暂时只支持简单select和带where的简单select
         */
        //简单select语句测试，不带where,通过测试
//        String sql = "select * from table_a;";
//        AST ast = SQLParserUtils.AssSQlASTgen(sql);
//        ASTTestPrint(ast.getRoot());
//        System.out.println();
//        System.out.println(ast.getAstType());
//        System.out.println("----------------------------------------------");
//        SQLBuilderWraper sbw = new SQLBuilderWraper(sql);
//        try {
//            SQLSelectBuilderImpl scb = (SQLSelectBuilderImpl) sbw.getSQLBuilder();
//            List<SchemaStatVisitor.Column> columns = scb.columns();
//            String tableName = scb.from();
//            System.out.println("tableName=" + tableName);
//            for (SchemaStatVisitor.Column column : columns) {
//                System.out.print("ColumnName=" + column.getName());
//            }
//            System.out.println();
//            System.out.println(scb.grammerType());
//
//        }catch (Exception e) {
//            e.printStackTrace();
//        }

        //简单select语句测试，带where,不带in,通过测试
//        String sql = "select a,b from table_a where A = '123' AND B = '321';";
//        AST ast = SQLParserUtils.AssSQlASTgen(sql);
//        ASTTestPrint(ast.getRoot());
//        System.out.println();
//        System.out.println(ast.getAstType());
//        System.out.println("----------------------------------------------");
//        SQLBuilderWraper sbw = new SQLBuilderWraper(sql);
//        try {
//            SQLSelectBuilderImpl scb = (SQLSelectBuilderImpl) sbw.getSQLBuilder();
//            List<SchemaStatVisitor.Column> columns = scb.columns();
//            String tableName = scb.from();
//            Set<SchemaStatVisitor.Relationship> rls = scb.where();
//            List<String> AndOr = scb.AndOr();
//            System.out.println("tableName=" + tableName);
//            for (SchemaStatVisitor.Column column : columns) {
//                System.out.print("  ColumnName=" + column.getName());
//            }
//            System.out.println();
//            for (SchemaStatVisitor.Relationship rs : rls) {
//                System.out.println(rs.getLeft().getName() + rs.getOperator() + rs.getRight().getValue());
//            }
//
//            for (String str: AndOr) {
//                System.out.print(str + "  ");
//            }
//            System.out.println(scb.grammerType());
//
//        }catch (Exception e) {
//            e.printStackTrace();
//        }

//        create database语句测试通过
//        String sql = "CREATE DATABASE my_db;";
//        AST ast = SQLParserUtils.AssSQlASTgen(sql);
//        ASTTestPrint(ast.getRoot());
//        System.out.println();
//        System.out.println(ast.getAstType());
//        System.out.println("----------------------------------------------");
//        SQLBuilderWraper sbw = new SQLBuilderWraper(sql);
//        try {
//            SQLCreateDbBuilderImpl sdb = (SQLCreateDbBuilderImpl) sbw.getSQLBuilder();
//            String dataBaseName = sdb.dataBaseName();
//            System.out.println("dataBaseName= " + dataBaseName);
//            System.out.println("ast type:" + sdb.grammerType());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        //use database 语句测试通过
//        String sql = "use db_name;";
//        AST ast = SQLParserUtils.AssSQlASTgen("use db_name;");
//        ASTTestPrint(ast.getRoot());
//        System.out.println();
//        System.out.println(ast.getAstType());
//        System.out.println("----------------------------------------------");
//        SQLBuilderWraper sbw = new SQLBuilderWraper(sql);
//        try {
//            SQLUseDbBuilderImpl sud = (SQLUseDbBuilderImpl) sbw.getSQLBuilder();
//            String useDbName = sud.dbName();
//            System.out.println("use db name:" + useDbName);
//            System.out.println(sud.grammerType());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        //show databases 语句测试通过
//        AST ast = SQLParserUtils.AssSQlASTgen("SHOW DATABASES;");
//        ASTTestPrint(ast.getRoot());
//        System.out.println();
//        System.out.println(ast.getAstType());


        //show tables;通过
//        AST ast = SQLParserUtils.AssSQlASTgen("SHOW TABLES;");
//        ASTTestPrint(ast.getRoot());
//        System.out.println();
//        System.out.println(ast.getAstType());

        /**
         * drop语句
         */
        // drop database语句通过
//        AST ast = SQLParserUtils.AssSQlASTgen("Drop database database_name;");
//        ASTTestPrint(ast.getRoot());
//        System.out.println();
//        System.out.println(ast.getAstType());

        // drop table语句通过
//        AST ast = SQLParserUtils.AssSQlASTgen("DROP TABLE Customer;");
//        ASTTestPrint(ast.getRoot());
//        System.out.println();
//        System.out.println(ast.getAstType());

        /**
         * Insert语句测试
         */
//        单行缺省值插入通过测试
//        String sql = "INSERT INTO Persons VALUES ('Gates', 'Bill', 'Xuanwumen 10', 'Beijing');";
//        AST ast = SQLParserUtils.AssSQlASTgen(sql);
//        ASTTestPrint(ast.getRoot());
//        System.out.println();
//        System.out.println(ast.getAstType());
//        System.out.println("----------------------------------------------");
//        SQLBuilderWraper sbw = new SQLBuilderWraper(sql);
//        try {
//            SQLInsertBuilderImpl sib = (SQLInsertBuilderImpl) sbw.getSQLBuilder();
//            List<List<SchemaStatVisitor.Value>> values = sib.values();
//            String tableName = sib.from();
//            for (List<SchemaStatVisitor.Value> list_value : values) {
//                for (SchemaStatVisitor.Value value : list_value) {
//                    System.out.print("value = " + value.getVal() + "  ");
//                }
//                System.out.println();
//            }
//            System.out.println("tableName=" + tableName);
//            System.out.println("sql type:" + sib.grammerType());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        //单行非缺省值通过测试
//        String sql = "INSERT INTO Persons (LastName, FirstName) VALUES ('Wilson',123);";
//        AST ast = SQLParserUtils.AssSQlASTgen(sql);
//        ASTTestPrint(ast.getRoot());
//        System.out.println();
//        System.out.println(ast.getAstType());
//        System.out.println("----------------------------------------------");
//        SQLBuilderWraper sbw = new SQLBuilderWraper(sql);
//        try {
//            SQLInsertBuilderImpl sib = (SQLInsertBuilderImpl) sbw.getSQLBuilder();
//            List<List<SchemaStatVisitor.Value>> values = sib.values();
//            String tableName = sib.from();
//            List<SchemaStatVisitor.Column> columns = sib.columns();
//            for (SchemaStatVisitor.Column column : columns) {
//                System.out.print("column name = " + column.getName() + "  ");
//            }
//            System.out.println();
//            for (List<SchemaStatVisitor.Value> list_value : values) {
//                for (SchemaStatVisitor.Value value : list_value) {
//                    System.out.print("value = " + value.getVal() + "  ");
//                }
//                System.out.println();
//            }
//            System.out.println("tableName=" + tableName);
//            System.out.println("sql type:" + sib.grammerType());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        //多行插入非缺省值测试通过
//        String sql = "insert into persons" +
//                "(id_p, lastname , firstName, city )" +
//                "values" +
//                "(200,'haha' , 'deng' , 'shenzhen')," +
//                "(201,'haha2' , 'deng' , 'GD')," +
//                "(202,'haha3' , 'deng' , 'Beijing');";
//        AST ast = SQLParserUtils.AssSQlASTgen(sql);
//        ASTTestPrint(ast.getRoot());
//        System.out.println();
//        System.out.println(ast.getAstType());
//        System.out.println("----------------------------------------------");
//        SQLBuilderWraper sbw = new SQLBuilderWraper(sql);
//        try {
//            SQLInsertBuilderImpl sib = (SQLInsertBuilderImpl) sbw.getSQLBuilder();
//            List<List<SchemaStatVisitor.Value>> values = sib.values();
//            String tableName = sib.from();
//            List<SchemaStatVisitor.Column> columns = sib.columns();
//            for (SchemaStatVisitor.Column column : columns) {
//                System.out.print("column name = " + column.getName() + "  ");
//            }
//            System.out.println();
//            for (List<SchemaStatVisitor.Value> list_value : values) {
//                for (SchemaStatVisitor.Value value : list_value) {
//                    System.out.print("value = " + value.getVal() + "  ");
//                }
//                System.out.println();
//            }
//            System.out.println("tableName=" + tableName);
//            System.out.println("sql type:" + sib.grammerType());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        //多行插入缺省值测试通过
//        String sql = "insert into persons " +
//                "values" +
//                "(200,'haha' , 'deng' , 'shenzhen')," +
//                "(201,'haha2' , 'deng' , 'GD')," +
//                "(202,'haha3' , 'deng' , 'Beijing');";
//        AST ast = SQLParserUtils.AssSQlASTgen(sql);
//        ASTTestPrint(ast.getRoot());
//        System.out.println();
//        System.out.println(ast.getAstType());
//        System.out.println("----------------------------------------------");
//        SQLBuilderWraper sbw = new SQLBuilderWraper(sql);
//        try {
//            SQLInsertBuilderImpl sib = (SQLInsertBuilderImpl) sbw.getSQLBuilder();
//            List<List<SchemaStatVisitor.Value>> values = sib.values();
//            String tableName = sib.from();
//            for (List<SchemaStatVisitor.Value> list_value : values) {
//                for (SchemaStatVisitor.Value value : list_value) {
//                    System.out.print("value = " + value.getVal() + "  ");
//                }
//                System.out.println();
//            }
//            System.out.println("tableName=" + tableName);
//            System.out.println("sql type:" + sib.grammerType());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        /**
         * update语句测试
         */
//        不要where语句通过
//        String sql = "UPDATE Person SET Address = 23, City = 'Nanjing';";
//        AST ast = SQLParserUtils.AssSQlASTgen(sql);
//        ASTTestPrint(ast.getRoot());
//        System.out.println();
//        System.out.println(ast.getAstType());
//        System.out.println("----------------------------------------------");
//        SQLBuilderWraper sbw = new SQLBuilderWraper(sql);
//        try {
//            SQLUpdateBuilderImpl sub = (SQLUpdateBuilderImpl) sbw.getSQLBuilder();
//            Map<String, SchemaStatVisitor.Value> map = sub.AssValue();
//            String tableName = sub.from();
//
//            System.out.println("TableName = " + tableName);
//            for (String str : map.keySet()) {
//                System.out.println(str + "=" + map.get(str).getVal());
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        //带where的语句通过
//        String sql = "UPDATE Person SET Address = 'Zhongshan 23', City = 'Nanjing' WHERE LastName = 'Wilson' AND FirstName = 'Roy';";
//        AST ast = SQLParserUtils.AssSQlASTgen(sql);
//        ASTTestPrint(ast.getRoot());
//        System.out.println();
//        System.out.println(ast.getAstType());
//        System.out.println("----------------------------------------------");
//        SQLBuilderWraper sbw = new SQLBuilderWraper(sql);
//        try {
//            SQLUpdateBuilderImpl sub = (SQLUpdateBuilderImpl) sbw.getSQLBuilder();
//            Map<String, SchemaStatVisitor.Value> map = sub.AssValue();
//            String tableName = sub.from();
//            Set<SchemaStatVisitor.Relationship> rls = sub.where();
//            List<String> andOr = sub.AndOr();
//
//            System.out.println("TableName = " + tableName);
//            for (String str : map.keySet()) {
//                System.out.println(str + "=" + map.get(str).getVal());
//            }
//            System.out.println("==========================");
//            for (SchemaStatVisitor.Relationship rs : rls) {
//                System.out.println(rs.getLeft().getName() + rs.getOperator() + rs.getRight().getValue());
//            }
//
//            for (String str: andOr) {
//                System.out.print(str + "  ");
//            }
//            System.out.println(sub.grammerType());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        /**
         * delete语句测试
         */
        //不带where delete测试成功
//        String sql = "DELETE FROM Person;";
//        AST ast = SQLParserUtils.AssSQlASTgen(sql);
//        ASTTestPrint(ast.getRoot());
//        System.out.println();
//        System.out.println(ast.getAstType());
//        System.out.println("----------------------------------------------");
//        SQLBuilderWraper sbw = new SQLBuilderWraper(sql);
//        try {
//            SQLDeleteBuilderImpl sdi = (SQLDeleteBuilderImpl) sbw.getSQLBuilder();
//            String tableName = sdi.from();
//            System.out.print("tableName= " + tableName);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        //带where delete测试成功
//        String sql = "DELETE FROM Person WHERE LastName = 'Wilson' AND LastName = 'Wilson' OR LastName = 'Wilson';";
//        AST ast = SQLParserUtils.AssSQlASTgen(sql);
//        ASTTestPrint(ast.getRoot());
//        System.out.println();
//        System.out.println(ast.getAstType());
//        System.out.println("----------------------------------------------");
//        SQLBuilderWraper sbw = new SQLBuilderWraper(sql);
//        try {
//            SQLDeleteBuilderImpl sdi = (SQLDeleteBuilderImpl) sbw.getSQLBuilder();
//            String tableName = sdi.from();
//            Set<SchemaStatVisitor.Relationship> rls = sdi.where();
//            List<String> andOr = sdi.AndOr();
//
//            for (SchemaStatVisitor.Relationship rs : rls) {
//                System.out.println(rs.getLeft().getName() + rs.getOperator() + rs.getRight().getValue());
//            }
//
//            for (String str: andOr) {
//                System.out.print(str + "  ");
//            }
//            System.out.println();
//            System.out.println("tableName= " + tableName);
//            System.out.println("grammer type=" + sdi.grammerType());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }
}
