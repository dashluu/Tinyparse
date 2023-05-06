package Operators;

import Tokens.TokenType;
import Types.TypeInfo;

public class UnaryOperatorCompat extends OperatorCompat {
    private final TypeInfo operandDataType;

    public UnaryOperatorCompat(TokenType id, TypeInfo operandDataType) {
        super(id, OperatorCompatType.UNARY);
        this.operandDataType = operandDataType;
    }

    public TypeInfo getOperandDataType() {
        return operandDataType;
    }

    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj) || !(obj instanceof UnaryOperatorCompat unaryOpTypeCompat)) {
            return false;
        }
        return operandDataType.equals(unaryOpTypeCompat.operandDataType);
    }
}
