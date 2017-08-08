package Parser.Visitor;

/**
 * Created by ruanxin on 2017/8/8.
 */
public interface PrintableASTVisitor extends SQLASTVisitor {
    String toString();
}
