package Nodes;

import java.util.ArrayList;

public class ScopeNode extends Node {
    private final ArrayList<Node> children = new ArrayList<>();

    public ScopeNode() {
        super(null, NodeType.SCOPE);
    }

    public void addChild(Node child) {
        children.add(child);
    }

    public int countChildren() {
        return children.size();
    }

    @Override
    public String toJson() {
        StringBuilder jsonStr = new StringBuilder(super.toJson() + ",\n\"Children\": [\n");
        for (Node child : children) {
            jsonStr.append("{\n").append(child.toJson()).append("\n},");
        }
        jsonStr.append("\n]");
        return jsonStr.toString();
    }
}
