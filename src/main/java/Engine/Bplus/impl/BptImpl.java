package Engine.Bplus.impl;

import Engine.Bplus.*;
import Utils.ListUtils;

import java.util.List;

/**
 * Created by rx on 2017/8/19.
 */
public class BptImpl<T,E> implements Bpt<T,E> {
    private Page<T,E> root;
    public BptImpl(Page<T,E> root) {
        this.root = root;
    }

    public boolean insert(Entry<T,E> entry) {
        if (root instanceof LeafPage) {
            //存在唯一结点，即根结点
            Entry<T,E> resEntry = ((LeafPage) root).insert(entry);
            if (resEntry == null) {
                //结点空间未满，正常插入
                return true;
            } else if (((LeafPage) root).length() > BptConstant.PAGE_SIZE) {
                //结点空间满,分裂
                IndexPage<T,E> indexPage = new IndexPageImpl<T, E>();
                indexPage.insert(entry);
                setRoot(indexPage);

                int splitPoint = BptConstant.PAGE_SIZE / 2 + 1;
                List<Entry<T,E>> list = ((LeafPage) root).getEntries();
                //创建左结点
                LeafPage<T,E> leftLeafPage = new LeafPageImpl<T, E>(null, null);
                //创建右结点
                LeafPage<T,E> rightLeafPage = new LeafPageImpl<T, E>(leftLeafPage, null);
                leftLeafPage.setFirst(true);
                rightLeafPage.setLast(true);

                //初始化左结点的条目数
                List<Entry<T,E>> leftEntries = leftLeafPage.getEntries();
                ListUtils.copyFromTo(0, splitPoint - 1, list, leftEntries);

                //初始化右节点的条目数
                List<Entry<T,E>> rightEntries = rightLeafPage.getEntries();
                ListUtils.copyFromTo(splitPoint, BptConstant.PAGE_SIZE + 1, list, rightEntries);


            }
        } else {
            //根结点为索引结点
            root.insert(entry);
        }

        return false;
    }

    public void setRoot(Page<T, E> root) {
        this.root = root;
    }

    public Page<T, E> getRoot() {
        return root;
    }

    public static void main(String arg[]) {
        Bpt<String, Integer> bpt = new BptImpl<String, Integer>(new LeafPageImpl<String, Integer>(null,null));
        bpt.insert(new Entry<String, Integer>("1",1));
    }
}
