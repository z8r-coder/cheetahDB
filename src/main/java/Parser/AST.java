package Parser;

import java.util.ArrayList;
import java.util.List;
import Exception.NotRootException;

/**
 * Created by roy on 2017/7/29.
 */
public class AST {
    private List<ASTNode> root_set = new ArrayList<ASTNode>();
    private String name;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setRoot_set(List<ASTNode> root_set) {
        this.root_set = root_set;
    }

    public List<ASTNode> getRoot_set() {
        return root_set;
    }

    public void addRootNode(ASTNode astNode) throws Exception {
        if (astNode.get_isRoot()) {
            this.root_set.add(astNode);
            return;
        }
        throw new NotRootException(getClass().toString());
    }
}
