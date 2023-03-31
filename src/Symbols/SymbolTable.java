package Symbols;

import java.util.HashMap;

public class SymbolTable {
    private final HashMap<String, SymbolInfo> symbolMap = new HashMap<>();
    private final SymbolTable parent;

    public SymbolTable(SymbolTable parent) {
        this.parent = parent;
    }

    public SymbolTable getParent() {
        return parent;
    }

    /**
     * Inserts a new symbol into the table if it does not exist, otherwise, replaces the old symbol with the new one.
     *
     * @param symbolInfo the symbol to be set.
     * @return the old symbol.
     */
    public SymbolInfo set(SymbolInfo symbolInfo) {
        return symbolMap.put(symbolInfo.getId(), symbolInfo);
    }

    /**
     * Finds the symbol associated with the given key by moving up the chain of symbol tables.
     *
     * @param key the string that identifies a symbol in the table.
     * @return a symbol if one exists and null otherwise.
     */
    public SymbolInfo get(String key) {
        SymbolTable table = this;
        SymbolInfo symbolInfo = null;
        while (table != null && symbolInfo == null) {
            symbolInfo = table.symbolMap.get(key);
            table = table.parent;
        }
        return symbolInfo;
    }
}
