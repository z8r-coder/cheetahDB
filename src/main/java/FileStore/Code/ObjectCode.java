package FileStore.Code;

import java.nio.ByteBuffer;

/**
 * Created by rx on 2017/9/2.
 */
public class ObjectCode implements StoreCode<Object> {

    public void encode(ByteBuffer buffer, Object value) {

    }

    public Object decode(ByteBuffer buffer) {
        return null;
    }
}
