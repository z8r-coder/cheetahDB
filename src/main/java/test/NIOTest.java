package test;

import filestore.code.CodeUtils;
import models.Column;
import models.Table;
import utils.ConfigUtils;

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
//        ByteBuffer bb = ByteBuffer.allocate(100000);
//        List<Column> columns = new ArrayList<Column>(32);
//        for (int i = 0; i < 25; i++) {
//            columns.add(new Column("aa", "1" + i));
//        }
//
//        Table table = new Table("myTable",columns);
//
//        CodeUtils.encode(table, bb);
//
        ConfigUtils.getConfig().loadPropertiesFromSrc();
//
//        try {
//            RandomAccessFile afile = new RandomAccessFile(ConfigUtils.getConfig().getAbsolutePath() + "test.db","rw");
//            FileChannel inchannel = afile.getChannel();
//
//            bb.flip();
//            bb.limit(10000);
//            while (bb.hasRemaining()) {
//                //缓冲区是读模式，读缓冲区写入通道
//                inchannel.write(bb);
//            }
////            bb.clear();
////            bb.limit(2000);
////            //如bb.limit(2000)，只有1600的字节，不足的400用0补齐
////            while (bb.hasRemaining()) {
////                inchannel.write(bb);
////            }
//            //分段写入的时候不要将position + 1
////            bb.limit(bb.capacity());
////            while (bb.hasRemaining()) {
////                inchannel.write(bb);
////            }
//            inchannel.close();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        //CodeUtils.encode(table, bb);
        //bb.flip();
//        System.out.println(bb.getChar());
//        System.out.println(bb.get());
//        System.out.println(bb.get());
//        System.out.println(bb.get());
        ByteBuffer buf = ByteBuffer.allocate(2000);
//
        try {
            RandomAccessFile afile = new RandomAccessFile(ConfigUtils.getConfig().getAbsolutePath() + "test.db","rw");
            FileChannel inchannel = afile.getChannel();
            //设置起始位置,用ByteBuffer来限制读取大小
            inchannel.position(0);
            buf.limit(2000);

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
//
        Table cc = (Table) CodeUtils.decode(buf);
        for (Column column : cc.getColumns()) {
            System.out.println(column.getName());
        }
//        System.out.println(bb.limit());
//        bb.clear();//clear方法可将所有position，limit,capacity变成初始状态
//        System.out.println("capacity=" + bb.capacity());
//        System.out.println("limit=" + bb.limit());


    }
}
