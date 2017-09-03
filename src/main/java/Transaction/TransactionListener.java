package Transaction;

/**
 * Created by Administrator on 2017/9/3.
 */
public interface TransactionListener {
    /**
     * 成功
     */
    public void onSuccess();

    /**
     * 失败
     * @param e
     */
    public void onError(Exception e);
}
