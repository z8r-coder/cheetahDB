package Utils;

import Parser.ASTNode;

import java.util.List;

/**
 * Created by ruanxin on 2017/8/9.
 */
public class ASTUtils {
    /**
     * 入参结点集合，深度优先输出，仅仅输出叶节点
     * @param nodes
     * @return
     */
    public static String TreeSetDfsPrintUtils(List<ASTNode> nodes, StringBuilder sb) {
        assert nodes == null;

        for (ASTNode node : nodes) {
            if (node.get_isLeaf()) {
                sb.append(node.getValue() + " ");
            } else {
                List<ASTNode> list = node.getChildSet();
                TreeSetDfsPrintUtils(list, sb);
            }
        }
        return sb.substring(0, sb.length() - 1);
    }

    /**
     * 入参结点，深入优先输出，只输出叶节点
     * @param node
     * @param sb
     * @return
     */
    public static String TreeNodeDfsPrintUtils(ASTNode node, StringBuilder sb) {
        assert node == null;

        if (node.get_isLeaf()) {
            sb.append(node.getValue() + " ");
        } else {
            List<ASTNode> nodes = node.getChildSet();
            for (ASTNode tmp_node : nodes) {
                TreeNodeDfsPrintUtils(tmp_node, sb);
            }
        }
        return sb.substring(0,sb.length() - 1);
    }

    public static ASTNode getASTNodeByValue(ASTNode root,String value) {
        if (root.getValue().equals(value)) {
            return root;
        }
        if (!root.get_isLeaf()) {
            List<ASTNode> nodes = root.getChildSet();
            for (ASTNode tmp_node : nodes) {
                ASTNode astNode = getASTNodeByValue(tmp_node, value);
            }
        }
        return null;
    }

}
