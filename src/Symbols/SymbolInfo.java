package Symbols;

import Types.TypeInfo;

public abstract class SymbolInfo {
    protected final String id;
    protected final SymbolType symbolType;
    protected TypeInfo dataType;

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

    public void setDataType(TypeInfo dataType) {
        this.dataType = dataType;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof SymbolInfo symbol)) {
            return false;
        }
        return id.equals(symbol.id);
    }
}
