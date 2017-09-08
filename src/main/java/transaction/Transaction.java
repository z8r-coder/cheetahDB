package transaction;

/**
 * Created by rx on 2017/9/3.
 */
public interface Transaction {
    /**
     * 提交事务
     */
    public void commit();

    /**
     * 回滚事务
     */
    public void rollback();

    /**
     * 监听
     * @param listener
     */
    public void addListener(TransactionListener listener);
}
