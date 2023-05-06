package Operators;

import Tokens.TokenType;
import Types.TypeInfo;

public class BinaryOperatorCompat extends OperatorCompat {
    private final TypeInfo leftDataType;
    private final TypeInfo rightDataType;

    public BinaryOperatorCompat(TokenType id, TypeInfo leftDataType, TypeInfo rightDataType) {
        super(id, OperatorCompatType.BINARY);
        this.leftDataType = leftDataType;
        this.rightDataType = rightDataType;
    }

    public TypeInfo getLeftDataType() {
        return leftDataType;
    }

    public TypeInfo getRightDataType() {
        return rightDataType;
    }

    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj) || !(obj instanceof BinaryOperatorCompat binaryOpTypeCompat)) {
            return false;
        }
        return leftDataType.equals(binaryOpTypeCompat.leftDataType) &&
                rightDataType.equals(binaryOpTypeCompat.rightDataType);
    }
}
