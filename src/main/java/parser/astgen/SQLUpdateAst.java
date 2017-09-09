package parser.astgen;

import models.Relationship;
import models.Value;
import parser.AST;
import parser.visitor.SQLASTVisitor;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by ruanxin on 2017/8/12.
 */
public class SQLUpdateAst implements BaseAST{
    private AST ast;

    private Map<String, Value> AssMap;

    private Set<Relationship> rls;

    private List<String> rs;

    private String tableName;


    public SQLUpdateAst(AST ast) {
        this.ast = ast;
    }

    public void setAst(AST ast) {
        this.ast = ast;
    }

    public AST getAst() {
        return ast;
    }

    public void setAssMap(Map<String, Value> assMap) {
        AssMap = assMap;
    }

    public Map<String, Value> getAssMap() {
        return AssMap;
    }

    public void setRls(Set<Relationship> rls) {
        this.rls = rls;
    }

    public Set<Relationship> getRls() {
        return rls;
    }

    public void setRs(List<String> rs) {
        this.rs = rs;
    }

    public List<String> getRs() {
        return rs;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getTableName() {
        return tableName;
    }

    public void accept(SQLASTVisitor visitor) throws Exception {
        visitor.visit(this);
    }

}
