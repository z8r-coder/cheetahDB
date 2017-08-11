package Utils;

import Parser.AST;
import Parser.ASTNode;
import Parser.SQLParserUntil;

import java.util.List;

/**
 * Created by ruanxin on 2017/8/10.
 */
public class ASTTestUtils {
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
        //create table语句测试
//        AST ast = SQLParserUntil.AssSQlASTgen("CREATE TABLE Persons" +
//                "         (" +
//                "         Id_P int," +
//                "         LastName varchar(255)," +
//                "         FirstName varchar(255)," +
//                "         Address varchar(255)," +
//                "         City varchar(255)" +
//                "         );");
//        ASTTestPrint(ast.getRoot_set().get(0));
        //create database语句测试通过
//        AST ast = SQLParserUntil.AssSQlASTgen("CREATE DATABASE my_db;");
//        ASTTestPrint(ast.getRoot_set().get(0));
        //use database

        /**
         * Insert语句测试
         */
//        单行缺省值插入通过测试
//        AST ast = SQLParserUntil.AssSQlASTgen("INSERT INTO Persons VALUES ('Gates', 'Bill', 'Xuanwumen 10', 'Beijing');");
//        ASTTestPrint(ast.getRoot_set().get(0));

        //单行非缺省值通过测试
//        AST ast = SQLParserUntil.AssSQlASTgen("INSERT INTO Persons (LastName) VALUES ('Wilson');");
//        ASTTestPrint(ast.getRoot_set().get(0));

        //多行插入非缺省值测试通过
//        AST ast = SQLParserUntil.AssSQlASTgen("insert into persons" +
//                "(id_p, lastname , firstName, city )" +
//                "values" +
//                "(200,'haha' , 'deng' , 'shenzhen')," +
//                "(201,'haha2' , 'deng' , 'GD')," +
//                "(202,'haha3' , 'deng' , 'Beijing');");
//        ASTTestPrint((ast.getRoot_set().get(0)));

        //多行插入缺省值测试通过
//        AST ast = SQLParserUntil.AssSQlASTgen("insert into persons " +
//                "values" +
//                "(200,'haha' , 'deng' , 'shenzhen')," +
//                "(201,'haha2' , 'deng' , 'GD')," +
//                "(202,'haha3' , 'deng' , 'Beijing');");
//        ASTTestPrint((ast.getRoot_set().get(0)));

        /**
         * update语句测试
         */
        //不要where语句通过
//        AST ast = SQLParserUntil.AssSQlASTgen("UPDATE Person SET Address = 'Zhongshan 23', City = 'Nanjing';");
//        ASTTestPrint((ast.getRoot_set().get(0)));
        //带where的语句通过
//        AST ast = SQLParserUntil.AssSQlASTgen("UPDATE Person SET Address = 'Zhongshan 23', City = 'Nanjing' WHERE LastName = 'Wilson' AND FirstName = 'Roy';");
//        ASTTestPrint((ast.getRoot_set().get(0)));

        /**
         * delete语句测试
         */
        //不带where delete测试成功
//        AST ast = SQLParserUntil.AssSQlASTgen("DELETE FROM Person;");
//        ASTTestPrint((ast.getRoot_set().get(0)));

        //带where delete测试成功
//        AST ast = SQLParserUntil.AssSQlASTgen("DELETE FROM Person WHERE LastName = 'Wilson' AND LastName = 'Wilson' OR LastName = 'Wilson';");
//        ASTTestPrint((ast.getRoot_set().get(0)));
    }
}
