package Symbols;

import Global.Block;
import Global.TokenType;

public class VarInfo extends SymbolInfo {
    private final Block scope;
    private final TypeInfo dataType;
    private final boolean mutable;

    public VarInfo(String id, Block scope, TypeInfo dataType, boolean mutable) {
        super(id, TokenType.VAR, SymbolType.VAR);
        this.scope = scope;
        this.dataType = dataType;
        this.mutable = mutable;
    }

    public Block getScope() {
        return scope;
    }

    public TypeInfo getDataType() {
        return dataType;
    }

    public boolean isMutable() {
        return mutable;
    }

    @Override
    public int hashCode() {
        String hashStr = scope.getId() + "." + id;
        return hashStr.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj) || !(obj instanceof VarInfo varInfo)) {
            return false;
        }
        return scope.equals(varInfo.getScope());
    }
}
