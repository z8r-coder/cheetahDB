package Models;

import java.util.ArrayList;
import java.util.List;

/**
 * 行描述
 * Created by rx on 2017/8/26.
 */
public class Row {
    /**
     * 该行的主键
     */
    private Value PRIMARY_KEY;
    /**
     *
     */
    private List<Value> values;

    public Row(Value PRIMARY_KEY) {
        this.PRIMARY_KEY = PRIMARY_KEY;
    }

    public void setPRIMARY_KEY(Value PRIMARY_KEY) {
        this.PRIMARY_KEY = PRIMARY_KEY;
    }

    public Value getPRIMARY_KEY() {
        return PRIMARY_KEY;
    }
}
