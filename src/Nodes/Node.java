package Nodes;

import Tokens.Token;

import java.util.ArrayList;
import java.util.Iterator;

public class Node implements Iterable<Node> {
    protected final Token tok;
    protected final NodeType type;
    protected final ArrayList<Node> children = new ArrayList<>();

    @Override
    public Iterator<Node> iterator() {
        return new NodeIter();
    }

    private class NodeIter implements Iterator<Node> {
        private int index;

        public NodeIter() {
            index = 0;
        }

        @Override
        public boolean hasNext() {
            return index < children.size();
        }

        @Override
        public Node next() {
            Node child = children.get(index);
            ++index;
            return child;
        }
    }

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

    public Node getChild(int i) {
        return children.get(i);
    }

    public void addChild(Node child) {
        children.add(child);
    }

    public int countChildren() {
        return children.size();
    }

    public String toJson() {
        return "\"Node type\": \"" + type + "\"";
    }
}
