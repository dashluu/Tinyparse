package Parser;

import Tokens.Token;

import java.util.ArrayList;
import java.util.Iterator;

public class Node implements Iterable<Node> {
    private final Token token;
    private final NodeType type;
    private final ArrayList<Node> children = new ArrayList<>();

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

    public Node(Token token, NodeType type) {
        this.token = token;
        this.type = type;
    }

    public Token getToken() {
        return token;
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

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Node node)) {
            return false;
        }
        return token.equals(node.token) && type == node.type;
    }
}
