package utils;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import java.util.Date;

/**
 * 时间Utils
 * Created by rx on 2017/9/5.
 */
public class DateUtils {
    public static final String TIME_CACHE_FORMAT = "yyyyMMddHHmmss";

    public static final String MAX_TIME_STAMP = "99999999999999";

    public static String convertDate2Str(Date date, String dateFormate) {
        if (date != null && !StringUtils.isBlank(dateFormate)) {
            DateTime dt = new DateTime(date);
            return dt.toString(DateTimeFormat.forPattern(dateFormate));
        } else {
            return "";
        }
    }

    public static void main(String arg[]) {
        long dateTime = Long.parseLong(DateUtils.convertDate2Str(new Date(), DateUtils.TIME_CACHE_FORMAT));
//        try {
//            Thread.sleep(3300);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        long dateTime2 = Long.parseLong(DateUtils.convertDate2Str(new Date(), DateUtils.TIME_CACHE_FORMAT));
        System.out.println(dateTime);
        System.out.println(dateTime2);
        System.out.println(Long.parseLong(DateUtils.MAX_TIME_STAMP));
        System.out.println(dateTime2 > dateTime);
    }
}
