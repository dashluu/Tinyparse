package Symbols;

import Parser.Block;
import Reserved.TypeInfo;

public class SymbolInfo {
    protected final String id;
    protected final SymbolType symbolType;
    protected final TypeInfo dataType;

    public SymbolInfo(String id, SymbolType symbolType, TypeInfo dataType) {
        this.id = id;
        this.symbolType = symbolType;
        this.dataType = dataType;
    }

    public String getId() {
        return id;
    }

    public SymbolType getSymbolType() {
        return symbolType;
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
