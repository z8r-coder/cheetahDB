package Parser.Builder.impl;

import Parser.AST;
import Parser.AstGen.SQLUpdateAst;
import Parser.Builder.SQLBuilder;
import Parser.Builder.SQLUpdateBuilder;
import Parser.SQLParserUtils;
import Parser.Visitor.SchemaStatVisitor;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by ruanxin on 2017/8/16.
 */
public class SQLUpdateBuilderImpl implements SQLUpdateBuilder, SQLBuilder {
    private SQLUpdateAst updateAst;

    public SQLUpdateBuilderImpl() {
    }
    public void build(String sql) throws Exception {
        AST past = SQLParserUtils.AssSQlASTgen(sql);
        updateAst = new SQLUpdateAst(past);
        updateAst.accept(new SchemaStatVisitor());
    }

    public String grammerType() {
        return "Update";
    }

    public String from() {
        return updateAst.getTableName();
    }

    public Map<String, SchemaStatVisitor.Value> AssValue() {
        return updateAst.getAssMap();
    }

    public Set<SchemaStatVisitor.Relationship> where() {
        return updateAst.getRls();
    }

    public List<String> AndOr() {
        return updateAst.getRs();
    }
}
