package Engine;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

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
        //校正数据
        if (list.get(mid) >= target) {
            return mid;
        } else {
            return mid + 1;
        }
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
        if (list.get(mid).hashCode() >= target.hashCode()) {
            return mid;
        } else {
            return mid + 1;
        }
    }
    public static void main(String args[]) {
//        List<Integer> list = new ArrayList<Integer>();
//        list.add(1);list.add(3);list.add(5);list.add(9);
//        list.add(10);list.add(11);list.add(15);list.add(21);
//        System.out.println(list.size());
//        int mid = dichotomy(list,12);
//        System.out.println(mid);

//        List<String> list = new ArrayList<String>();
//        list.add("a");
//        list.add("c");
//        list.add("f");
//        list.add("g");
//        list.add("m");
//        list.add("p");
//        list.add("q");
//
//        System.out.println(list.size());
//        int mid = dichotomy(list, "n");
//        System.out.println(mid);

//        List<Integer> list = new ArrayList<Integer>(5);
//        System.out.println(list.size());
    }
}
