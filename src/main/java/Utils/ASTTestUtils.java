package Utils;

import Parser.AST;
import Parser.ASTNode;
import Parser.SQLParserUtils;
import Support.Logging.Log;
import Support.Logging.LogFactory;

import java.util.List;

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
//        AST ast = SQLParserUtils.AssSQlASTgen("CREATE TABLE Persons" +
//                "         (" +
//                "         Id_P integer(10)," +
//                "         LastName varchar(255)," +
//                "         FirstName varchar(255)," +
//                "         Address varchar(255)," +
//                "         City varchar(255)" +
//                "         );");
//        ASTTestPrint(ast.getRoot());
//        System.out.println();
//        System.out.println(ast.getAstType());

        //create table语句测试,含not null约束语句,通过测试
//        AST ast = SQLParserUtils.AssSQlASTgen("CREATE TABLE Persons" +
//                "         (" +
//                "         Id_P integer(10) NOT NULL," +
//                "         LastName varchar(255) not null," +
//                "         FirstName varchar(255)," +
//                "         Address varchar(255)," +
//                "         City varchar(255)" +
//                "         );");
//        ASTTestPrint(ast.getRoot());
//        System.out.println();
//        System.out.println(ast.getAstType());

        //create table语句测试,含not null约束语句,含unique,check,primary,foreign约束通过测试
//        AST ast = SQLParserUtils.AssSQlASTgen("CREATE TABLE Persons" +
//                "         (" +
//                "         Id_P integer(10) NOT NULL," +
//                "         LastName varchar(255) not null," +
//                "         FirstName varchar(255)," +
//                "         Address varchar(255)," +
//                "         UNIQUE (Id_P)," +
//                "         PRIMARY KEY (Id_P)," +
//                "         FOREIGN KEY (Id_P) REFERENCES Persons(Id_P)," +
//                "         City varchar(255)," +
//                "         CHECK (Id_P>0 AND ID_P < 1)" +
//                "         );");
//        ASTTestPrint(ast.getRoot());
//        System.out.println();
//        System.out.println(ast.getAstType());

        /**
         * select暂时只支持简单select和带where的简单select
         */
        //简单select语句测试，不带where,通过测试
//        AST ast = SQLParserUtils.AssSQlASTgen("select * from table_a;");
//        ASTTestPrint(ast.getRoot());
//        System.out.println();
//        System.out.println(ast.getAstType());

        //简单select语句测试，带where,不带in,通过测试
//        AST ast = SQLParserUtils.AssSQlASTgen("select a,b from table_a where A = '123' AND B = '321';");
//        ASTTestPrint(ast.getRoot());
//        System.out.println();
//        System.out.println(ast.getAstType());

        //create database语句测试通过
//        AST ast = SQLParserUtils.AssSQlASTgen("CREATE DATABASE my_db;");
//        ASTTestPrint(ast.getRoot());
//        System.out.println();
//        System.out.println(ast.getAstType());

        //use database 语句测试通过
//        AST ast = SQLParserUtils.AssSQlASTgen("use db_name;");
//        ASTTestPrint(ast.getRoot());
//        System.out.println();
//        System.out.println(ast.getAstType());

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
//        AST ast = SQLParserUtils.AssSQlASTgen("INSERT INTO Persons VALUES ('Gates', 'Bill', 'Xuanwumen 10', 'Beijing');");
//        ASTTestPrint(ast.getRoot());
//        System.out.println();
//        System.out.println(ast.getAstType());

        //单行非缺省值通过测试
//        AST ast = SQLParserUtils.AssSQlASTgen("INSERT INTO Persons (LastName, FirstName) VALUES ('Wilson',123);");
//        ASTTestPrint(ast.getRoot());
//        System.out.println();
//        System.out.println(ast.getAstType());

        //多行插入非缺省值测试通过
//        AST ast = SQLParserUtils.AssSQlASTgen("insert into persons" +
//                "(id_p, lastname , firstName, city )" +
//                "values" +
//                "(200,'haha' , 'deng' , 'shenzhen')," +
//                "(201,'haha2' , 'deng' , 'GD')," +
//                "(202,'haha3' , 'deng' , 'Beijing');");
//        ASTTestPrint(ast.getRoot());
//        System.out.println();
//        System.out.println(ast.getAstType());

        //多行插入缺省值测试通过
//        AST ast = SQLParserUtils.AssSQlASTgen("insert into persons " +
//                "values" +
//                "(200,'haha' , 'deng' , 'shenzhen')," +
//                "(201,'haha2' , 'deng' , 'GD')," +
//                "(202,'haha3' , 'deng' , 'Beijing');");
//        ASTTestPrint(ast.getRoot());
//        System.out.println();
//        System.out.println(ast.getAstType());

        /**
         * update语句测试
         */
//        不要where语句通过
//        AST ast = SQLParserUtils.AssSQlASTgen("UPDATE Person SET Address = 23, City = 'Nanjing';");
//        ASTTestPrint(ast.getRoot());
//        System.out.println();
//        System.out.println(ast.getAstType());

        //带where的语句通过
//        AST ast = SQLParserUtils.AssSQlASTgen("UPDATE Person SET Address = 'Zhongshan 23', City = 'Nanjing' WHERE LastName = 'Wilson' AND FirstName = 'Roy';");
//        ASTTestPrint(ast.getRoot());
//        System.out.println();
//        System.out.println(ast.getAstType());

        /**
         * delete语句测试
         */
        //不带where delete测试成功
        AST ast = SQLParserUtils.AssSQlASTgen("DELETE FROM Person;");
        assert ast == null;
        ASTTestPrint(ast.getRoot());
        System.out.println();
        System.out.println(ast.getAstType());

        //带where delete测试成功
//        AST ast = SQLParserUtils.AssSQlASTgen("DELETE FROM Person WHERE LastName = 'Wilson' AND LastName = 'Wilson' OR LastName = 'Wilson';");
//        ASTTestPrint(ast.getRoot());
//        System.out.println();
//        System.out.println(ast.getAstType());
    }
}
