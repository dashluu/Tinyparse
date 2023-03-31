package Parser;

import Tokens.Token;

import java.util.ArrayList;

public class Node {
    private final Token token;
    private final ArrayList<Node> children = new ArrayList<>();

    public Node(Token token) {
        this.token = token;
    }

    public Token getToken() {
        return token;
    }

    public Node getChild(int i) {
        return children.get(i);
    }

    public void addChild(Node child) {
        children.add(child);
    }
}
