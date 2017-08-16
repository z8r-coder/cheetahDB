package Parser.Builder;

import BPT.BPT;
import Parser.AstGen.BaseAST;

/**
 * Created by ruanxin on 2017/8/16.
 */
public interface SQLBuildAdapter {
    /**
     * 生成语法树
     */
    public void build(String sql);
}
