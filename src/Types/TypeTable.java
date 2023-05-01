package Types;

import Tokens.TokenType;

import java.util.HashMap;

public class TypeTable {
    private final HashMap<String, TypeInfo> strTypeMap = new HashMap<>();
    private final HashMap<TokenType, TypeInfo> literalTypeMap = new HashMap<>();
    private final static TypeTable INSTANCE = new TypeTable();
    private static boolean init = false;

    private TypeTable() {
    }

    /**
     * Initializes the only instance of TypeTable if it has not been initialized and then returns it.
     *
     * @return a TypeTable object.
     */
    public static TypeTable getInstance() {
        if (!init) {
            // Add types to table
            INSTANCE.register(TokenType.INT_LITERAL, new TypeInfo("int", 4));
            INSTANCE.register(TokenType.FLOAT_LITERAL, new TypeInfo("float", 4));
            INSTANCE.register(TokenType.BOOL_LITERAL, new TypeInfo("bool", 4));

            init = true;
        }
        return INSTANCE;
    }

    /**
     * Adds a new data type to the table.
     *
     * @param type TypeInfo object that carries type data.
     */
    public void register(TypeInfo type) {
        strTypeMap.put(type.getId(), type);
    }

    /**
     * Maps a literal token type to a new data type and adds that data type to the table.
     *
     * @param literalType the token type of the literal.
     * @param type        the data type to be mapped to.
     */
    private void register(TokenType literalType, TypeInfo type) {
        literalTypeMap.put(literalType, type);
        register(type);
    }

    /**
     * Gets the type associated with the given id.
     *
     * @param id id of the type.
     * @return a TypeInfo object associated with the given id.
     */
    public TypeInfo getType(String id) {
        return strTypeMap.get(id);
    }

    /**
     * Gets the type associated with the given literal type.
     *
     * @param literalType the token type of the literal.
     * @return a TypeInfo object associated with the given literal type.
     */
    public TypeInfo getType(TokenType literalType) {
        return literalTypeMap.get(literalType);
    }
}
