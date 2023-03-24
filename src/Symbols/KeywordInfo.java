package Symbols;

import Global.TokenType;

public class KeywordInfo extends SymbolInfo {
    public KeywordInfo(TokenType tokenType) {
        super(tokenType.name(), tokenType, SymbolType.KEYWORD);
    }
}
