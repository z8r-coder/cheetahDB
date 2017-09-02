package FileStore;

import java.io.File;
import java.io.IOException;

/**
 * Created by rx on 2017/9/2.
 */
public class DataSource {
    static {
        try {
            File file = File.createTempFile("test",".db");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
