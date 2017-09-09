package server;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * Created by rx on 2017/9/9.
 */
public class Socket {
    private long socketId;

    private SocketChannel  socketChannel;
    private IMessageReader messageReader;
    private MessageWriter messageWriter;

    public boolean endOfStreamReached = false;

    public Socket() {
    }

    public Socket (SocketChannel socketChannel) {
        this.socketChannel = socketChannel;
    }

    /**
     * 从通道中读
     * @param byteBuffer
     * @return
     * @throws IOException
     */
    public int read(ByteBuffer byteBuffer) throws IOException {
        int bytesRead = this.socketChannel.read(byteBuffer);
        int totalBytesRead = bytesRead;

        while (bytesRead > 0) {
            bytesRead = this.socketChannel.read(byteBuffer);
            totalBytesRead += bytesRead;
        }
        if (bytesRead == -1) {
            this.endOfStreamReached = true;
        }

        return totalBytesRead;
    }

    /**
     * 写入通道
     * @param byteBuffer
     * @return
     * @throws IOException
     */
    public int write(ByteBuffer byteBuffer) throws IOException {
        int bytesWritten = this.socketChannel.write(byteBuffer);
        int totalBytesWritten = bytesWritten;

        while (bytesWritten > 0 && byteBuffer.hasRemaining()) {
            bytesWritten = this.socketChannel.write(byteBuffer);
            totalBytesWritten += bytesWritten;
        }

        return totalBytesWritten;
    }

    public long getSocketId() {
        return socketId;
    }

    public void setSocketId(long socketId) {
        this.socketId = socketId;
    }

    public SocketChannel getSocketChannel() {
        return socketChannel;
    }

    public void setSocketChannel(SocketChannel socketChannel) {
        this.socketChannel = socketChannel;
    }

    public IMessageReader getMessageReader() {
        return messageReader;
    }

    public void setMessageReader(IMessageReader messageReader) {
        this.messageReader = messageReader;
    }

    public MessageWriter getMessageWriter() {
        return messageWriter;
    }

    public void setMessageWriter(MessageWriter messageWriter) {
        this.messageWriter = messageWriter;
    }
}
