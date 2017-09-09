package executor.sqlexecutor.selectexecutor;

import models.Column;
import models.Relationship;
import parser.SQLDataType;
import parser.SortCode;
import parser.Token;

import java.util.Set;

/**
 * Created by rx on 2017/9/1.
 */
public class SelectUtils {

    /**
     * 检查比较参数是否匹配
     * @param rps
     * @return
     */
    public static boolean checkDataType(Set<Relationship> rps) {
        for (Relationship rp : rps) {
            Column left = rp.getLeft();
            Token right = rp.getRight();

            if (left.getDataType() == SQLDataType.INTEGER){
                if (right.getSortCode() != SortCode.NUMBER) {
                    return false;
                }
            } else if (left.getDataType() == SQLDataType.VARCHAR) {
                if (right.getSortCode() != SortCode.STRING) {
                    return false;
                }
            }
        }
        return true;
    }
}
