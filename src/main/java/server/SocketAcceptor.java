package server;

import support.logging.Log;
import support.logging.LogFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Queue;

/**
 * 接受消息
 * Created by rx on 2017/9/9.
 */
public class SocketAcceptor implements Runnable{
    private int tcpPort;
    private ServerSocketChannel serverSocketChannel;

    private Queue socketQueue;

    private final static Log log = LogFactory.getLog(SocketAcceptor.class);

    private SocketAcceptor(int tcpPort, Queue socketQueue) {
        this.tcpPort = tcpPort;
        this.socketQueue = socketQueue;
    }

    public void run() {
        try {
            this.serverSocketChannel = ServerSocketChannel.open();
            this.serverSocketChannel.socket().bind(new InetSocketAddress(tcpPort));
        } catch (IOException e) {
            log.error(Thread.currentThread().toString() + "IO ERROR", e);
            return;
        }

        while (true) {
            try {
                SocketChannel socketChannel = this.serverSocketChannel.accept();
                log.info("Socket accepted:" + socketChannel);
                // TODO: 2017/9/9 check if queue can even accept more sockets
                this.socketQueue.add(new Socket(socketChannel));
            } catch (IOException e) {
                log.error(Thread.currentThread().toString() + "IO ERROR", e);
            }

        }
    }
}
