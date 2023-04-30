package Lexer;

import Exceptions.SyntaxError;
import Tokens.Token;
import Tokens.TokenType;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class LexerTest {
    private void extractToks(String input, ArrayList<Token> tokList)
            throws SyntaxError, IOException {
        BufferedReader reader = new BufferedReader(new StringReader(input));
        Lexer lexer = new Lexer(reader);
        Token tok;
        while ((tok = lexer.consume()) != null && tok.getType() != TokenType.EOF) {
            tokList.add(tok);
        }
    }

    private void checkToks(String input, ArrayList<Token> expected) {
        try {
            ArrayList<Token> actual = new ArrayList<>();
            extractToks(input, actual);
            assertEquals(expected, actual);
        } catch (SyntaxError | IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testValidInput1() {
        String input = "52+-(-25.)-(32.4-+.e.)/.9*.";
        ArrayList<Token> expected = new ArrayList<>();
        expected.add(new Token("52", TokenType.INT_LITERAL));
        expected.add(new Token("+", TokenType.ADD));
        expected.add(new Token("-", TokenType.SUB));
        expected.add(new Token("(", TokenType.LPAREN));
        expected.add(new Token("-", TokenType.SUB));
        expected.add(new Token("25.0", TokenType.FLOAT_LITERAL));
        expected.add(new Token(")", TokenType.RPAREN));
        expected.add(new Token("-", TokenType.SUB));
        expected.add(new Token("(", TokenType.LPAREN));
        expected.add(new Token("32.4", TokenType.FLOAT_LITERAL));
        expected.add(new Token("-", TokenType.SUB));
        expected.add(new Token("+", TokenType.ADD));
        expected.add(new Token("0.0e0.0", TokenType.FLOAT_LITERAL));
        expected.add(new Token(")", TokenType.RPAREN));
        expected.add(new Token("/", TokenType.DIV));
        expected.add(new Token("0.9", TokenType.FLOAT_LITERAL));
        expected.add(new Token("*", TokenType.MULT));
        expected.add(new Token("0.0", TokenType.FLOAT_LITERAL));
        checkToks(input, expected);
    }

    @Test
    public void testValidInput2() {
        String input = "  var b=b +\t-.e+.5 *  a/a  *((2.e-1-67.+71e3*21)))\t";
        ArrayList<Token> expected = new ArrayList<>();
        expected.add(new Token("var", TokenType.VAR_DECL));
        expected.add(new Token("b", TokenType.ID));
        expected.add(new Token("=", TokenType.ASSIGNMENT));
        expected.add(new Token("b", TokenType.ID));
        expected.add(new Token("+", TokenType.ADD));
        expected.add(new Token("-", TokenType.SUB));
        expected.add(new Token("0.0e+0.5", TokenType.FLOAT_LITERAL));
        expected.add(new Token("*", TokenType.MULT));
        expected.add(new Token("a", TokenType.ID));
        expected.add(new Token("/", TokenType.DIV));
        expected.add(new Token("a", TokenType.ID));
        expected.add(new Token("*", TokenType.MULT));
        expected.add(new Token("(", TokenType.LPAREN));
        expected.add(new Token("(", TokenType.LPAREN));
        expected.add(new Token("2.0e-1", TokenType.FLOAT_LITERAL));
        expected.add(new Token("-", TokenType.SUB));
        expected.add(new Token("67.0", TokenType.FLOAT_LITERAL));
        expected.add(new Token("+", TokenType.ADD));
        expected.add(new Token("71e3", TokenType.FLOAT_LITERAL));
        expected.add(new Token("*", TokenType.MULT));
        expected.add(new Token("21", TokenType.INT_LITERAL));
        expected.add(new Token(")", TokenType.RPAREN));
        expected.add(new Token(")", TokenType.RPAREN));
        expected.add(new Token(")", TokenType.RPAREN));
        checkToks(input, expected);
    }

    @Test
    public void testInvalidInput1() {
        String input = "\nw32_=_b +-.e+.5 /\n a/72a\t";
        String expectedException;
        ArrayList<Token> actualList = new ArrayList<>();
        ArrayList<Token> expectedList = new ArrayList<>();

        try {
            extractToks(input, actualList);
        } catch (SyntaxError | IOException e) {
            // Check the tokens that have been read before the exception is thrown
            expectedList.add(new Token("w32_", TokenType.ID));
            expectedList.add(new Token("=", TokenType.ASSIGNMENT));
            expectedList.add(new Token("_b", TokenType.ID));
            expectedList.add(new Token("+", TokenType.ADD));
            expectedList.add(new Token("-", TokenType.SUB));
            expectedList.add(new Token("0.0e+0.5", TokenType.FLOAT_LITERAL));
            expectedList.add(new Token("/", TokenType.DIV));
            expectedList.add(new Token("a", TokenType.ID));
            expectedList.add(new Token("/", TokenType.DIV));
            assertEquals(expectedList, actualList);
            // Check the exception
            expectedException = "Invalid numeric expression after '72' on line 3";
            assertEquals(expectedException, e.getMessage());
        }
    }
}