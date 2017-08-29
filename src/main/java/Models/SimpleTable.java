package Models;

import java.util.List;
import java.util.Set;

/**
 * Created by Rx on 2017/8/30.
 * 执行select语句后形成的临时table
 */
public class SimpleTable {
    /**
     * 选择的列
     */
    private Set<String> columns;
    /**
     * 筛选出来的行
     */
    private List<SimpleRow> simpleRows;

    public SimpleTable() {

    }

    public SimpleTable(Set<String> columns) {
        this.columns = columns;
    }

    public void setColumns(Set<String> columns) {
        this.columns = columns;
    }

    public void setSimpleRows(List<SimpleRow> simpleRows) {
        this.simpleRows = simpleRows;
    }

    public List<SimpleRow> getSimpleRows() {
        return simpleRows;
    }

    public void addSimpleRow(SimpleRow row) {
        simpleRows.add(row);
    }

    public Set<String> getColumns() {
        return columns;
    }
}
