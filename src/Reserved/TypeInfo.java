package Reserved;

import Tokens.TokenType;

public class TypeInfo extends ReservedInfo {
    private final int size;

    public TypeInfo(TokenType id, int size) {
        super(id, ReservedType.TYPE);
        this.size = size;
    }

    public int getSize() {
        return size;
    }
}
