package Parser.AstGen;

import Parser.AST;
import Parser.ASTNode;
import Parser.Visitor.SQLASTVisitor;
import Parser.Visitor.SchemaStatVisitor;

import java.util.ArrayList;
import java.util.List;


/**
 * select ast
 *
 * Created by ruanxin on 2017/8/9.
 */
public class SQLSelectAst implements BaseAST{
    private AST ast;
    //查询的column
    private List<SchemaStatVisitor.Column> slt_col;
    //按顺序结合
    private List<SchemaStatVisitor.Relationship> rls;
    //and or
    private List<String> rs = new ArrayList<String>();
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

    public void setSlt_col(List<SchemaStatVisitor.Column> slt_col) {
        this.slt_col = slt_col;
    }

    public List<SchemaStatVisitor.Column> getSlt_col() {
        return slt_col;
    }

    public void setTable_name(String table_name) {
        this.table_name = table_name;
    }

    public String getTable_name() {
        return table_name;
    }

    public void setRls(List<SchemaStatVisitor.Relationship> rls) {
        this.rls = rls;
    }

    public List<SchemaStatVisitor.Relationship> getRls() {
        return rls;
    }
}
