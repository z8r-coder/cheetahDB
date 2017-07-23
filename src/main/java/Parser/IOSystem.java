
package Parser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

public class IOSystem {
    private List<String> linebuf = new ArrayList();
    private static final String ENCODING = "UTF-8";

    public IOSystem() {
    }

    public String readFromFile(File file) {
        StringBuilder sb = new StringBuilder();
        try {
            InputStreamReader e = new InputStreamReader(new FileInputStream(file), "UTF-8");
            BufferedReader br = new BufferedReader(e);
            String lineText = null;

            while((lineText = br.readLine()) != null) {
                sb.append(lineText + '\n');
            }
            br.close();
            e.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return sb.toString();
    }

    public void writeToFile(File file, List<Token> iobuf) {
        try {
            OutputStreamWriter e1 = new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
            BufferedWriter bw = new BufferedWriter(e1);
            for(int i = 0; i < iobuf.size(); ++i) {
                bw.write("<" + iobuf.get(i).getValue() + "," + iobuf.get(i).getLine());
            }
            bw.close();
            e1.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
