package parser.builder;

/**
 * Created by ruanxin on 2017/8/16.
 */
public interface SQLBuilder {
    /**
     * 生成语法树
     */
    public void build(String sql) throws Exception;

}
