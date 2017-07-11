package Parser;

import java.util.Map;

/**
 * Created by roy on 2017/7/9.
 */
public class Keywords {
    private final Map<String, Token> keywords;

    public boolean containsTheValue(Token token) {
        return this.keywords.containsKey(token);
    }
    public Keywords(Map<String, Token> keywords) {
        this.keywords = keywords;
    }
    public Token getValue(String key) {
        key = key.toUpperCase();
        return keywords.get(key);
    }

    public Map<String, Token> getKeywords() {
        return keywords;
    }
}
