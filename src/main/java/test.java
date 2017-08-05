import BPT.BPT;
import BPT.BPTImpl.BPTImpl;
import BPT.Entry;
import BPT.Node;
import BPT.NodeImpl.InteriorNode;
import BPT.NodeImpl.LeafNode;
import Parser.Lexer;
import Parser.SQLDDLPattern;
import Parser.Token;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Created by roy on 2017/7/2.
 */
public class test {
    public static void main(String args[]) {
        Lexer lexer = new Lexer();
        try {
            List<Token> tokens = lexer.generateTokenStream();
            for (Token token : tokens) {
                System.out.println(token.getValue() + ":" + token.getLine());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
//        String[] key = {"a","b","c","d","e","f","g","h"};
//        String[] value = {"1","2","3","4","5","6","7","8"};
//        Entry<String,String> entry = new Entry<String, String>("hash","here");
//        Entry<String,String> entry1 = new Entry<String, String>("ds","dsa");
//        Vector<Entry<String, String>> entries = new Vector<Entry<String, String>>();
//        for (int i = 0; i < key.length;i++) {
//            entries.add(new Entry<String, String>(key[i], value[i]));
//        }
//        LeafNode<String,String> node = new LeafNode<String,String>(null);
//        node.setEntries(entries);
//        BPT<String,String> bpt = new BPTImpl<String, String>();
//        try {
////            Entry<String, String> entry2 = bpt.search(node,  "h5");
////            System.out.println(entry2.getValue());
//            bpt.update(node,"a",new Entry<String, String>("8","9"));
//            Entry<String, String> entry2 = bpt.search(node,"8");
//            //System.out.println(entry2.getValue());
//
//        } catch (Exception e) {
//
//        }
    }
}
