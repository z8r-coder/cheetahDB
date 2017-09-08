package executor.sqlbptexecutor.selectexecutor;

import constants.SQLErrorCode;
import engine.Bplustree;
import models.*;
import parser.Builder.SQLSelectBuilder;
import parser.SQLDataType;
import parser.SortCode;
import exception.SelectException;
import utils.ListUtils;
import utils.ServiceUtils;

import java.util.*;

/**
 * todo 暂时搁置
 * 具有索引的列一定得非空
 * 带where的查询
 * Created by rx on 2017/8/26.
 */
public class SelectWithWhereService implements SelectService {

    public SimpleTable invoke(Table table, SQLSelectBuilder selectBuilder) throws SelectException {
        //选中的列名
        Set<String> columns = selectBuilder.columns();
        //where关系筛选
        Set<Relationship> relations = selectBuilder.where();
        //关系集合
        List<String> AndOr = selectBuilder.AndOr();
        //所有的行
        List<Row> rows = table.getRows();
        //标准行
        List<Column> standerCol = table.getColumns();

        //需要返回的simpleTable
        SimpleTable simpleTable = new SimpleTable();
        //若该表中还不存在行
        if (rows.size() == 0) {
            if (columns.size() == 1) {
                Iterator it = columns.iterator();
                String column = (String) it.next();
                if (column.equals("*")) {
                    simpleTable.setColumns(ListUtils.List2Set(standerCol,0,standerCol.size() - 1));
                    simpleTable.setSimpleRows(new ArrayList<SimpleRow>());
                    return simpleTable;
                }
            }
            simpleTable.setColumns(columns);
            simpleTable.setSimpleRows(new ArrayList<SimpleRow>());
            return simpleTable;
        }

        //获取主键的列名，并根据此来排序
        String primaryKey = rows.get(0).getPRIMARY_KEY().getColumName();
        if (!ServiceUtils.checkColumn(columns, standerCol)) {
            throw new SelectException(SQLErrorCode.SQL00042);
        }
        if (!SelectUtils.checkDataType(relations)) {
            throw new SelectException(SQLErrorCode.SQL00044);
        }
        //筛选出来的行集合,先只能选行集合

        List<Row> resList = null;
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
                //命中索引,索引不能为空
                if(resList == null) {
                    //第一次查找
                    resList = bpt.searchForList(value, rs.getOperator());
                    continue;
                }

            } else {
                //未命中索引
                resList = rowFilter(rows, value, rs.getOperator());
                //若存在多个集合，则需要排序，按主键排序
                qsort(resList,0, resList.size() - 1, primaryKey);
            }
        }

        return null;
    }

    /**
     * 求两个集合的并集
     * @param prevList
     * @param nextList
     * @return
     */
    private List<Row> andList(List<Row> prevList, List<Row> nextList) {
        if (prevList == null || nextList == null
                || prevList.size() == 0 || nextList.size() == 0) {
            return new ArrayList<Row>();
        }
        List<Row> resList = new ArrayList<Row>();
        int pointerPrev = 0;
        int pointerNext = 0;

        while (pointerPrev <prevList.size() && pointerNext < nextList.size()) {

        }
        return null;
    }

    /**
     * 快排找pivote
     * @param start
     * @param end
     * @return
     */
    private int pivote(int start, int end) {
        return (start + end) / 2;
    }

    /**
     * 递归排，根据筛选的value来对row进行排序
     * @param rows
     * @param low
     * @param high
     * @param columnName 依赖排序的列
     * @return
     */
    public int partition(List<Row> rows, int low, int high, String columnName) {
        int hi = high;
        Row keyRow = rows.get(pivote(low, high));
        rows.set(pivote(low,high), rows.get(high));
        rows.set(high, keyRow);
        while (high > low) {
            Value lowValue = rows.get(low).getValue(columnName);
            Value keyValue = keyRow.getValue(columnName);
            while (lowValue.compareTo(keyValue) <= 0 && high > low) {
                low++;
                lowValue = rows.get(low).getValue(columnName);
            }

            Value highValue = rows.get(high).getValue(columnName);
            while (highValue.compareTo(keyValue) >= 0 && high > low) {
                high--;
                highValue = rows.get(high).getValue(columnName);
            }

            if (low < high) {
                Row tmp = rows.get(low);
                rows.set(low, rows.get(high));
                rows.set(high, tmp);
            }
        }
        rows.set(hi, rows.get(high));
        rows.set(high, keyRow);
        return high;
    }

    public void qsort(List<Row> rows, int low, int high, String columnName) {
        if (low >= high) {
            return;
        }
        int index = partition(rows, low, high,columnName);
        qsort(rows, low, index - 1,columnName);
        qsort(rows, index + 1, high,columnName);
    }

    /**
     * 获取筛选后的列表，NULL类型的不参与筛选
     * @param rows
     * @param value
     * @param op
     * @return
     */
    private List<Row> rowFilter(List<Row> rows, Value value, String op) {
        List<Row> filterRows = new ArrayList<Row>();

        if (op.equals("<")) {
            for (Row row : rows) {
                Value compareValue = row.getValue(value.getColumName());
                if (compareValue.getDataType() == SQLDataType.NULL) {
                    //NULL类型不参与筛选
                    continue;
                }
                if (value.compareTo(compareValue) > 0) {
                    filterRows.add(row);
                }
            }
        } else if (op.equals(">")) {
            for (Row row : rows) {
                Value compareValue = row.getValue(value.getColumName());
                if (compareValue.getDataType() == SQLDataType.NULL) {
                    continue;
                }
                if (value.compareTo(row.getValue(value.getColumName())) < 0) {
                    filterRows.add(row);
                }
            }
        } else if (op.equals(">=")) {
            for (Row row : rows) {
                Value compareValue = row.getValue(value.getColumName());
                if (compareValue.getDataType() == SQLDataType.NULL) {
                    continue;
                }
                if (value.compareTo(row.getValue(value.getColumName())) <= 0) {
                    filterRows.add(row);
                }
            }
        } else if (op.equals("<=")) {
            for (Row row : rows) {
                Value compareValue = row.getValue(value.getColumName());
                if (compareValue.getDataType() == SQLDataType.NULL) {
                    continue;
                }
                if (value.compareTo(row.getValue(value.getColumName())) >= 0) {
                    filterRows.add(row);
                }
            }
        } else if (op.equals("=")) {
            for (Row row : rows) {
                Value compareValue = row.getValue(value.getColumName());
                if (compareValue.getDataType() == SQLDataType.NULL) {
                    continue;
                }
                if (value.compareTo(row.getValue(value.getColumName())) == 0) {
                    filterRows.add(row);
                }
            }
        }
        return filterRows;
    }

    public static void main(String args[]) {
        Row a = new Row(new Value("a",SQLDataType.VARCHAR));
        Row b = new Row(new Value("b", SQLDataType.VARCHAR));
        Row tmp = a;
        a = b;
        b = tmp;
        System.out.println(a.getPRIMARY_KEY().getVal());
        System.out.println(b.getPRIMARY_KEY().getVal());
    }
}
