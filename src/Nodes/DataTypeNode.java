package Nodes;

import Tokens.Token;
import Types.TypeInfo;

public class DataTypeNode extends Node {
    protected TypeInfo dataType;

    public DataTypeNode(Token tok, NodeType type, TypeInfo dataType) {
        super(tok, type);
        this.dataType = dataType;
    }

    public TypeInfo getDataType() {
        return dataType;
    }

    public void setDataType(TypeInfo dataType) {
        this.dataType = dataType;
    }

    @Override
    public String toJson() {
        return super.toJson() +
                ",\n\"Data type\": \"" + dataType.getId() +
                "\",\n\"Size\": " + dataType.getSize();
    }
}
