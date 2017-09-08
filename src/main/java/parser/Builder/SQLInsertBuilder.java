package parser.Builder;

import models.Value;
import parser.SQLASTType;

import java.util.List;

/**
 * Created by ruanxin on 2017/8/16.
 */
public interface SQLInsertBuilder extends SQLBuilder {

    /**
     * 获取表名
     * @return
     */
    public String from();

    /**
     * 获取插入的行 todo此处可为string
     * @return
     */
    public List<String> columnName();

    /**
     * 获取插入的值
     * @return
     */
    public List<List<Value>> values();

    /**
     * 获取语句类型
     * @return
     */
    public SQLASTType grammerType();
}
