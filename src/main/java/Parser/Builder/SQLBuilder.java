package Parser.Builder;

import BPT.BPT;
import Parser.AstGen.BaseAST;

/**
 * Created by ruanxin on 2017/8/16.
 */
public interface SQLBuilder {
    /**
     * 生成语法树
     */
    public void build(String sql) throws Exception;

    /**
     * 获取语法类型
     * @return
     */
    public String grammerType();
}
