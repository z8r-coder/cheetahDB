package parser.builder;

import models.Relationship;
import models.Value;
import parser.SQLASTType;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by ruanxin on 2017/8/16.
 */
public interface SQLUpdateBuilder extends SQLBuilder {
    /**
     * 获取表名
     * @return
     */
    public String from();

    /**
     * 获取赋值语句
     * @return
     */
    public Map<String, Value> AssValue();

    /**
     * 筛选
     * @return
     */
    public Set<Relationship> where();

    /**
     * 筛选条件
     * @return
     */
    public List<String> AndOr();

    /**
     * 获取语句类型
     * @return
     */
    public SQLASTType grammerType();
}
