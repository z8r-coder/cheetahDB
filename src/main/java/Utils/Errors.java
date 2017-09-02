package Utils;

/**
 * Created by rx on 2017/9/2.
 */
public class Errors {

    public static IllegalStateException notExpected() {
        return new IllegalStateException("The operation is not supposed to be called!");
    }
}
