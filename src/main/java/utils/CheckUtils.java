package utils;

/**
 * Created by rx on 2017/9/6.
 */
public class CheckUtils {

    public static void state(boolean expectedCondition) {
        if (!expectedCondition) {
            throw new IllegalStateException();
        }
    }

    public static void state(boolean expectedCondition, String message, Object... args) {
        if (!expectedCondition) {
            throw new IllegalStateException(String.format(message, args));
        }
    }
}
