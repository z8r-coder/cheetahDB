package server;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;

/**
 * 读取消息器
 * Created by rx on 2017/9/8.
 */
public interface IMessageReader {
    /**
     * 初始化缓冲区
     * @param readMessageBuffer
     */
    public void init(MessageBuffer readMessageBuffer);

    /**
     * 读消息
     * @param socket
     * @param byteBuffer
     * @throws IOException
     */
    public void read(Socket socket, ByteBuffer byteBuffer) throws IOException;

    /**
     * 获取消息体
     * @return
     */
    public List<Message> getMessage();
}
