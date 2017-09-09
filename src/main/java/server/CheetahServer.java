package server;

import utils.ConfigUtils;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * server
 * Created by rx on 2017/9/7.
 */
public class CheetahServer {

    private final static int SOCKET_SIZE;

    static {
        ConfigUtils.getConfig().loadPropertiesFromSrc();
        SOCKET_SIZE = Integer.parseInt(ConfigUtils.getConfig().getSocketSize());
    }
    private SocketAccepter socketAccepter;
    private SocketProcessor socketProcessor;

    private int tcpPort = 0;

    private IMessageReaderFactory messageReaderFactory;
    private IMessageProcessor messageProcessor;

    public CheetahServer(int tcpPort, IMessageReaderFactory messageReaderFactory,
                         IMessageProcessor messageProcessor) {
        this.tcpPort = tcpPort;
        this.messageReaderFactory = messageReaderFactory;
        this.messageProcessor = messageProcessor;
    }

    public void start() throws IOException {
        Queue socketQueue = new ArrayBlockingQueue(1024);

        this.socketAccepter = new SocketAccepter(tcpPort, socketQueue);

        MessageBuffer readBuffer = new MessageBuffer();
        MessageBuffer writeBuffer = new MessageBuffer();

        this.socketProcessor = new SocketProcessor(socketQueue, readBuffer,writeBuffer,
                this.messageReaderFactory, this.messageProcessor);

        Thread accepterThread = new Thread(this.socketAccepter);
        Thread processorThread = new Thread(this.socketProcessor);

        accepterThread.start();
        processorThread.start();
    }
}
