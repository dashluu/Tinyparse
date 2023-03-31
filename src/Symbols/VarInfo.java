package Symbols;

import Parser.Block;
import Reserved.TypeInfo;

public class VarInfo extends SymbolInfo {
    private final boolean mutable;

    public VarInfo(String id, Block scope, TypeInfo dataType, boolean mutable) {
        super(id, SymbolType.VAR, scope, dataType);
        this.mutable = mutable;
    }

    public boolean isMutable() {
        return mutable;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
