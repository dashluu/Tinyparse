package Symbols;

import Reserved.TypeInfo;

public class VarInfo extends SymbolInfo {
    private final boolean mutable;

    public VarInfo(String id, TypeInfo dataType, boolean mutable) {
        super(id, SymbolType.VAR, dataType);
        this.mutable = mutable;
    }

    public boolean isMutable() {
        return mutable;
    }
}
