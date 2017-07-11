package BPT;

import BPT.NodeImpl.InteriorNode;

/**
 * Created by roy on 2017/7/3.
 */
public interface Node<T,E> {
        public final static int CAPACITY = 5;
        public void setParentNode(InteriorNode<T,E> parentNode);

//        /**
//         * 插入条目
//         * @param entry
//         * @throws Exception
//         */
//        public void insertEntry(Entry<T,E> entry) throws Exception;
//
//        /**
//         * 查询条目
//         * @param key
//         * @throws Exception
//         */
//        public void queryEntry(T key) throws Exception;
//
//        /**
//         * 删除条目
//         * @param key
//         * @throws Exception
//         */
//        public void deleteEntry(T key) throws Exception;
//
//        /**
//         * 更新条目
//         * @param key
//         * @throws Exception
//         */
//        public void updateEntry(T key) throws Exception;
}
