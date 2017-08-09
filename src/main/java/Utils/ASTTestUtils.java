package Utils;

import Parser.AST;
import Parser.ASTNode;
import Parser.SQLParserProxy;

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
        //单行缺省值插入通过测试
        AST ast = SQLParserProxy.AssSQlASTgen("INSERT INTO Persons VALUES ('Gates', 'Bill', 'Xuanwumen 10', 'Beijing');");
        ASTTestPrint(ast.getRoot_set().get(0));
    }
}
