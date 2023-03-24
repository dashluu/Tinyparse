package Symbols;

import Global.TokenType;

public class OperatorInfo extends SymbolInfo {
    // Precedence
    private final int preced;
    private final boolean leftToRight;
    // Operator type: NONE, UNARY, BINARY
    private final OperatorType opType;

    public OperatorInfo(TokenType tokenType, int preced, boolean leftToRight, OperatorType opType) {
        super(tokenType.name(), tokenType, SymbolType.OPERATOR);
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
