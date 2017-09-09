package parser.builder;

import parser.SQLASTType;

/**
 * Created by ruanxin on 2017/8/16.
 */
public interface SQLCreateDbBuilder extends SQLBuilder {
    /**
     * 返回创建数据库的名字
     * @return
     */
    public String dataBaseName();

    /**
     * 获取语句类型
     * @return
     */
    public SQLASTType grammerType();

}
