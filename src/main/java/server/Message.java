package server;

import java.nio.ByteBuffer;

/**
 * 消息bean,消息操作
 * Created by rx on 2017/9/9.
 */
public class Message {
    private MessageBuffer messageBuffer;

    private long socketId = 0;

    private byte[] sharedArray;
    private int   offset   = 0;
    private int capacity   = 0;
    private int   length   = 0;

    // TODO: 2017/9/10 将来可能实现cheetah协议
    private Object metaData;

    public Message(MessageBuffer messageBuffer) {
        this.messageBuffer = messageBuffer;
    }

    public int writeToMessage(ByteBuffer byteBuffer) {
        int remaining = byteBuffer.remaining();

        while (this.length + remaining > capacity) {
            if (!this.messageBuffer.expandMessage(this)) {
                return -1;
            }
        }

        int bytesToCopy = Math.min(remaining, this.capacity - this.length);
        byteBuffer.get(this.sharedArray, this.offset + this.length, bytesToCopy);
        this.length += bytesToCopy;

        return bytesToCopy;
    }

    public int writeToMessage(byte[] byteArray) {
        return writeToMessage(byteArray, 0, byteArray.length);
    }

    public int writeToMessage(byte[] byteArray, int offset, int length) {
        int remaining = length;

        while (this.length + remaining > capacity) {
            if (!this.messageBuffer.expandMessage(this)) {
                return -1;
            }
        }

        int bytesToCopy = Math.min(remaining, this.capacity - this.length);
        System.arraycopy(byteArray, offset, this.sharedArray,
                this.offset + this.length, bytesToCopy);
        this.length += bytesToCopy;
        return bytesToCopy;
    }

    public void writePartialMessageToMessage(Message message, int endIndex) {
        int startIndexOfPartialMessage = message.offset + endIndex;
        int lengthOfPartialMessage = (message.offset + message.length) - endIndex;

        System.arraycopy(message.sharedArray, startIndexOfPartialMessage,
                this.sharedArray, this.offset, lengthOfPartialMessage);
    }
    public long getSocketId() {
        return socketId;
    }

    public void setSocketId(long socketId) {
        this.socketId = socketId;
    }

    public byte[] getSharedArray() {
        return sharedArray;
    }

    public void setSharedArray(byte[] sharedArray) {
        this.sharedArray = sharedArray;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public Object getMetaData() {
        return metaData;
    }

    public void setMetaData(Object metaData) {
        this.metaData = metaData;
    }
}
