package Parser;

import Exceptions.SyntaxError;
import Lexer.Lexer;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class ExprParserTest {
    private Node parseExpr(String inputStr) throws SyntaxError, IOException {
        BufferedReader reader = new BufferedReader(new StringReader(inputStr));
        Lexer lexer = new Lexer(reader);
        ExprParser exprParser = new ExprParser(lexer);
        Block scope = new Block(null);
        return exprParser.parseExpr(scope);
    }

    private String recurTraverseAST(Node root) {
        if (root != null) {
            String value = root.getToken().getValue();
            switch (root.getType()) {
                case TERM -> {
                    return value;
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
        try {
            BufferedReader reader = new BufferedReader(new FileReader("src/Parser/ExprParserTestCases.txt"));
            String input, expected, actual;
            Node root;

            while ((input = reader.readLine()) != null) {
                expected = reader.readLine();
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