package Parser;

import Exceptions.SyntaxError;
import Lexer.Lexer;
import Tokens.Token;
import Tokens.TokenType;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.fail;

class ExprParserTest {
    private Node parseExpr(String inputStr) throws SyntaxError, IOException {
        BufferedReader reader = new BufferedReader(new StringReader(inputStr));
        Lexer lexer = new Lexer(reader);
        ExprParser exprParser = new ExprParser(lexer);
        Block scope = new Block(null);
        return exprParser.parseExpr(scope);
    }

    private boolean recurCheckAST(Node expectedAST, Node actualAST) {
        // Check if both nodes are null
        if (expectedAST == null) {
            return actualAST == null;
        }
        // Compare data of the two nodes
        if (!actualAST.equals(expectedAST)) {
            return false;
        }
        // Compare number of children of the two nodes
        if (actualAST.countChildren() != expectedAST.countChildren()) {
            return false;
        }
        // Recursively compare children of the two nodes
        for (int i = 0; i < actualAST.countChildren(); ++i) {
            if (!recurCheckAST(actualAST.getChild(i), expectedAST.getChild(i))) {
                return false;
            }
        }
        return true;
    }

    @Test
    void testValidExpr1() {
        String inputStr = "-1 + 2 * 3;";

        // Construct the expected tree
        Node node1 = new Node(new Token("1", TokenType.INT_LITERAL));
        Node node2 = new Node(new Token("2", TokenType.INT_LITERAL));
        Node node3 = new Node(new Token("3", TokenType.INT_LITERAL));
        Node node4 = new Node(new Token("+", TokenType.ADD));
        Node node5 = new Node(new Token("*", TokenType.MULT));
        Node node6 = new Node(new Token("-", TokenType.SUB));
        node6.addChild(node1);
        node5.addChild(node2);
        node5.addChild(node3);
        node4.addChild(node6);
        node4.addChild(node5);

        // Check if the parser produces a correct tree
        try {
            Node actualAST = parseExpr(inputStr);
            boolean astCheck = recurCheckAST(node4, actualAST);
            if (!astCheck) {
                fail();
            }
        } catch (SyntaxError | IOException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    void testEmptyExpr() {
        String inputStr = ";";

        // Check if the parser produces a correct tree
        try {
            Node actualAST = parseExpr(inputStr);
            boolean astCheck = recurCheckAST(null, actualAST);
            if (!astCheck) {
                fail();
            }
        } catch (SyntaxError | IOException e) {
            e.printStackTrace();
            fail();
        }
    }
}