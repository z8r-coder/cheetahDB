package server;

import java.util.Queue;

/**
 * Created by rx on 2017/9/9.
 */
public class WriteProxy {

    private MessageBuffer messageBuffer;
    private Queue writeQueue;

    public WriteProxy(MessageBuffer messageBuffer, Queue writeQueue) {
        this.messageBuffer = messageBuffer;
        this.writeQueue = writeQueue;
    }

    public Message getMessage() {
        return this.messageBuffer.getMessage();
    }

    public boolean enqueue(Message message) {
        return writeQueue.offer(message);
    }
}
