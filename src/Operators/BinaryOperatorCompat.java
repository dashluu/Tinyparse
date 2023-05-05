package Operators;

import Tokens.TokenType;
import Types.TypeInfo;

public class BinaryOperatorCompat extends OperatorCompat {
    private final TypeInfo leftDataType;
    private final TypeInfo rightDataType;
    private final TypeInfo resultDataType;

    protected BinaryOperatorCompat(TokenType id, TypeInfo leftDataType, TypeInfo rightDataType, TypeInfo resultDataType) {
        super(id, OperatorCompatType.BINARY);
        this.leftDataType = leftDataType;
        this.rightDataType = rightDataType;
        this.resultDataType = resultDataType;
    }

    public TypeInfo getLeftDataType() {
        return leftDataType;
    }

    public TypeInfo getRightDataType() {
        return rightDataType;
    }

    public TypeInfo getResultDataType() {
        return resultDataType;
    }

    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj) || !(obj instanceof BinaryOperatorCompat binaryOpTypeCompat)) {
            return false;
        }
        return leftDataType.equals(binaryOpTypeCompat.leftDataType) &&
                rightDataType.equals(binaryOpTypeCompat.rightDataType) &&
                resultDataType.equals(binaryOpTypeCompat.resultDataType);
    }
}
