package parser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by roy on 2017/8/11.
 */
public class SQLDataTypePattern {
    private static List<SortCode> dataType;

    public static List<SortCode> getDataTypePattern() {
        if (dataType == null) {
            dataType = new ArrayList<SortCode>();
            dataType.add(SortCode.OPTION);// number or varchar
            dataType.add(SortCode.LPARENT);
            dataType.add(SortCode.NUMBER);
            dataType.add(SortCode.RPARENT);
            dataType.add(SortCode.SEMICOLON);

            return dataType;
        }

        return dataType;
    }

}
