package server;

/**
 * 消息buffer
 * Created by rx on 2017/9/8.
 */
public class MessageBuffer {
    public final static int KB = 1024;
    public final static int MB = 1024 * KB;

    private static final int CAPACITY_SMALL = 4 * KB;
    private static final int CAPACITY_MEDIUM = 128 * KB;
    private static final int CAPACITY_LARGE = 1024 * KB;

    public byte[] smallMessageBuffer  = new byte[1024 *   4 * KB]; // 1024 X 4KB
    public byte[] mediumMessageBuffer = new byte[128  * 128 * KB]; //  128 X 128KB
    public byte[] largeMessageBuffer  = new byte[16   *   1 * MB]; //   16 X 1MB

}
