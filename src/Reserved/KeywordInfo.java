package Reserved;

import Tokens.TokenType;

public class KeywordInfo extends ReservedInfo {
    public KeywordInfo(TokenType id) {
        super(id, ReservedType.KEYWORD);
    }
}