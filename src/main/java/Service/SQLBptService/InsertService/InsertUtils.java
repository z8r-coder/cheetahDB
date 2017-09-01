package Service.SQLBptService.InsertService;

import Models.Column;
import Models.Relationship;
import Models.Value;
import Parser.SQLDataType;
import Parser.SortCode;
import Parser.Token;

import java.util.Set;

/**
 * Created by rx on 2017/9/1.
 */
public class InsertUtils {

    /**
     * 检查比较参数是否匹配
     * @param rps
     * @return
     */
    public boolean checkDataType(Set<Relationship> rps) {
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
