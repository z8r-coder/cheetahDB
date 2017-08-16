package Parser.Builder.impl;

import Parser.AST;
import Parser.AstGen.SQLDeleteAst;
import Parser.Builder.SQLBuilder;
import Parser.Builder.SQLDeleteBuilder;
import Parser.SQLParserUtils;
import Parser.Visitor.SchemaStatVisitor;

import java.util.List;
import java.util.Set;

/**
 * Created by ruanxin on 2017/8/16.
 */
public class SQLDeleteBuilderImpl implements SQLDeleteBuilder, SQLBuilder {

    private SQLDeleteAst deleteAst;

    public SQLDeleteBuilderImpl() {
    }
    public void build(String sql) throws Exception {
        AST past = SQLParserUtils.AssSQlASTgen(sql);
        deleteAst = new SQLDeleteAst(past);
        deleteAst.accept(new SchemaStatVisitor());
    }

    public String grammerType() {
        return "delete";
    }

    public String from() {
        return deleteAst.getTableName();
    }

    public Set<SchemaStatVisitor.Relationship> where() {
        return deleteAst.getRls();
    }

    public List<String> AndOr() {
        return deleteAst.getRs();
    }
}
