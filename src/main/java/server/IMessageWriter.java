package server;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Created by rx on 2017/9/9.
 */
public interface IMessageWriter {

    /**
     * 消息入队
     * @param message
     */
    public void enqueue(Message message);

    /**
     * 从buffer中写入
     * @param socket
     * @param byteBuffer
     * @throws IOException
     */
    public void write(Socket socket, ByteBuffer byteBuffer) throws IOException;

    /**
     * 消息队列是否为空
     * @return
     */
    public boolean isEmpty();
}
