package Service.SQLBptService.SelectService;

import Engine.Bplustree;
import Models.Relationship;
import Models.SimpleTable;
import Models.Table;
import Parser.Builder.SQLSelectBuilder;
import Parser.SortCode;

import javax.management.relation.Relation;
import java.util.List;
import java.util.Set;

/**
 * 带where的查询
 * Created by rx on 2017/8/26.
 */
public class SelectWithWhereService implements SelectService {

    public SimpleTable invoke(Table table, SQLSelectBuilder selectBuilder) {
        Set<String> column = selectBuilder.columns();

        Set<Relationship> relations = selectBuilder.where();

        List<String> AndOr = selectBuilder.AndOr();

        for (Relationship rs : relations) {
            String columnName = rs.getLeft().getName();
            if (rs.getRight().getSortCode() == SortCode.STRING) {
                String indexName = rs.getRight().getValue();
            } else if (rs.getRight().getSortCode() == SortCode.INTEGER) {
                int indexName = Integer.parseInt(rs.getRight().getValue());
            }
            Bplustree bpt = table.getIndexTree(columnName);
            if (bpt != null) {
                //命中索引
                bpt.searchForList()
            }
        }
        return null;
    }
}
