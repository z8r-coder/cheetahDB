package engine.fulltextindex;

import models.Row;
import models.Value;
import parser.SQLDataType;
import utils.ConfigUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 全文索引每页
 * Created by rx on 2017/9/11.
 */
public class TextPage {
    /**
     * 该页在文件中的具体位置
     */
    private long id;

    /**
     * 该页中的条目
     */
    private List<Row> rows;

    public TextPage (long id) {
        this.id = id;

        ConfigUtils.getConfig().loadPropertiesFromSrc();
        int entry_size = Integer.parseInt(ConfigUtils.getConfig().getPageSize());
        rows = new ArrayList<Row>(entry_size);
    }

    /**
     * 全文搜索
     * @param op
     * @param colName
     * @param value
     * @param dataType
     * @param resRow
     */
    public void search(String op, String colName, Object value,
                       SQLDataType dataType, List<Row> resRow) {
        if (op.equals("=")) {
            if (dataType == SQLDataType.INTEGER) {
                for (Row row : rows) {
                    Value vv = row.getValue(colName);
                    if (vv.getDataType() == SQLDataType.NULL) {
                        //null值不加入
                        continue;
                    }
                    if (vv.getIntVal().compareTo((Integer) value) == 0) {
                        resRow.add(row);
                    }
                }
            } else if (dataType == SQLDataType.VARCHAR) {
                for (Row row : rows) {
                    Value vv = row.getValue(colName);
                    if (vv.getDataType() == SQLDataType.NULL) {
                        continue;
                    }
                    if (vv.getVal().compareTo((String) value) == 0) {
                        resRow.add(row);
                    }
                }
            }
        } else if (op.equals("<")) {
            if (dataType == SQLDataType.INTEGER) {
                for (Row row : rows) {
                    Value vv = row.getValue(colName);
                    if (vv.getDataType() == SQLDataType.NULL) {
                        continue;
                    }

                    if (vv.getIntVal().compareTo((Integer) value) < 0) {
                        resRow.add(row);
                    }
                }
            } else if (dataType == SQLDataType.VARCHAR) {
                for (Row row : rows) {
                    Value vv = row.getValue(colName);
                    if (vv.getDataType() == SQLDataType.NULL) {
                        continue;
                    }

                    if (vv.getVal().compareTo((String) value) < 0) {
                        resRow.add(row);
                    }
                }
            }
        } else if (op.equals(">")) {
            if (dataType == SQLDataType.INTEGER) {
                for (Row row : rows) {
                    Value vv = row.getValue(colName);
                    if (vv.getDataType() == SQLDataType.NULL) {
                        continue;
                    }

                    if (vv.getIntVal().compareTo((Integer) value) > 0) {
                        resRow.add(row);
                    }
                }
            } else if (dataType == SQLDataType.VARCHAR) {
                for (Row row : rows) {
                    Value vv = row.getValue(colName);
                    if (vv.getDataType() == SQLDataType.NULL) {
                        continue;
                    }

                    if (vv.getVal().compareTo((String) value) > 0) {
                        resRow.add(row);
                    }
                }
            }
        } else if (op.equals("<=")) {
            search("<",colName,value,dataType,resRow);
            search("=",colName,value,dataType,resRow);
        } else if(op.equals(">=")) {
            search(">", colName, value, dataType, resRow);
            search("=", colName, value, dataType, resRow);
        }
    }

    /**
     * 支持多行插入
     * @param rows
     */
    public void insert(List<Row> rows) {

    }

    public static void main(String arg[]) {
        List<String> list = new ArrayList<String>(1024);
        list.size();
    }
}
