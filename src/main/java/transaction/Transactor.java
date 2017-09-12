package transaction;

/**
 * 事务bean
 * Created by rx on 2017/9/12.
 */
public class Transactor {
    /**
     * 事务版本
     */
    private int version;

    /**
     * 属于某个表的事务
     */
    private String tableName;
}
