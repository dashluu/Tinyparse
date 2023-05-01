package Nodes;

import Tokens.Token;
import Types.TypeInfo;

public class VarNode extends DataTypeNode {
    private final boolean mutable;

    public VarNode(Token tok, NodeType type, TypeInfo dataType, boolean mutable) {
        super(tok, type, dataType);
        this.mutable = mutable;
    }

    public boolean isMutable() {
        return mutable;
    }

    @Override
    public String toJson() {
        return super.toJson() + ",\n\"Mutable\": \"" + mutable + "\",\n\"Id\": \"" + tok.getValue() + "\"";
    }
}
