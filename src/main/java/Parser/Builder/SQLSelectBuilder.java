package Parser.Builder;

import Models.Column;
import Models.Relationship;
import Parser.SQLASTType;
import Parser.Visitor.SchemaStatVisitor;

import java.util.List;
import java.util.Set;

/**
 * Created by ruanxin on 2017/8/16.
 */
public interface SQLSelectBuilder extends SQLBuilder {
    /**
     * 查询的表
     * @return
     */
    public String from();

    /**
     * 查询的列 todo 此处类型可用String
     * @return
     */
    public List<Column> columns();

    /**
     * 筛选条件
     * @return
     */
    public Set<Relationship> where();

    /**
     * 筛选条件关系
     * @return
     */
    public List<String> AndOr();

    /**
     * 获取语句类型
     * @return
     */
    public SQLASTType grammerType();
}
