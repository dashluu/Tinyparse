package Parser;

import Exceptions.SyntaxError;
import Lexer.Lexer;
import Nodes.JsonTraversal;
import Nodes.Node;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.*;

class DeclParserTest {
    private Node parseDecl(String input) throws SyntaxError, IOException {
        BufferedReader reader = new BufferedReader(new StringReader(input));
        Lexer lexer = new Lexer(reader);
        ExprParser exprParser = new ExprParser(lexer);
        DeclParser declParser = new DeclParser(lexer, exprParser);
        Block scope = new Block(null);
        return declParser.parseDecl(scope);
    }

    private void checkValidDecl(String input, String expected) {
        try {
            Node root = parseDecl(input);
            JsonTraversal jsonTraversal = new JsonTraversal();
            String actual = jsonTraversal.traverse(root);
            assertEquals(expected, actual);
        } catch (IOException | SyntaxError e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    void testValidDecl1() {
        String input = "var a: int;";
        String expected =
                """
                        {
                        "Node type": "DECL",
                        "Data type": "int",
                        "Size": 4,
                        "Mutable": "true",
                        "Id": "a",
                        "Children": [
                        
                        ]
                        }
                        """;
        checkValidDecl(input, expected);
    }
}