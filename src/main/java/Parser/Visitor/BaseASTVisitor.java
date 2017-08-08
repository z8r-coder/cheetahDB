package Parser.Visitor;

import Parser.ASTNode;
import Parser.AstGen.ASTContext;
import Parser.AstGen.BaseAST;

import java.util.List;

/**
 * Created by ruanin on 2017/8/9.
 */
public class BaseASTVisitor implements SQLASTVisitor {

    public String toString(ASTContext astContext) {
        StringBuilder sb = new StringBuilder();
        
        BaseAST ast = astContext.getAst();
        List<ASTNode> nodes = ast.getCollection();
        if (nodes == null) {
            // TODO: 2017/8/9 打日志 + name
            return null;
        }
        for (ASTNode node : nodes) {
            if (node.get_isLeaf()) {
                System.out.print(node.getValue());
            }
        }
        return null;
    }

}
