package parser.builder;

import models.Relationship;
import parser.SQLASTType;

import java.util.List;
import java.util.Set;

/**
 * Created by ruanxin on 2017/8/16.
 */
public interface SQLDeleteBuilder extends SQLBuilder {

    /**
     * 获取表名
     * @return
     */
    public String from();

    /**
     * where后筛选
     * @return
     */
    public Set<Relationship> where();

    /**
     * 筛选关系连接
     * @return
     */
    public List<String> AndOr();

    /**
     * 获取语句类型
     * @return
     */
    public SQLASTType grammerType();
}
