package exception;

/**
 * Created by RX on 2017/7/5.
 */
public class NodeClassNotFoundException extends Exception {
    public NodeClassNotFoundException (String name) {
        System.err.println("There is NodeClassNotFoundException in " + name + "!");
    }
}
