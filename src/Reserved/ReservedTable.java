package Reserved;

import Tokens.TokenType;

import java.util.HashMap;

public class ReservedTable {
    private final HashMap<String, TokenType> strTokenTypeMap = new HashMap<>();
    private final HashMap<TokenType, ReservedInfo> reservedMap = new HashMap<>();
    private final static ReservedTable INSTANCE = new ReservedTable();
    private static boolean init = false;

    private ReservedTable() {
    }

    /**
     * Initializes the only instance of ReservedTable if it has not been initialized and then returns it.
     *
     * @return a ReservedTable object.
     */
    public static ReservedTable getInstance() {
        if (!init) {
            // Initialize the reserved table
            INSTANCE.register("var", new KeywordInfo(TokenType.MUTABLE_DECL));
            INSTANCE.register("let", new KeywordInfo(TokenType.IMMUTABLE_DECL));
            INSTANCE.register("true", new KeywordInfo(TokenType.BOOL_LITERAL));
            INSTANCE.register("false", new KeywordInfo(TokenType.BOOL_LITERAL));
            INSTANCE.register("int", new TypeInfo(TokenType.INT_TYPE_ID, 4));
            INSTANCE.register("float", new TypeInfo(TokenType.FLOAT_TYPE_ID, 4));
            INSTANCE.register("bool", new TypeInfo(TokenType.BOOL_TYPE_ID, 1));
            INSTANCE.register("+", new OperatorInfo(TokenType.ADD));
            INSTANCE.register("-", new OperatorInfo(TokenType.SUB));
            INSTANCE.register("*", new OperatorInfo(TokenType.MULT));
            INSTANCE.register("/", new OperatorInfo(TokenType.DIV));
            INSTANCE.register("%", new OperatorInfo(TokenType.MOD));
            INSTANCE.register(".", new OperatorInfo(TokenType.DOT));
            INSTANCE.register(":", new OperatorInfo(TokenType.COLON));
            INSTANCE.register("=", new OperatorInfo(TokenType.ASSIGNMENT));
            INSTANCE.register("(", new OperatorInfo(TokenType.LPAREN));
            INSTANCE.register(")", new OperatorInfo(TokenType.RPAREN));
            INSTANCE.register(";", new OperatorInfo(TokenType.SEMICOLON));

            init = true;
        }
        return INSTANCE;
    }

    /**
     * Maps a string to a token type and a token type to a ReservedInfo object.
     *
     * @param strKey       the string to be mapped to a token type.
     * @param reservedInfo the ReservedInfo object to be mapped from a token type.
     */
    private void register(String strKey, ReservedInfo reservedInfo) {
        TokenType reservedId = reservedInfo.getId();
        strTokenTypeMap.put(strKey, reservedId);
        reservedMap.put(reservedId, reservedInfo);
    }

    /**
     * Gets the token type associated with the given string.
     *
     * @param strKey the input string.
     * @return a token type if there is one associated with the given string, otherwise, return null.
     */
    public TokenType getTokenType(String strKey) {
        return strTokenTypeMap.get(strKey);
    }

    /**
     * Gets a ReservedInfo object based on the identifier.
     *
     * @param reservedId the ReservedInfo object identifier.
     * @return a ReservedInfo object if one exists in the table and null otherwise.
     */
    public ReservedInfo getReservedInfo(TokenType reservedId) {
        return reservedMap.get(reservedId);
    }
}
