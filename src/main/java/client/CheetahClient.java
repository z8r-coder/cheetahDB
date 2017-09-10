package client;

import com.sun.org.apache.bcel.internal.classfile.Code;
import filestore.code.CodeUtils;
import support.CharsetHelper;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * client
 * Created by rx on 2017/9/7.
 */
public class CheetahClient implements Runnable{
    private BlockingQueue<String> words;
    private Random random;

    public void run() {
        SocketChannel channel = null;
        Selector selector = null;

        try {
            channel = SocketChannel.open();
            selector = Selector.open();
            //请求连接
            channel.configureBlocking(false);
            channel.connect(new InetSocketAddress("localhost", 9999));
            selector = Selector.open();
            channel.register(selector, SelectionKey.OP_CONNECT);
            boolean isOver = false;

            while (!isOver) {
                selector.select();
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    iterator.remove();

                    if (key.isConnectable()) {
                        if (channel.isConnectionPending()) {
                            if (channel.finishConnect()) {
                                //成功连接
                                key.interestOps(SelectionKey.OP_READ);
                                ByteBuffer byteBuffer = ByteBuffer.allocate(128);
                                CodeUtils.encode(getWord(), byteBuffer);
                                channel.write(byteBuffer);
                                sleep();
                            } else {
                                key.cancel();
                            }
                        }
                    } else if (key.isReadable()) {
                        ByteBuffer byteBuffer = ByteBuffer.allocate(128);
                        channel.read(byteBuffer);
                        byteBuffer.flip();
                        CharBuffer charBuffer = CharsetHelper.decode(byteBuffer);
                        String answer = charBuffer.toString();
                        System.out.println(Thread.currentThread().getId() + "---" + answer);

                        String word = getWord();
                        if (word != null) {
                            channel.write(CharsetHelper.encode(CharBuffer.wrap(word)));
                        } else {
                            isOver = true;
                        }

                        sleep();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (channel != null) {
                try {
                    channel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (selector != null) {
                try {
                    selector.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void init() {
        words = new ArrayBlockingQueue<String>(5);
        try {
            words.put("SELECT * FROM TABLE_A;");
//            words.put("who");
//            words.put("what");
//            words.put("where");
//            words.put("bye");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        random = new Random();
    }

    private void sleep() {
        try {
            TimeUnit.SECONDS.sleep(random.nextInt(3));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void sleep(long l) {
        try {
            TimeUnit.SECONDS.sleep(l);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    private String getWord(){
        return words.poll();
    }

    public static void main(String arg[]) {
        CheetahClient client = new CheetahClient();
        client.init();
        new Thread(client).start();
    }
}
