package parser.AstGen;

/**
 * Created by ruanxin on 2017/8/12.
 */
public class SQLEntryOps {
    private String left;//一般为column name
    private String op;
    private String right;//number or varchar

    public SQLEntryOps(String left, String op, String right) {
        this.left = left;
        this.op = op;
        this.right = right;
    }

    public void setLeft(String left) {
        this.left = left;
    }

    public String getLeft() {
        return left;
    }

    public void setOp(String op) {
        this.op = op;
    }

    public String getOp() {
        return op;
    }

    public void setRight(String right) {
        this.right = right;
    }

    public String getRight() {
        return right;
    }
}
