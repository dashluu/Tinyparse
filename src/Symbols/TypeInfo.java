package Symbols;

import Global.TokenType;

public class TypeInfo extends SymbolInfo {
    private final int size;

    public TypeInfo(TokenType tokenType, int size) {
        super(tokenType.name(), tokenType, SymbolType.TYPE);
        this.size = size;
    }

    public int getSize() {
        return size;
    }
}
