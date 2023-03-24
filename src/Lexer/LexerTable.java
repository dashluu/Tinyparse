package Lexer;

import Global.Config;
import Global.TokenType;
import Symbols.SymbolInfo;
import Symbols.SymbolTable;

import java.util.HashMap;

public class LexerTable {
    private final HashMap<String, SymbolInfo> strToSymbolMap = new HashMap<>();
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
            SymbolTable symbolTable = SymbolTable.getInstance();
            // Initialize the lexer table
            lexerTable.set(Config.MUTABLE_ID_DECL, symbolTable.getKeyword(TokenType.MUTABLE_ID_DECL.name()));
            lexerTable.set(Config.IMMUTABLE_ID_DECL, symbolTable.getKeyword(TokenType.IMMUTABLE_ID_DECL.name()));
            lexerTable.set(Config.BOOL_TRUE, symbolTable.getKeyword(TokenType.BOOL_TRUE.name()));
            lexerTable.set(Config.BOOL_FALSE, symbolTable.getKeyword(TokenType.BOOL_FALSE.name()));
            lexerTable.set(Config.INT_TYPE_ID, symbolTable.getType(TokenType.INT_TYPE_ID.name()));
            lexerTable.set(Config.FLOAT_TYPE_ID, symbolTable.getType(TokenType.FLOAT_TYPE_ID.name()));
            lexerTable.set(Config.BOOL_TYPE_ID, symbolTable.getType(TokenType.BOOL_TYPE_ID.name()));
            lexerTable.set(Config.ADD, symbolTable.getOperator(TokenType.ADD.name()));
            lexerTable.set(Config.SUB, symbolTable.getOperator(TokenType.SUB.name()));
            lexerTable.set(Config.MULT, symbolTable.getOperator(TokenType.MULT.name()));
            lexerTable.set(Config.DIV, symbolTable.getOperator(TokenType.DIV.name()));
            lexerTable.set(Config.MOD, symbolTable.getOperator(TokenType.MOD.name()));
            lexerTable.set(Config.POW, symbolTable.getOperator(TokenType.POW.name()));
            lexerTable.set(Config.BITWISE_NOT, symbolTable.getOperator(TokenType.BITWISE_NOT.name()));
            lexerTable.set(Config.BITWISE_AND, symbolTable.getOperator(TokenType.BITWISE_AND.name()));
            lexerTable.set(Config.BITWISE_OR, symbolTable.getOperator(TokenType.BITWISE_OR.name()));
            lexerTable.set(Config.LOGICAL_NOT, symbolTable.getOperator(TokenType.LOGICAL_NOT.name()));
            lexerTable.set(Config.LOGICAL_AND, symbolTable.getOperator(TokenType.LOGICAL_AND.name()));
            lexerTable.set(Config.LOGICAL_OR, symbolTable.getOperator(TokenType.LOGICAL_OR.name()));
            lexerTable.set(Config.DOT, symbolTable.getOperator(TokenType.DOT.name()));
            lexerTable.set(Config.COLON, symbolTable.getOperator(TokenType.COLON.name()));
            lexerTable.set(Config.LPAREN, symbolTable.getOperator(TokenType.LPAREN.name()));
            lexerTable.set(Config.RPAREN, symbolTable.getOperator(TokenType.RPAREN.name()));
            lexerTable.set(Config.SEMICOLON, symbolTable.getOperator(TokenType.SEMICOLON.name()));
            lexerTable.set(Config.ASSIGNMENT, symbolTable.getOperator(TokenType.ASSIGNMENT.name()));
            lexerTable.set(Config.EQ, symbolTable.getOperator(TokenType.EQ.name()));

            init = true;
        }
        return lexerTable;
    }

    /**
     * Gets the symbol in the table associated with the given string.
     *
     * @param key the input string as the key.
     * @return the symbol associated with the given key.
     */
    public SymbolInfo get(String key) {
        return strToSymbolMap.get(key);
    }

    /**
     * Inserts a new key-value pair into the table if it does not exist, otherwise, replace the old symbol
     * with the new one.
     *
     * @param key    the string as a key.
     * @param symbol the symbol to be set.
     * @return the old symbol if one exists and null otherwise.
     */
    private SymbolInfo set(String key, SymbolInfo symbol) {
        return strToSymbolMap.put(key, symbol);
    }
}
