package Service.SQLBptService.InsertService;

import Constants.SQLErrorCode;
import Engine.BPlusTree.Node;
import Engine.Bplustree;
import Models.Column;
import Models.Row;
import Models.Table;
import Models.Value;
import Parser.Builder.SQLInsertBuilder;
import Exception.InsertException;

import java.util.List;

/**
 * 单行非缺省插入
 * Created by rx on 2017/8/26.
 */
public class InsertSingleService implements InsertService{

    public void invoke(Table table, SQLInsertBuilder insertBuilder) throws Exception {
        List<List<Value>> values = insertBuilder.values();
        List<Column> columns = insertBuilder.columns();

        List<Value> singleValues = values.get(0);

        if (columns.size() != singleValues.size()) {
            throw new InsertException(SQLErrorCode.SQL00002);
        }

        Row row = new Row();

        for (int i = 0; i < columns.size();i++) {
            if (columns.get(i).getDataType() != singleValues.get(i).getDataType()) {
                throw new InsertException(SQLErrorCode.SQL00005);
            }
            if (columns.get(i).getPrimaryKey()) {
                if (singleValues.get(i) == null) {
                    throw new InsertException(SQLErrorCode.SQL00006);
                }
                //设置该行的主键value
                row.setPRIMARY_KEY(singleValues.get(i));
            }
            singleValues.get(i).setColumName(columns.get(i).getName());
        }
    }
}
