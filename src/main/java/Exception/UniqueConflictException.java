package Exception;

import Constants.TableErrorCode;

/**
 * Created by rx on 2017/8/27.
 */
public class UniqueConflictException extends KeyConflictException {

    public UniqueConflictException(TableErrorCode errorCode) {
        super(errorCode.getTableErrorDes());
    }
}
