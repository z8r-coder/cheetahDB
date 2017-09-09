package server;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by rx on 2017/9/10.
 */
public class MessageReader implements IMessageReader {
    private MessageBuffer messageBuffer;

    private List<Message> completeMessages = new ArrayList<Message>();

    private Message nextMessage;

    public MessageReader(){

    }
    public void init(MessageBuffer readMessageBuffer) {
        this.messageBuffer = readMessageBuffer;
        this.nextMessage = messageBuffer.getMessage();
    }

    public void read(Socket socket, ByteBuffer byteBuffer) throws IOException {
        int bytesRead = socket.read(byteBuffer);
        byteBuffer.flip();

        if (byteBuffer.remaining() == 0) {
            byteBuffer.clear();
            return;
        }

        this.nextMessage.writeToMessage(byteBuffer);

        Message message = this.messageBuffer.getMessage();
        message.writeToMessage(nextMessage.getSharedArray());

        completeMessages.add(nextMessage);
        nextMessage = message;

        byteBuffer.clear();
    }

    public List<Message> getMessage() {
        return this.completeMessages;
    }
}
