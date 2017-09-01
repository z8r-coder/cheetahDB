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
        //筛选出来的行集合
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
            List<Row> resList = null;
            if (bpt != null) {
                //命中索引
                resList = bpt.searchForList(value, rs.getOperator());
                rowSet.add(resList);
            } else {
                //未命中索引
                resList = rowFilter(rows, value, rs.getOperator());
            }
        }

        return null;
    }

    private List<Row> rowFilter(List<Row> rows, Value value, String op) {
        List<Row> filterRows = new ArrayList<Row>();

        if (op.equals("<")) {
            for (Row row : rows) {
                if (value.compareTo(row.getValue(value.getColumName())) > 0) {
                    filterRows.add(row);
                }
            }
        } else if (op.equals(">")) {
            for (Row row : rows) {
                if (value.compareTo(row.getValue(value.getColumName())) < 0) {
                    filterRows.add(row);
                }
            }
        } else if (op.equals(">=")) {
            for (Row row : rows) {
                if (value.compareTo(row.getValue(value.getColumName())) <= 0) {
                    filterRows.add(row);
                }
            }
        } else if (op.equals("<=")) {
            for (Row row : rows) {
                if (value.compareTo(row.getValue(value.getColumName())) >= 0) {
                    filterRows.add(row);
                }
            }
        } else if (op.equals("=")) {
            for (Row row : rows) {
                if (value.compareTo(row.getValue(value.getColumName())) == 0) {
                    filterRows.add(row);
                }
            }
        }
        return filterRows;
    }
}
