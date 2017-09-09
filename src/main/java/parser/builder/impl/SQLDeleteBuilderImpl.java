package parser.builder.impl;

import models.Relationship;
import parser.AST;
import parser.astgen.SQLDeleteAst;
import parser.builder.SQLDeleteBuilder;
import parser.SQLASTType;
import parser.SQLParserUtils;
import parser.visitor.SchemaStatVisitor;

import java.util.List;
import java.util.Set;

/**
 * Created by ruanxin on 2017/8/16.
 */
public class SQLDeleteBuilderImpl implements SQLDeleteBuilder {

    private SQLDeleteAst deleteAst;

    public SQLDeleteBuilderImpl() {
    }
    public void build(String sql) throws Exception {
        AST past = SQLParserUtils.AssSQlASTgen(sql);
        deleteAst = new SQLDeleteAst(past);
        deleteAst.accept(new SchemaStatVisitor());
    }

    public SQLASTType grammerType() {
        return deleteAst.getAst().getAstType();
    }

    public String from() {
        return deleteAst.getTableName();
    }

    public Set<Relationship> where() {
        return deleteAst.getRls();
    }

    public List<String> AndOr() {
        return deleteAst.getRs();
    }
}
