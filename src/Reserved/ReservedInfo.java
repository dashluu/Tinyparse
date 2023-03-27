package Reserved;

import Global.TokenType;

public class ReservedInfo {
    protected final TokenType id;
    protected final ReservedType reservedType;

    public ReservedInfo(TokenType id, ReservedType reservedType) {
        this.id = id;
        this.reservedType = reservedType;
    }

    public TokenType getId() {
        return id;
    }

    public ReservedType getReservedType() {
        return reservedType;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
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
