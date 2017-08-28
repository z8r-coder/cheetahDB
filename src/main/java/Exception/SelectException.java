package Exception;

import Constants.SQLErrorCode;

/**
 * Created by rx on 2017/8/28.
 */
public class SelectException extends Exception {

    public SelectException(SQLErrorCode errorCode) {
        super(errorCode.getErrorDes());
    }
}
