package Symbols;

import Global.TokenType;

/**
 * Base class for everything that will be stored in the symbol table.
 */
public class SymbolInfo {
    protected final String id;
    protected final TokenType tokenType;
    protected final SymbolType symbolType;

    public SymbolInfo(String id, TokenType tokenType, SymbolType symbolType) {
        this.id = id;
        this.tokenType = tokenType;
        this.symbolType = symbolType;
    }

    public String getId() {
        return id;
    }

    public TokenType getTokenType() {
        return tokenType;
    }

    public SymbolType getSymbolType() {
        return symbolType;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
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
