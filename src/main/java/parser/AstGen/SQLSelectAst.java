package parser.AstGen;

import models.Relationship;
import parser.AST;

import parser.Visitor.SQLASTVisitor;

import java.util.List;
import java.util.Set;


/**
 * select ast
 *
 * Created by ruanxin on 2017/8/9.
 */
public class SQLSelectAst implements BaseAST{
    private AST ast;
    //查询的column
    private Set<String> slt_col;
    //按顺序结合
    private Set<Relationship> rls;
    //and or
    private List<String> rs;
    //查询的表
    private String table_name;
    public SQLSelectAst(AST ast) {
        this.ast = ast;
    }

    public void setAst(AST ast) {
        this.ast = ast;
    }

    public AST getAst() {
        return ast;
    }

    public void accept(SQLASTVisitor visitor) throws Exception {
        visitor.visit(this);
    }

    public void setSlt_col(Set<String> slt_col) {
        this.slt_col = slt_col;
    }

    public Set<String> getSlt_col() {
        return slt_col;
    }

    public void setTable_name(String table_name) {
        this.table_name = table_name;
    }

    public String getTable_name() {
        return table_name;
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
}
