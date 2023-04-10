package Reserved;

import Tokens.TokenType;

public class OperatorInfo extends ReservedInfo {
    public OperatorInfo(TokenType id) {
        super(id, ReservedType.OPERATOR);
    }
}
