
package parser;

import support.logging.Log;
import support.logging.LogFactory;
import utils.ASTTestUtils;

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

    private final static Log LOG = LogFactory.getLog(ASTTestUtils.class);

    public IOSystem() {
    }

    public String readFromFile(File file) throws IOException {
        StringBuilder sb = new StringBuilder();
        InputStreamReader e = null;
        BufferedReader br = null;
        try {
            e = new InputStreamReader(new FileInputStream(file), "UTF-8");
            br = new BufferedReader(e);
            String lineText = null;

            while((lineText = br.readLine()) != null) {
                sb.append(lineText + '\n');
            }
        } catch (Exception ex) {
            LOG.error("readFromFile failed", ex);
        } finally {
            if (br != null) {
                br.close();
            }
            if (e != null) {
                e.close();
            }
        }

        return sb.toString();
    }

    public void writeToFile(File file, List<Token> iobuf) throws IOException {
        OutputStreamWriter ow = null;
        BufferedWriter bw = null;
        try {
            ow = new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
            bw = new BufferedWriter(ow);
            for(int i = 0; i < iobuf.size(); ++i) {
                bw.write("<" + iobuf.get(i).getValue() + "," + iobuf.get(i).getLine());
            }
        } catch (IOException e) {
            LOG.error("writeToFile failed", e);
        } finally {
            if (ow != null) {
                ow.close();
            }
            if (bw != null) {
                bw.close();
            }
        }
    }
}
