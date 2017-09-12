package filestore;

import engine.diskbplustree.DiskBplusTree;

/**
 * 数据存储接口
 * Created by rx on 2017/9/4.
 */
public interface DataStore {

    public void write(DiskBplusTree diskBplusTree);
}
