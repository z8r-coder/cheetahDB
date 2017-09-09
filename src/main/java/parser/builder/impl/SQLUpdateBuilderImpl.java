package parser.builder.impl;

import models.Relationship;
import models.Value;
import parser.AST;
import parser.astgen.SQLUpdateAst;
import parser.builder.SQLUpdateBuilder;
import parser.SQLASTType;
import parser.SQLParserUtils;
import parser.visitor.SchemaStatVisitor;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by ruanxin on 2017/8/16.
 */
public class SQLUpdateBuilderImpl implements SQLUpdateBuilder {
    private SQLUpdateAst updateAst;

    public SQLUpdateBuilderImpl() {
    }
    public void build(String sql) throws Exception {
        AST past = SQLParserUtils.AssSQlASTgen(sql);
        updateAst = new SQLUpdateAst(past);
        updateAst.accept(new SchemaStatVisitor());
    }

    public SQLASTType grammerType() {
        return updateAst.getAst().getAstType();
    }

    public String from() {
        return updateAst.getTableName();
    }

    public Map<String, Value> AssValue() {
        return updateAst.getAssMap();
    }

    public Set<Relationship> where() {
        return updateAst.getRls();
    }

    public List<String> AndOr() {
        return updateAst.getRs();
    }
}
