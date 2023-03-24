package Symbols;

import Global.TokenType;
import Global.Config;
import Global.Block;

import java.util.HashMap;

public class SymbolTable {
    private final HashMap<SymbolInfo, SymbolInfo> symbols = new HashMap<>();
    private static final SymbolTable symbolTable = new SymbolTable();
    private static boolean init = false;

    private SymbolTable() {
    }

    /**
     * Initializes the only instance of the symbol table if it has not been initialized and then returns it.
     *
     * @return a SymbolTable object.
     */
    public static SymbolTable getInstance() {
        if (!init) {
            // Initialize the symbol table
            symbolTable.set(new KeywordInfo(TokenType.MUTABLE_ID_DECL));
            symbolTable.set(new KeywordInfo(TokenType.IMMUTABLE_ID_DECL));
            symbolTable.set(new KeywordInfo(TokenType.BOOL_LITERAL));
            symbolTable.set(new KeywordInfo(TokenType.BOOL_LITERAL));
            symbolTable.set(new TypeInfo(TokenType.INT_TYPE_ID, 4));
            symbolTable.set(new TypeInfo(TokenType.FLOAT_TYPE_ID, 4));
            symbolTable.set(new TypeInfo(TokenType.BOOL_TYPE_ID, 1));
            symbolTable.set(new OperatorInfo(TokenType.ADD, 0, true, OperatorType.BINARY));
            symbolTable.set(new OperatorInfo(TokenType.SUB, 0, true, OperatorType.BINARY));
            symbolTable.set(new OperatorInfo(TokenType.MULT, 1, true, OperatorType.BINARY));
            symbolTable.set(new OperatorInfo(TokenType.DIV, 1, true, OperatorType.BINARY));
            symbolTable.set(new OperatorInfo(TokenType.MOD, 1, true, OperatorType.BINARY));
            symbolTable.set(new OperatorInfo(TokenType.BITWISE_NOT, 1, true, OperatorType.UNARY));
            symbolTable.set(new OperatorInfo(TokenType.BITWISE_AND, 1, true, OperatorType.BINARY));
            symbolTable.set(new OperatorInfo(TokenType.BITWISE_OR, 1, true, OperatorType.BINARY));
            symbolTable.set(new OperatorInfo(TokenType.LOGICAL_NOT, 1, true, OperatorType.UNARY));
            symbolTable.set(new OperatorInfo(TokenType.LOGICAL_AND, 1, true, OperatorType.BINARY));
            symbolTable.set(new OperatorInfo(TokenType.LOGICAL_OR, 1, true, OperatorType.BINARY));
            symbolTable.set(new OperatorInfo(TokenType.DOT, 0, true, OperatorType.BINARY));
            symbolTable.set(new OperatorInfo(TokenType.COLON, 0, true, OperatorType.BINARY));
            symbolTable.set(new OperatorInfo(TokenType.LPAREN, 0, true, OperatorType.NONE));
            symbolTable.set(new OperatorInfo(TokenType.RPAREN, 0, true, OperatorType.NONE));
            symbolTable.set(new OperatorInfo(TokenType.SEMICOLON, 0, false, OperatorType.NONE));
            symbolTable.set(new OperatorInfo(TokenType.ASSIGNMENT, 0, false, OperatorType.BINARY));

            init = true;
        }
        return symbolTable;
    }

    /**
     * Gets the symbol in the table associated with the given key.
     *
     * @param key a dummy symbol.
     * @return the symbol associated with the given key.
     */
    private SymbolInfo get(SymbolInfo key) {
        return symbols.get(key);
    }

    /**
     * Inserts a new symbol into the table if it does not exist, otherwise, replace the old symbol with the new one.
     *
     * @param symbol the symbol to be set.
     * @return the old symbol if one exists and null otherwise.
     */
    public SymbolInfo set(SymbolInfo symbol) {
        return symbols.put(symbol, symbol);
    }

    /**
     * Gets a symbol based on the key string and the symbol type.
     *
     * @param id         the string that identifies the symbol.
     * @param symbolType the type of the symbol.
     * @return a symbol if one exists in the table and null otherwise.
     */
    private SymbolInfo getSymbol(String id, SymbolType symbolType) {
        SymbolInfo dummy = new SymbolInfo(id, TokenType.UNKNOWN, symbolType);
        return get(dummy);
    }

    /**
     * Gets a variable symbol based on the given key string and the scope.
     *
     * @param id    the string that identifies the variable.
     * @param scope the scope of the variable.
     * @return a variable symbol if one exists in the table and null otherwise.
     */
    public VarInfo getVar(String id, Block scope) {
        VarInfo dummy = new VarInfo(id, scope, null, true);
        return (VarInfo) get(dummy);
    }

    /**
     * Gets a keyword symbol in the symbol table.
     *
     * @param id the string that identifies the keyword.
     * @return a keyword symbol if one exists and null otherwise.
     */
    public KeywordInfo getKeyword(String id) {
        return (KeywordInfo) getSymbol(id, SymbolType.KEYWORD);
    }

    /**
     * Gets an operator symbol in the symbol table.
     *
     * @param id the string that identifies the operator.
     * @return an operator symbol if one exists and null otherwise.
     */
    public OperatorInfo getOperator(String id) {
        return (OperatorInfo) getSymbol(id, SymbolType.OPERATOR);
    }

    /**
     * Gets a type symbol in the symbol table.
     *
     * @param id the string that identifies the type.
     * @return a type symbol if one exists and null otherwise.
     */
    public TypeInfo getType(String id) {
        return (TypeInfo) getSymbol(id, SymbolType.TYPE);
    }
}
