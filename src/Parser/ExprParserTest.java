package Parser;

import Exceptions.SyntaxError;
import Lexer.Lexer;
import Nodes.Node;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class ExprParserTest {
    private Node parseExpr(String input) throws SyntaxError, IOException {
        BufferedReader reader = new BufferedReader(new StringReader(input));
        Lexer lexer = new Lexer(reader);
        ExprParser exprParser = new ExprParser(lexer);
        Block scope = new Block(null);
        return exprParser.parseExpr(scope);
    }

    private String recurTraverseAST(Node root) {
        if (root != null) {
            String value = root.getTok().getValue();
            switch (root.getType()) {
                case TERMINAL -> {
                    return "(" + value + ")";
                }
                case UNARY -> {
                    String operand = recurTraverseAST(root.getChild(0));
                    return "(" + value + operand + ")";
                }
                case BINARY -> {
                    String left = recurTraverseAST(root.getChild(0));
                    String right = recurTraverseAST(root.getChild(1));
                    return "(" + left + value + right + ")";
                }
            }
        }
        return null;
    }

    @Test
    void testValidExpr() {
        String[] inputArr = {
                "1*2+3;",
                "-1 + 2 * 3;",
                "--12.* 326+ 4.3e1 / 97.0;",
                "1*(2+3);"
        };

        String[] expectedArr = {
                "(((1)*(2))+(3))",
                "((-(1))+((2)*(3)))",
                "(((-(-(12.0)))*(326))+((4.3e1)/(97.0)))",
                "((1)*((2)+(3)))"
        };

        String input, expected, actual;
        Node root;

        try {
            for (int i = 0; i < inputArr.length; ++i) {
                input = inputArr[i];
                expected = expectedArr[i];
                root = parseExpr(input);
                actual = recurTraverseAST(root);
                assertEquals(expected, actual);
            }
        } catch (IOException | SyntaxError e) {
            e.printStackTrace();
            fail();
        }
    }
}