package Parser;

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
    private String value;

    public ASTNode(boolean root, boolean leaf, String value) {
        this.root = root;
        this.leaf = leaf;
        this.value = value;
    }
    public void setValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
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
}
