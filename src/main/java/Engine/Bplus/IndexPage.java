package Engine.Bplus;

import java.util.List;

/**
 * 索引页
 * Created by rx on 2017/8/19.
 */
public interface IndexPage<T,E> extends Page<T,E> {

    /**
     * 插入条目
     * @param entry
     * @return
     */
    public boolean insert(Entry<T,E> entry);
}
