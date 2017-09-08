package parser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by roy on 2017/8/7.
 */
public class SQLAlterPattern {
    public static List<SortCode> alter_list;

    public static List<SortCode> getAlterPattern () {
        if (alter_list == null) {
            alter_list = new ArrayList<SortCode>();
            alter_list.add(SortCode.ALTER);
            alter_list.add(SortCode.TABLE);
            alter_list.add(SortCode.IDENTIFIED);
            alter_list.add(SortCode.OPTION);//可选 add drop alter
            alter_list.add(SortCode.COLUMN);
            alter_list.add(SortCode.IDENTIFIED);
            //类型可缺省
            return alter_list;
        }
        return alter_list;
    }
}
