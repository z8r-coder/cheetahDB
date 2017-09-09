package server;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by rx on 2017/9/9.
 */
public class MessageWriter {
    private List<Message> writeQueue = new ArrayList<Message>();
    private Message messageInProgress;
    private int bytesWritten = 0;

    public MessageWriter() {

    }


    public void enqueue(Message message) {
        if (this.messageInProgress == null) {
            this.messageInProgress = message;
        } else {
            this.writeQueue.add(message);
        }
    }

    public void write(Socket socket, ByteBuffer byteBuffer) throws IOException {
        byteBuffer.put(this.messageInProgress.getSharedArray(),
                this.messageInProgress.getOffset() + this.bytesWritten,
                this.messageInProgress.getLength() - this.bytesWritten);
        byteBuffer.flip();

        this.bytesWritten += socket.write(byteBuffer);
        byteBuffer.clear();

        if (this.bytesWritten >= this.messageInProgress.getLength()) {
            if (this.writeQueue.size() > 0) {
                this.messageInProgress = this.writeQueue.remove(0);
            } else {
                this.messageInProgress = null;
                // TODO: 2017/9/9 unregister from selector
            }
        }
    }

    public boolean isEmpty() {
        return this.writeQueue.isEmpty() && this.messageInProgress == null;
    }
}
