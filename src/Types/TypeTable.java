package Types;

import java.util.HashMap;

public class TypeTable {
    private final HashMap<String, TypeInfo> typeMap = new HashMap<>();
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
            INSTANCE.register(new TypeInfo("int", 4));
            INSTANCE.register(new TypeInfo("float", 4));
            INSTANCE.register(new TypeInfo("bool", 4));

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
        typeMap.put(type.getId(), type);
    }

    /**
     * Gets the type associated with the given id.
     *
     * @param id id of the type.
     * @return a TypeInfo object associated with the given id.
     */
    public TypeInfo getType(String id) {
        return typeMap.get(id);
    }
}
