package parser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by roy on 2017/7/29.
 */
public class SQLCreateTablePattern {
    private static List<SortCode> crtTab;

    public static List<SortCode> getCrtTabPat() {
        if (crtTab == null) {
            crtTab = new ArrayList<SortCode>();
            crtTab.add(SortCode.IDENTIFIED);
            crtTab.add(SortCode.OPTION);//目前只支持Integer and Varchar
            crtTab.add(SortCode.LPARENT);
            crtTab.add(SortCode.NUMBER);
            crtTab.add(SortCode.RPARENT);

            return crtTab;
        }
        return crtTab;
    }
}
