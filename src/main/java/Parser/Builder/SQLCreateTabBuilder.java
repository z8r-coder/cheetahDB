package Parser.Builder;

import Parser.Visitor.SchemaStatVisitor;

import java.util.List;

/**
 * Created by ruanxin on 2017/8/16.
 */
public interface SQLCreateTabBuilder extends SQLBuilder {

    /**
     * 获取创建的表名
     * @return
     */
    public String tableName();

    /**
     * 获取创建的行信息
     * @return
     */
    public List<SchemaStatVisitor.Column> Columns();

    /**
     * 获取语句类型
     * @return
     */
    public String grammerType();
}
