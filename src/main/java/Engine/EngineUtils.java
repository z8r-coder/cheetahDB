package Engine;

import java.util.List;

/**
 * Created by ruanxin on 2017/8/19.
 */
public class EngineUtils {
    public static int dichotomy(List<Integer> list, int target) {
        int start = 0;
        int end = list.size() - 1;
        int mid = (start + end) / 2;

        while (list.get(mid) != target && end > start) {
            if (list.get(mid) > target) {
                end = mid - 1;
            } else if (list.get(mid) < target) {
                start = mid + 1;
            }
            mid = (start + end) / 2;
        }
        return mid;
    }
    public static int dichotomy(List<String> list, String target) {
        int start = 0;
        int end = list.size() - 1;
        int mid = (start + end) / 2;
        while (list.get(mid).hashCode() != target.hashCode() && end > start) {
            if (list.get(mid).hashCode() > target.hashCode()) {
                end = mid - 1;
            } else if (list.get(mid).hashCode() < target.hashCode()) {
                start = mid + 1;
            }
            mid = (start + end) / 2;
        }
        return mid;
    }
    public static void main(String args[]) {
    }
}
