package Service.SQLBptService.SelectService;

import Constants.SQLErrorCode;
import Engine.Bplustree;
import Models.*;
import Parser.Builder.SQLSelectBuilder;
import Parser.SQLDataType;
import Parser.SortCode;
import Exception.SelectException;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 带where的查询
 * Created by rx on 2017/8/26.
 */
public class SelectWithWhereService implements SelectService {

    public SimpleTable invoke(Table table, SQLSelectBuilder selectBuilder) throws SelectException {
        Set<String> column = selectBuilder.columns();

        Set<Relationship> relations = selectBuilder.where();

        List<String> AndOr = selectBuilder.AndOr();

        List<Row> rows = table.getRows();

        if (!SelectUtils.checkDataType(relations)) {
            throw new SelectException(SQLErrorCode.SQL00044);
        }
        //筛选出来的集合
        List<List<Row>> rowSet = new ArrayList<List<Row>>();

        for (Relationship rs : relations) {
            String columnName = rs.getLeft().getName();
            Value value = null;
            if (rs.getRight().getSortCode() == SortCode.STRING) {
                String indexValue = rs.getRight().getValue();
                value = new Value(indexValue, SQLDataType.VARCHAR);
            } else if (rs.getRight().getSortCode() == SortCode.INTEGER) {
                int indexValue = Integer.parseInt(rs.getRight().getValue());
                value = new Value(indexValue, SQLDataType.INTEGER);
            }
            Bplustree bpt = table.getIndexTree(columnName);
            if (bpt != null) {
                //命中索引
                List<Row> resList = bpt.searchForList(value, rs.getOperator());
            }
        }
        return null;
    }
}
