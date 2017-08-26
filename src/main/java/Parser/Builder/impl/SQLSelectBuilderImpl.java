package Parser.Builder.impl;

import Models.Column;
import Models.Relationship;
import Parser.AST;
import Parser.AstGen.SQLSelectAst;
import Parser.Builder.SQLBuilder;
import Parser.Builder.SQLSelectBuilder;
import Parser.SQLASTType;
import Parser.SQLParserUtils;
import Parser.Visitor.SchemaStatVisitor;

import java.util.List;
import java.util.Set;

/**
 * Created by ruanxin on 2017/8/16.
 */
public class SQLSelectBuilderImpl implements SQLSelectBuilder {
    private SQLSelectAst selectAst;

    public SQLSelectBuilderImpl(){
    }
    public void build(String sql) throws Exception {
        AST past = SQLParserUtils.AssSQlASTgen(sql);
        selectAst = new SQLSelectAst(past);
        selectAst.accept(new SchemaStatVisitor());
    }

    public SQLASTType grammerType() {
        return selectAst.getAst().getAstType();
    }

    public String from() {
        return selectAst.getTable_name();
    }

    public List<Column> columns() {
        return selectAst.getSlt_col();
    }

    public Set<Relationship> where() {
        return selectAst.getRls();
    }

    public List<String> AndOr() {
        return selectAst.getRs();
    }
}
