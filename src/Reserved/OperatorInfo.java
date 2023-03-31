package Reserved;

import Tokens.TokenType;

public class OperatorInfo extends ReservedInfo {
    // Precedence
    private final int preced;
    private final boolean leftToRight;
    // Operator type: NONE, UNARY, BINARY
    private final OperatorType opType;

    public OperatorInfo(TokenType id, int preced, boolean leftToRight, OperatorType opType) {
        super(id, ReservedType.OPERATOR);
        this.preced = preced;
        this.leftToRight = leftToRight;
        this.opType = opType;
    }

    public int getPreced() {
        return preced;
    }

    public boolean isLeftToRight() {
        return leftToRight;
    }

    public OperatorType getOperatorType() {
        return opType;
    }
}