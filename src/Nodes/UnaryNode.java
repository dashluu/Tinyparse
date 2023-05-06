package Nodes;

import Tokens.Token;
import Types.TypeInfo;

public class UnaryNode extends DataTypeNode {
    private DataTypeNode child;

    public UnaryNode(Token tok, NodeType type, TypeInfo dataType) {
        super(tok, type, dataType);
    }

    public DataTypeNode getChild() {
        return child;
    }

    public void setChild(DataTypeNode child) {
        this.child = child;
    }

    @Override
    public String toJson() {
        return super.toJson() + "\",\\n\"Children\": [\n{\n\"" + child.toJson() + "\n]\n}\n";
    }
}
