package Exception;

/**
 * Created by Administrator on 2017/7/6.
 */
public class KeyConflictException extends Exception {
    public KeyConflictException (String name) {
        System.err.println("There is KeyConflictException in " + name + "!");
    }
}
