package exception;

import constants.SQLErrorCode;

/**
 * Created by rx on 2017/8/27.
 */
public class InsertException extends Exception {

    public InsertException(SQLErrorCode errorCode) {
        super(errorCode.getErrorDes());
    }
}
