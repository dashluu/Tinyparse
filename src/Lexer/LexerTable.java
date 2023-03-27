package Lexer;

import Global.TokenType;
import Reserved.ReservedInfo;
import Reserved.ReservedTable;

import java.util.HashMap;

public class LexerTable {
    private final HashMap<String, ReservedInfo> strToReservedMap = new HashMap<>();
    private static final LexerTable lexerTable = new LexerTable();
    private static boolean init = false;

    private LexerTable() {
    }

    /**
     * Initializes the only instance of the lexer table if it has not been initialized and then returns it.
     *
     * @return a LexerTable object.
     */
    public static LexerTable getInstance() {
        if (!init) {
            ReservedTable reservedTable = ReservedTable.getInstance();
            // Initialize the lexer table
            lexerTable.set("var", reservedTable.getKeyword(TokenType.MUTABLE_ID_DECL));
            lexerTable.set("const", reservedTable.getKeyword(TokenType.IMMUTABLE_ID_DECL));
            lexerTable.set("true", reservedTable.getKeyword(TokenType.BOOL_TRUE));
            lexerTable.set("false", reservedTable.getKeyword(TokenType.BOOL_FALSE));
            lexerTable.set("Int", reservedTable.getType(TokenType.INT_TYPE_ID));
            lexerTable.set("Float", reservedTable.getType(TokenType.FLOAT_TYPE_ID));
            lexerTable.set("Bool", reservedTable.getType(TokenType.BOOL_TYPE_ID));
            lexerTable.set("+", reservedTable.getOperator(TokenType.ADD));
            lexerTable.set("-", reservedTable.getOperator(TokenType.SUB));
            lexerTable.set("*", reservedTable.getOperator(TokenType.MULT));
            lexerTable.set("/", reservedTable.getOperator(TokenType.DIV));
            lexerTable.set("%", reservedTable.getOperator(TokenType.MOD));
            lexerTable.set("**", reservedTable.getOperator(TokenType.POW));
            lexerTable.set(".", reservedTable.getOperator(TokenType.DOT));
            lexerTable.set(":", reservedTable.getOperator(TokenType.COLON));
            lexerTable.set("(", reservedTable.getOperator(TokenType.LPAREN));
            lexerTable.set(")", reservedTable.getOperator(TokenType.RPAREN));
            lexerTable.set("=", reservedTable.getOperator(TokenType.ASSIGNMENT));
            lexerTable.set(";", reservedTable.getOperator(TokenType.SEMICOLON));

            init = true;
        }
        return lexerTable;
    }

    /**
     * Gets the ReservedInfo object in the table associated with the given string.
     *
     * @param key the input string as the key.
     * @return the ReservedInfo object associated with the given key.
     */
    public ReservedInfo get(String key) {
        return strToReservedMap.get(key);
    }

    /**
     * Inserts a new key-value pair into the table if it does not exist, otherwise, replaces the old ReservedInfo object
     * with the new one.
     *
     * @param key          the string as a key.
     * @param reservedInfo the ReservedInfo object to be set.
     */
    private void set(String key, ReservedInfo reservedInfo) {
        strToReservedMap.put(key, reservedInfo);
    }
}
