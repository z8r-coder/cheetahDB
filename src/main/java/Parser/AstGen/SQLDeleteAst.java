package Parser.AstGen;

import Models.Relationship;
import Parser.AST;
import Parser.ASTNode;
import Parser.Visitor.SQLASTVisitor;
import Parser.Visitor.SchemaStatVisitor;

import java.util.List;
import java.util.Set;

/**
 * Created by ruanxin on 2017/8/12.
 */
public class SQLDeleteAst implements BaseAST {
    private AST ast;

    private Set<Relationship> rls;

    private List<String> rs;

    private String tableName;
    public SQLDeleteAst(AST ast) {
        this.ast = ast;
    }

    public void setAst(AST ast) {
        this.ast = ast;
    }

    public AST getAst() {
        return ast;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setRs(List<String> rs) {
        this.rs = rs;
    }

    public List<String> getRs() {
        return rs;
    }

    public void setRls(Set<Relationship> rls) {
        this.rls = rls;
    }

    public Set<Relationship> getRls() {
        return rls;
    }

    public void accept(SQLASTVisitor visitor) throws Exception {
        visitor.visit(this);
    }
}
