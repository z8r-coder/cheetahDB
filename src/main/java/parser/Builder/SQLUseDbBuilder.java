package parser.Builder;

import parser.SQLASTType;

/**
 * Created by ruanxin on 2017/8/16.
 */
public interface SQLUseDbBuilder extends SQLBuilder {

    /**
     * 使用指定的数据库
     * @return
     */
    public String dbName();

    /**
     * 获取语句类型
     * @return
     */
    public SQLASTType grammerType();
}
