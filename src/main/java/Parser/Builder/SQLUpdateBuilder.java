package Parser.Builder;

import Parser.Visitor.SchemaStatVisitor;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by ruanxin on 2017/8/16.
 */
public interface SQLUpdateBuilder {
    /**
     * 获取表名
     * @return
     */
    public String from();

    /**
     * 获取赋值语句
     * @return
     */
    public Map<String, SchemaStatVisitor.Value> AssValue();

    /**
     * 筛选
     * @return
     */
    public Set<SchemaStatVisitor.Relationship> where();

    /**
     * 筛选条件
     * @return
     */
    public List<String> AndOr();
}
