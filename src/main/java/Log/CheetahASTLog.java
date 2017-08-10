package Log;

import Parser.Token;

import java.util.List;

/**
 * Created by ruanxin on 2017/8/9.
 */
public class CheetahASTLog implements CheetahLog{
    private Class className;

    private CheetahASTLog (Class className) {
        this.className = className;
    }

    public final static void Info(String desc) {
        System.out.println(desc);
    }
    public final static void Info(String desc,String str) {
        System.out.println(desc + ":[" + str + "]");
    }

    public final static void Info(String desc, Object object) {
        System.out.println(desc + ":[" + object.toString() + "]" );
    }
    public final static void  Info(String desc, List<Token> tokens) {
        System.out.print(desc + ":[");
        for (Token token : tokens) {
            System.out.print(token.getValue() + ",");
        }
        System.out.println("]");
    }
    public static void Info(String desc, List<Token> tokens, char splitSymbol) {
        System.out.print(desc + ":[");
        for (Token token : tokens) {
            System.out.print(token.getValue() + splitSymbol);
        }
        System.out.println("]");
    }

}
