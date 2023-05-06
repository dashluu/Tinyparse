package Nodes;

import Tokens.Token;

import java.util.ArrayList;
import java.util.Iterator;

public class Node {
    protected final Token tok;
    protected final NodeType type;

    public Node(Token tok, NodeType type) {
        this.tok = tok;
        this.type = type;
    }

    public Token getTok() {
        return tok;
    }

    public NodeType getType() {
        return type;
    }

    public String toJson() {
        return "\"Node type\": \"" + type + "\"";
    }
}
