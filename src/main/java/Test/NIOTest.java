package Test;

import Engine.Bplustree;
import FileStore.Code.CodeUtils;
import Models.Column;
import Models.Row;
import Models.Table;
import Models.Value;
import Utils.ConfigUtils;

import java.awt.datatransfer.StringSelection;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

/**
 * NIO buffer控制测试
 * buffer默认是写模式
 * Created by rx on 2017/9/3.
 */
public class NIOTest {
    public static void main(String args[]) {
        ByteBuffer bb = ByteBuffer.allocate(100000);
        List<Column> columns = new ArrayList<Column>(32);
        for (int i = 0; i < 25; i++) {
            columns.add(new Column("aa", "1" + i));
        }

        Table table = new Table("myTable",columns);

        CodeUtils.encode(table, bb);

        ConfigUtils.getConfig().loadPropertiesFromSrc();

        try {
            RandomAccessFile afile = new RandomAccessFile(ConfigUtils.getConfig().getAbsolutePath() + "test.db","rw");
            FileChannel inchannel = afile.getChannel();

            bb.flip();
            inchannel.position(30);
            //无法控制写区间
//            while (bb.hasRemaining()) {
                //缓冲区是读模式，读缓冲区写入通道
                int a = inchannel.write(bb);
                System.out.println(afile.length());
//            }
            inchannel.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //CodeUtils.encode(table, bb);
        //bb.flip();
//        System.out.println(bb.getChar());
//        System.out.println(bb.get());
//        System.out.println(bb.get());
//        System.out.println(bb.get());
        ByteBuffer buf = ByteBuffer.allocate(20000);

        try {
            RandomAccessFile afile = new RandomAccessFile(ConfigUtils.getConfig().getAbsolutePath() + "test.db","rw");
            FileChannel inchannel = afile.getChannel();
            //设置启示位置
            inchannel.position(30);
            //截断
            //inchannel.truncate(300);
            //设置以上值，代表，从磁盘文件30-300读info写入缓冲区
            //从通道写入缓冲区，写模式
            int bytesRead = inchannel.read(buf);
            System.out.println(inchannel.position());
            //若不存在会返回-1
            System.out.println(bytesRead);
            inchannel.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

//        Table cc = (Table) CodeUtils.decode(buf);
//        for (Column column : cc.getColumns()) {
//            System.out.println(column.getName());
//        }
//        System.out.println(bb.limit());
//        bb.clear();//clear方法可将所有position，limit,capacity变成初始状态
//        System.out.println("capacity=" + bb.capacity());
//        System.out.println("limit=" + bb.limit());


    }
}
