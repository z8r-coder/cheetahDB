package Parser.Builder;

import Parser.Visitor.SchemaStatVisitor;

import java.util.List;

/**
 * Created by ruanxin on 2017/8/16.
 */
public interface SQLInsertBuilder {

    /**
     * 获取表名
     * @return
     */
    public String from();

    /**
     * 获取插入的行 todo此处可为string
     * @return
     */
    public List<SchemaStatVisitor.Column> columns();

    /**
     * 获取插入的值
     * @return
     */
    public List<List<SchemaStatVisitor.Value>> values();
}
