package test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

/**
 * 客户端测试
 * Created by rx on 2017/9/8.
 */
public class ClientTest {
    public static void main(String arg[]) {
        try {
            SocketChannel socketChannel = SocketChannel.open();
            socketChannel.connect(new InetSocketAddress("http://jenkov.com", 80));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
