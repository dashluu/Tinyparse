package Keywords;

import Tokens.TokenType;

import java.util.HashMap;

public class KeywordTable {
    private final HashMap<String, TokenType> kwMap = new HashMap<>();
    private final static KeywordTable INSTANCE = new KeywordTable();
    private static boolean init = false;

    private KeywordTable() {
    }

    /**
     * Initializes the only instance of KeywordTable if it has not been initialized and then returns it.
     *
     * @return a KeywordTable object.
     */
    public static KeywordTable getInstance() {
        if (!init) {
            // Add keywords to table
            INSTANCE.kwMap.put("var", TokenType.VAR_DECL);
            INSTANCE.kwMap.put("let", TokenType.CONST_DECL);
            INSTANCE.kwMap.put("true", TokenType.BOOL_LITERAL);
            INSTANCE.kwMap.put("false", TokenType.BOOL_LITERAL);

            init = true;
        }
        return INSTANCE;
    }

    /**
     * Gets the keyword's id associated with the given string.
     *
     * @param kwStr a string associated with a keyword.
     * @return a TokenType object as the keyword's id if it exists, otherwise, return null.
     */
    public TokenType getId(String kwStr) {
        return kwMap.get(kwStr);
    }
}
