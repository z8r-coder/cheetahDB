package server;

import support.logging.Log;
import support.logging.LogFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.*;

/**
 * Created by rx on 2017/9/9.
 */
public class SocketProcessor implements Runnable {
    private Queue<Socket> inBoundSocketQueue;

    private MessageBuffer readMessageBuffer;
    private MessageBuffer writeMessageBuffer;

    private IMessageReaderFactory messageReaderFactory;

    private Queue<Message> outBoundMessageQueue = new LinkedList<Message>();

    private Map<Long, Socket> socketMap = new HashMap<Long, Socket>();

    private ByteBuffer  readByteBuffer = ByteBuffer.allocate(1024 * 1024);
    private ByteBuffer writeByteBuffer = ByteBuffer.allocate(1024 * 1024);
    private Selector readSelector;
    private Selector writeSelector;

    private IMessageProcessor messageProcessor;
    private WriteProxy writeProxy;

    private long nextSocketId = 16 * 1024;

    private Set<Socket> emptyToNonEmptySockets = new HashSet<Socket>();
    private Set<Socket> nonEmptyToEmptySockets = new HashSet<Socket>();

    private Log log = LogFactory.getLog(SocketProcessor.class);

    public SocketProcessor(Queue<Socket> inBoundSocketQueue,
                           MessageBuffer readMessageBuffer,
                           MessageBuffer writeMessageBuffer,
                           IMessageReaderFactory messageReaderFactory,
                           IMessageProcessor messageProcessor) throws IOException {
        this.inBoundSocketQueue = inBoundSocketQueue;

        this.readMessageBuffer = readMessageBuffer;
        this.writeMessageBuffer = writeMessageBuffer;

        this.writeProxy = new WriteProxy(writeMessageBuffer, this.outBoundMessageQueue);

        this.messageReaderFactory = messageReaderFactory;

        this.messageProcessor = messageProcessor;

        this.readSelector = Selector.open();
        this.writeSelector = Selector.open();
    }

    public void run() {

    }

    public void executeCycle() {

    }

    public void takeNewSockets() throws IOException {
        Socket newSocket = this.inBoundSocketQueue.poll();

        while (newSocket != null) {
            newSocket.setSocketId(nextSocketId++);
            newSocket.getSocketChannel().configureBlocking(false);

            newSocket.setMessageReader(this.messageReaderFactory.createMessageReader());
            newSocket.getMessageReader().init(this.readMessageBuffer);

            newSocket.setMessageWriter(new MessageWriter());
            this.socketMap.put(newSocket.getSocketId(), newSocket);

            SelectionKey key = newSocket.getSocketChannel().register(this.readSelector, SelectionKey.OP_READ);
            key.attach(newSocket);

            newSocket = this.inBoundSocketQueue.poll();
        }
    }

    public void readFromSockets() throws IOException {
        int readReady = this.readSelector.selectNow();

        if (readReady > 0) {
            Set<SelectionKey> selectionKeys = this.readSelector.selectedKeys();
            Iterator<SelectionKey> keyIterator = selectionKeys.iterator();

            while (keyIterator.hasNext()) {
                SelectionKey key = keyIterator.next();

                readFromSockets(key);

                keyIterator.remove();
            }
            selectionKeys.clear();
        }
    }

    public void readFromSockets(SelectionKey key) throws IOException {
        Socket socket = (Socket) key.attachment();
        socket.getMessageReader().read(socket, this.readByteBuffer);

        List<Message> fullMessages = socket.getMessageReader().getMessage();
        if (fullMessages.size() > 0) {
            for (Message message : fullMessages) {
                message.setSocketId(socket.getSocketId());
                this.messageProcessor.process(message, this.writeProxy);
            }
            fullMessages.clear();
        }

        if (socket.endOfStreamReached) {
            log.info("Socket closed: " + socket.getSocketId());
            this.socketMap.remove(socket.getSocketId());
            key.attach(null);
            key.cancel();
            key.channel().close();
        }
    }

    public void writeToSockets() throws IOException {

        // 取出所有新的消息
        takeNewOutboundMessages();

        //关掉所有没有数据可读的socket
        cancelEmptySockets();

        // 注册所有存在数据却还未注册的socket
        registerNonEmptySockets();

        // Select from the Selector.
        int writeReady = this.writeSelector.selectNow();

        if(writeReady > 0){
            Set<SelectionKey>      selectionKeys = this.writeSelector.selectedKeys();
            Iterator<SelectionKey> keyIterator   = selectionKeys.iterator();

            while(keyIterator.hasNext()){
                SelectionKey key = keyIterator.next();

                Socket socket = (Socket) key.attachment();

                socket.getMessageWriter().write(socket, this.writeByteBuffer);

                if(socket.getMessageWriter().isEmpty()){
                    this.nonEmptyToEmptySockets.add(socket);
                }

                keyIterator.remove();
            }

            selectionKeys.clear();

        }
    }

    private void registerNonEmptySockets() throws ClosedChannelException {
        for(Socket socket : emptyToNonEmptySockets){
            socket.getSocketChannel().register(this.writeSelector, SelectionKey.OP_WRITE, socket);
        }
        emptyToNonEmptySockets.clear();
    }

    private void cancelEmptySockets() {
        for(Socket socket : nonEmptyToEmptySockets){
            SelectionKey key = socket.getSocketChannel().keyFor(this.writeSelector);

            key.cancel();
        }
        nonEmptyToEmptySockets.clear();
    }

    private void takeNewOutboundMessages() {
        Message outMessage = this.outBoundMessageQueue.poll();
        while(outMessage != null){
            Socket socket = this.socketMap.get(outMessage.getSocketId());

            if(socket != null){
                MessageWriter messageWriter = socket.getMessageWriter();
                if(messageWriter.isEmpty()){
                    messageWriter.enqueue(outMessage);
                    nonEmptyToEmptySockets.remove(socket);
                    emptyToNonEmptySockets.add(socket);    //not necessary if removed from nonEmptyToEmptySockets in prev. statement.
                } else{
                    messageWriter.enqueue(outMessage);
                }
            }

            outMessage = this.outBoundMessageQueue.poll();
        }
    }
}
