package Operators;

import Tokens.TokenType;

public abstract class OperatorCompat {
    protected final TokenType id;
    protected final OperatorCompatType compatType;

    public OperatorCompat(TokenType id, OperatorCompatType compatType) {
        this.id = id;
        this.compatType = compatType;
    }

    public TokenType getId() {
        return id;
    }

    public OperatorCompatType getCompatType() {
        return compatType;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof OperatorCompat opDataTypeCompat)) {
            return false;
        }
        return id == opDataTypeCompat.id && compatType == opDataTypeCompat.compatType;
    }
}
