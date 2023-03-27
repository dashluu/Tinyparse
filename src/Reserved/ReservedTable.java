package Reserved;

import Global.TokenType;

import java.util.HashMap;

public class ReservedTable {
    private final HashMap<ReservedInfo, ReservedInfo> reservedMap = new HashMap<>();
    private static final ReservedTable RESERVED_TABLE = new ReservedTable();
    private static boolean init = false;

    private ReservedTable() {
    }

    /**
     * Initializes the only instance of the reserved table if it has not been initialized and then returns it.
     *
     * @return a ReservedTable object.
     */
    public static ReservedTable getInstance() {
        if (!init) {
            // Initialize the reserved table
            RESERVED_TABLE.set(new KeywordInfo(TokenType.MUTABLE_ID_DECL));
            RESERVED_TABLE.set(new KeywordInfo(TokenType.IMMUTABLE_ID_DECL));
            RESERVED_TABLE.set(new KeywordInfo(TokenType.BOOL_LITERAL));
            RESERVED_TABLE.set(new KeywordInfo(TokenType.BOOL_LITERAL));
            RESERVED_TABLE.set(new TypeInfo(TokenType.INT_TYPE_ID, 4));
            RESERVED_TABLE.set(new TypeInfo(TokenType.FLOAT_TYPE_ID, 4));
            RESERVED_TABLE.set(new TypeInfo(TokenType.BOOL_TYPE_ID, 1));
            RESERVED_TABLE.set(new OperatorInfo(TokenType.ADD, 2, true, OperatorType.BINARY));
            RESERVED_TABLE.set(new OperatorInfo(TokenType.SUB, 2, true, OperatorType.BINARY));
            RESERVED_TABLE.set(new OperatorInfo(TokenType.MULT, 3, true, OperatorType.BINARY));
            RESERVED_TABLE.set(new OperatorInfo(TokenType.DIV, 3, true, OperatorType.BINARY));
            RESERVED_TABLE.set(new OperatorInfo(TokenType.MOD, 3, true, OperatorType.BINARY));
            RESERVED_TABLE.set(new OperatorInfo(TokenType.POW, 4, true, OperatorType.BINARY));
            RESERVED_TABLE.set(new OperatorInfo(TokenType.DOT, 0, true, OperatorType.BINARY));
            RESERVED_TABLE.set(new OperatorInfo(TokenType.COLON, 0, true, OperatorType.BINARY));
            RESERVED_TABLE.set(new OperatorInfo(TokenType.LPAREN, 0, true, OperatorType.NONE));
            RESERVED_TABLE.set(new OperatorInfo(TokenType.RPAREN, 0, true, OperatorType.NONE));
            RESERVED_TABLE.set(new OperatorInfo(TokenType.ASSIGNMENT, 1, false, OperatorType.BINARY));
            RESERVED_TABLE.set(new OperatorInfo(TokenType.SEMICOLON, 0, false, OperatorType.NONE));

            init = true;
        }
        return RESERVED_TABLE;
    }

    /**
     * Inserts a new ReservedInfo object into the table if it does not exist, otherwise, replaces the old ReservedInfo
     * object with the new one.
     *
     * @param reservedInfo the ReservedInfo object to be set.
     */
    private void set(ReservedInfo reservedInfo) {
        reservedMap.put(reservedInfo, reservedInfo);
    }

    /**
     * Gets a ReservedInfo object based on the identifier and the type.
     *
     * @param id           the ReservedInfo object's identifier.
     * @param reservedType the ReservedInfo object's type.
     * @return a ReservedInfo object if one exists in the table and null otherwise.
     */
    private ReservedInfo get(TokenType id, ReservedType reservedType) {
        ReservedInfo dummy = new ReservedInfo(id, reservedType);
        return reservedMap.get(dummy);
    }

    /**
     * Gets a keyword in the reserved table.
     *
     * @param id the keyword identifier.
     * @return a keyword if one exists and null otherwise.
     */
    public KeywordInfo getKeyword(TokenType id) {
        return (KeywordInfo) get(id, ReservedType.KEYWORD);
    }

    /**
     * Gets an operator in the reserved table.
     *
     * @param id the operator identifier.
     * @return an operator if one exists and null otherwise.
     */
    public OperatorInfo getOperator(TokenType id) {
        return (OperatorInfo) get(id, ReservedType.OPERATOR);
    }

    /**
     * Gets a type in the reserved table.
     *
     * @param id the type identifier.
     * @return a type if one exists and null otherwise.
     */
    public TypeInfo getType(TokenType id) {
        return (TypeInfo) get(id, ReservedType.TYPE);
    }
}
