package Parser.Builder;

import Parser.RelationOps;

import java.util.List;
import java.util.Set;

/**
 * Created by ruanxin on 2017/8/16.
 */
public interface SQLDeleteBuilder {

    /**
     * 获取表名
     * @return
     */
    public String from();

    /**
     * where后筛选
     * @return
     */
    public Set<RelationOps> where();

    /**
     * 筛选关系连接
     * @return
     */
    public List<String> AndOr();
}
