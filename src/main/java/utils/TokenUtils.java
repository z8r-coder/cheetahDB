package utils;

import parser.Token;

/**
 * Created by rx on 2017/8/26.
 * token工具类
 */
public class TokenUtils {
    /**
     * 判断token是否相等
     * @param tokenA
     * @param tokenB
     * @return
     */
    public static boolean equals(Token tokenA, Token tokenB) {
        if (tokenA == null) {
            return tokenB == null;
        }

        if (tokenA.getLine() != tokenB.getLine()) {
            return false;
        }

        if (tokenA.getSortCode() == null) {
            return tokenB.getSortCode() == null;
        }

        if (tokenA.getSortCode() != tokenB.getSortCode()) {
            return false;
        }

        if (!StringUtils.equals(tokenA.getValue(),tokenB.getValue())) {
            return false;
        }
        return true;
    }

    /**
     * 判断token是否为空
     * @param token
     * @return
     */
    public static boolean isEmpty(Token token) {
        if (token == null) {
            return true;
        }
        return false;
    }
}
