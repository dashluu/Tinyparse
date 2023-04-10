package Reserved;

import Tokens.TokenType;

public class ReservedInfo {
    protected final TokenType id;
    protected final ReservedType type;

    public ReservedInfo(TokenType id, ReservedType type) {
        this.id = id;
        this.type = type;
    }

    public TokenType getId() {
        return id;
    }

    public ReservedType getType() {
        return type;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ReservedInfo reservedInfo)) {
            return false;
        }
        return id == reservedInfo.id;
    }
}
