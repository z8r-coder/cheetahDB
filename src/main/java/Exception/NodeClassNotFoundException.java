package Exception;

import BPT.Node;

/**
 * Created by Administrator on 2017/7/5.
 */
public class NodeClassNotFoundException extends Exception {
    public NodeClassNotFoundException (String name) {
        System.err.println("There is NodeClassNotFoundException in " + name + "!");
    }
}
