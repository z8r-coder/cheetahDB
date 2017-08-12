package Parser;


import Support.Logging.Log;
import Support.Logging.LogFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rouruan on 2017/7/29.
 */
public class ASTNode {
    /**
     * 是否是根结点
     */
    private boolean root;
    /**
     * 孩子结点
     */
    private List<ASTNode> childSet = new ArrayList<ASTNode>();
    /**
     * 是否是叶子结点
     */
    private boolean leaf;
    /**
     * 值
     */
    private Token token;

    private final static Log LOG = LogFactory.getLog(ASTNode.class);

    public ASTNode(boolean root, boolean leaf, Token token) {
        this.root = root;
        this.leaf = leaf;
        this.token = token;
    }

    public void setToken(Token token) {
        this.token = token;
    }

    public Token getToken() {
        return token;
    }

    public String getValue() {
        if (token == null) {
            LOG.error("token is null");
            return "";
        }
        return token.getValue();
    }
    public void set_isLeaf(boolean leaf) {
        this.leaf = leaf;
    }
    public boolean get_isLeaf() {
        return leaf;
    }

    public void set_isRoot(boolean root) {
        this.root = root;
    }
    public boolean get_isRoot() {
        return root;
    }

    public void addChildNode(ASTNode childNode) {
        childSet.add(childNode);
    }

    public List<ASTNode> getChildSet() {
        return childSet;
    }
}
