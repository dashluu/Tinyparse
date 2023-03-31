package Symbols;

import Parser.Block;
import Reserved.TypeInfo;

public class SymbolInfo {
    protected final String id;
    protected final SymbolType symbolType;
    protected final Block scope;
    protected final TypeInfo dataType;

    public SymbolInfo(String id, SymbolType symbolType, Block scope, TypeInfo dataType) {
        this.id = id;
        this.symbolType = symbolType;
        this.scope = scope;
        this.dataType = dataType;
    }

    public String getId() {
        return id;
    }

    public SymbolType getSymbolType() {
        return symbolType;
    }

    public Block getScope() {
        return scope;
    }

    public TypeInfo getDataType() {
        return dataType;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof SymbolInfo symbolInfo)) {
            return false;
        }
        return id.equals(symbolInfo.id);
    }
}
