package Utils;

import Parser.ASTNode;

import java.util.List;

/**
 * Created by ruanxin on 2017/8/9.
 */
public class ASTUtils {
    /**
     * 结点集合为基础，深度优先输出
     * @param nodes
     * @return
     */
    public static String TreeSetDfsPrintUtils(List<ASTNode> nodes, StringBuilder sb) {
        assert nodes == null;

        for (ASTNode node : nodes) {
            if (node.get_isLeaf()) {
                sb.append(node.getValue() + "_");
            } else {
                List<ASTNode> list = node.getChildSet();
                TreeSetDfsPrintUtils(list, sb);
            }
        }
        return sb.substring(0, sb.length() - 1);
    }

    public static String TreeNodeDfsPrintUtils(ASTNode node, StringBuilder sb) {
        return null;
    }

}
