package Operators;

import Tokens.TokenType;
import Types.TypeInfo;

public class UnaryOperatorCompat extends OperatorCompat {
    private final TypeInfo operandDataType;
    private final TypeInfo resultDataType;

    protected UnaryOperatorCompat(TokenType id, TypeInfo operandDataType, TypeInfo resultDataType) {
        super(id, OperatorCompatType.UNARY);
        this.operandDataType = operandDataType;
        this.resultDataType = resultDataType;
    }

    public TypeInfo getOperandDataType() {
        return operandDataType;
    }

    public TypeInfo getResultDataType() {
        return resultDataType;
    }

    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj) || !(obj instanceof UnaryOperatorCompat unaryOpTypeCompat)) {
            return false;
        }
        return operandDataType.equals(unaryOpTypeCompat.operandDataType) &&
                resultDataType.equals(unaryOpTypeCompat.resultDataType);
    }
}
