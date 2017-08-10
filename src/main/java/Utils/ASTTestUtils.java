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
            System.out.println(root.getValue());
        } else {
            List<ASTNode> list = root.getChildSet();
            for (ASTNode astNode : list) {
                ASTTestPrint(astNode);
            }
        }
    }

    public static void main(String args[]) {
//        单行缺省值插入通过测试
//        AST ast = SQLParserUntil.AssSQlASTgen("INSERT INTO Persons VALUES ('Gates', 'Bill', 'Xuanwumen 10', 'Beijing');");
//        ASTTestPrint(ast.getRoot_set().get(0));

        //单行非缺省值通过测试
//        AST ast = SQLParserUntil.AssSQlASTgen("INSERT INTO Persons (LastName, Address) VALUES ('Wilson', 'Champs-Elysees');");
//        ASTTestPrint(ast.getRoot_set().get(0));

        //多行插入缺省值
        AST ast = SQLParserUntil.AssSQlASTgen("insert into persons" +
                "(id_p, lastname , firstName, city )" +
                "values" +
                "(200,'haha' , 'deng' , 'shenzhen')," +
                "(201,'haha2' , 'deng' , 'GD')," +
                "(202,'haha3' , 'deng' , 'Beijing');");
        ASTTestPrint((ast.getRoot_set().get(0)));
    }
}
