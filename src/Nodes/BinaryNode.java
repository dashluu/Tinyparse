package Nodes;

import Tokens.Token;
import Types.TypeInfo;

public class BinaryNode extends DataTypeNode {
    private DataTypeNode left;
    private DataTypeNode right;

    public BinaryNode(Token tok, NodeType type, TypeInfo dataType) {
        super(tok, type, dataType);
    }

    public DataTypeNode getLeft() {
        return left;
    }

    public void setLeft(DataTypeNode left) {
        this.left = left;
    }

    public DataTypeNode getRight() {
        return right;
    }

    public void setRight(DataTypeNode right) {
        this.right = right;
    }

    @Override
    public String toJson() {
        return super.toJson() + "\",\n\"Children\": [\n{\n\"" +
                left.toJson() + ",\n" +
                right.toJson() + "\n]\n}\n";
    }
}
