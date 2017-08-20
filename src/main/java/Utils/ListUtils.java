package Utils;

import Support.Logging.Log;
import Support.Logging.LogFactory;

import java.util.List;

/**
 * Created by ruanxin on 2017/8/16.
 */
public class ListUtils {

    public final static Log log = LogFactory.getLog(ListUtils.class);

    /**
     * 容器是否为空
     * @param list
     * @return
     */
    public static boolean isEmpty(List list) {
        if (list == null || list.size() == 0) {
            return true;
        }
        return false;
    }


    public static boolean equals(List<Object> a, List<Object> b) {
        if (a == null) {
            return b == null;
        }
        if (a.size() != b.size()) {
            return false;
        }
        int length = a.size();
        boolean isEq = true;
        for (int i = 0; i < length; i++) {
            if (!a.get(i).equals(b.get(i))) {
                isEq = false;
                break;
            }
        }
        return isEq;
    }

    /**
     * for example list = [0,1,2,3,4,5,6,7,8]
     *             target = [];
     * copyFromTo(3,6)
     * target = [3,4,5,6]
     * @param start
     * @param end
     * @param list
     * @param target
     */
    public static void copyFromTo(int start, int end, List list, List target) {
        if (end >= list.size()) {
            return;
        }
        for (;start <= end;start++) {
            target.add(list.get(start));
        }
    }
}
