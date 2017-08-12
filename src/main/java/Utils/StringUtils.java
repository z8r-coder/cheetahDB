package Utils;

/**
 * Created by ruanxin on 2017/8/13.
 */
public class StringUtils {

    public static Integer string2Integer(String in) {
        if (in == null) {
            return null;
        }
        in = in.trim();
        if (in.length() == 0) {
            return null;
        }

        try {
            return Integer.parseInt(in);
        } catch (NumberFormatException e) {

            return null;
        }

    }
}
