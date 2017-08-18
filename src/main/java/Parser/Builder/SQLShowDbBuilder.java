package Parser.Builder;

import Parser.SQLASTType;

/**
 * Created by ruanxin on 2017/8/16.
 */
public interface SQLShowDbBuilder extends SQLBuilder {
    /**
     * 获取语句类型
     * @return
     */
    public SQLASTType grammerType();
}
