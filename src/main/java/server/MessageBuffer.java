package server;

import utils.ConfigUtils;

/**
 * 消息buffer
 * Created by rx on 2017/9/8.
 */
public class MessageBuffer {
    public final static int KB = 1024;
    public final static int MB = 1024 * KB;

    private static final int CAPACITY_SMALL;
    private static final int CAPACITY_MEDIUM;
    private static final int CAPACITY_LARGE;

    private static final int FREE_BLOCKS_SMALL;
    private static final int FREE_BLOCKS_MEDIUM;
    private static final int FREE_BLOCKS_LARGE;

    static {
        ConfigUtils.getConfig().loadPropertiesFromSrc();
        CAPACITY_SMALL = Integer.parseInt(ConfigUtils.getConfig().getCapacitySmall());
        CAPACITY_MEDIUM = Integer.parseInt(ConfigUtils.getConfig().getCapacityMedium());
        CAPACITY_LARGE = Integer.parseInt(ConfigUtils.getConfig().getCapacityLarge());

        FREE_BLOCKS_SMALL = Integer.parseInt(ConfigUtils.getConfig().getFreeBlockSmall());
        FREE_BLOCKS_MEDIUM = Integer.parseInt(ConfigUtils.getConfig().getFreeBlockMedium());
        FREE_BLOCKS_LARGE = Integer.parseInt(ConfigUtils.getConfig().getFreeBlockLarge());
    }

    public byte[] smallMessageBuffer  = new byte[FREE_BLOCKS_SMALL *   4 * KB]; // 1024 X 4KB
    public byte[] mediumMessageBuffer = new byte[FREE_BLOCKS_MEDIUM  * 128 * KB]; //  128 X 128KB
    public byte[] largeMessageBuffer  = new byte[FREE_BLOCKS_LARGE   *   1 * MB]; //   16 X 1MB

    QueueIntFlip smallMessageBufferFreeBlocks = new QueueIntFlip(FREE_BLOCKS_SMALL);
    QueueIntFlip mediumMessageBufferFreeBlocks = new QueueIntFlip(FREE_BLOCKS_MEDIUM);
    QueueIntFlip largeMessageBufferFreeBlocks = new QueueIntFlip(FREE_BLOCKS_LARGE);

    public MessageBuffer() {
        for (int i = 0; i < smallMessageBuffer.length; i += CAPACITY_SMALL) {
            this.smallMessageBufferFreeBlocks.put(i);
        }

        for (int i = 0; i < mediumMessageBuffer.length; i += CAPACITY_MEDIUM) {
            this.mediumMessageBufferFreeBlocks.put(i);
        }

        for (int i = 0; i < largeMessageBuffer.length; i += CAPACITY_LARGE) {
            this.largeMessageBufferFreeBlocks.put(i);
        }
    }

    public Message getMessage () {
        int nextFreeSmallBlock = this.smallMessageBufferFreeBlocks.take();

        if (nextFreeSmallBlock == -1) {
            return null;
        }

        Message message = new Message(this);

        message.setSharedArray(this.smallMessageBuffer);
        message.setCapacity(CAPACITY_SMALL);
        message.setOffset(nextFreeSmallBlock);
        message.setLength(0);

        return message;
    }

    /**
     * 消息resize
     * @param message
     * @return
     */
    public boolean expandMessage(Message message) {
        if (message.getCapacity() == CAPACITY_SMALL) {
            return moveMessage(message, this.smallMessageBufferFreeBlocks,
                    this.mediumMessageBufferFreeBlocks,
                    this.mediumMessageBuffer, CAPACITY_MEDIUM);
        } else if (message.getCapacity() == CAPACITY_MEDIUM) {
            return moveMessage(message,
                    this.mediumMessageBufferFreeBlocks,
                    this.largeMessageBufferFreeBlocks,
                    this.largeMessageBuffer, CAPACITY_LARGE);
        } else {
            return false;
        }
    }

    /**
     * 消息内容复制
     * @param message
     * @param srcBlockQueue
     * @param destBlockQueue
     * @param dest
     * @param newCapacity
     * @return
     */
    private boolean moveMessage(Message message, QueueIntFlip srcBlockQueue,
                                QueueIntFlip destBlockQueue, byte[] dest, int newCapacity) {
        int nextFreeBlock = destBlockQueue.take();
        if (nextFreeBlock == -1) {
            return false;
        }

        System.arraycopy(message.getSharedArray(), message.getOffset(),
                dest, nextFreeBlock, message.getLength());

        srcBlockQueue.put(message.getOffset());

        message.setSharedArray(dest);
        message.setOffset(nextFreeBlock);
        message.setCapacity(newCapacity);
        return true;
    }
}
