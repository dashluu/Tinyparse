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
    private void extractTokens(String inputStr, ArrayList<Token> actualTokens)
            throws SyntaxError, IOException {
        BufferedReader reader = new BufferedReader(new StringReader(inputStr));
        Lexer lexer = new Lexer(reader);
        Token token;
        while ((token = lexer.consume()) != null && token.getType() != TokenType.EOF) {
            actualTokens.add(token);
        }
    }

    private void checkTokens(String inputStr, ArrayList<Token> expectedTokens) {
        try {
            ArrayList<Token> actualTokens = new ArrayList<>();
            extractTokens(inputStr, actualTokens);
            assertEquals(expectedTokens, actualTokens);
        } catch (SyntaxError | IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testValidInput1() {
        String inputStr = "52+-(-25.)-(32.4-+.e.)/.9*.";
        ArrayList<Token> expectedTokens = new ArrayList<>();
        expectedTokens.add(new Token("52", TokenType.INT_LITERAL));
        expectedTokens.add(new Token("+", TokenType.ADD));
        expectedTokens.add(new Token("-", TokenType.SUB));
        expectedTokens.add(new Token("(", TokenType.LPAREN));
        expectedTokens.add(new Token("-", TokenType.SUB));
        expectedTokens.add(new Token("25.0", TokenType.FLOAT_LITERAL));
        expectedTokens.add(new Token(")", TokenType.RPAREN));
        expectedTokens.add(new Token("-", TokenType.SUB));
        expectedTokens.add(new Token("(", TokenType.LPAREN));
        expectedTokens.add(new Token("32.4", TokenType.FLOAT_LITERAL));
        expectedTokens.add(new Token("-", TokenType.SUB));
        expectedTokens.add(new Token("+", TokenType.ADD));
        expectedTokens.add(new Token("0.0e0.0", TokenType.FLOAT_LITERAL));
        expectedTokens.add(new Token(")", TokenType.RPAREN));
        expectedTokens.add(new Token("/", TokenType.DIV));
        expectedTokens.add(new Token("0.9", TokenType.FLOAT_LITERAL));
        expectedTokens.add(new Token("*", TokenType.MULT));
        expectedTokens.add(new Token("0.0", TokenType.FLOAT_LITERAL));
        checkTokens(inputStr, expectedTokens);
    }

    @Test
    public void testValidInput2() {
        String inputStr = "  var b=b +\t-.e+.5 *  a/a  *((2.e-1-67.+71e3*21)))\t";
        ArrayList<Token> expectedTokens = new ArrayList<>();
        expectedTokens.add(new Token("var", TokenType.MUTABLE_DECL));
        expectedTokens.add(new Token("b", TokenType.ID));
        expectedTokens.add(new Token("=", TokenType.ASSIGNMENT));
        expectedTokens.add(new Token("b", TokenType.ID));
        expectedTokens.add(new Token("+", TokenType.ADD));
        expectedTokens.add(new Token("-", TokenType.SUB));
        expectedTokens.add(new Token("0.0e+0.5", TokenType.FLOAT_LITERAL));
        expectedTokens.add(new Token("*", TokenType.MULT));
        expectedTokens.add(new Token("a", TokenType.ID));
        expectedTokens.add(new Token("/", TokenType.DIV));
        expectedTokens.add(new Token("a", TokenType.ID));
        expectedTokens.add(new Token("*", TokenType.MULT));
        expectedTokens.add(new Token("(", TokenType.LPAREN));
        expectedTokens.add(new Token("(", TokenType.LPAREN));
        expectedTokens.add(new Token("2.0e-1", TokenType.FLOAT_LITERAL));
        expectedTokens.add(new Token("-", TokenType.SUB));
        expectedTokens.add(new Token("67.0", TokenType.FLOAT_LITERAL));
        expectedTokens.add(new Token("+", TokenType.ADD));
        expectedTokens.add(new Token("71e3", TokenType.FLOAT_LITERAL));
        expectedTokens.add(new Token("*", TokenType.MULT));
        expectedTokens.add(new Token("21", TokenType.INT_LITERAL));
        expectedTokens.add(new Token(")", TokenType.RPAREN));
        expectedTokens.add(new Token(")", TokenType.RPAREN));
        expectedTokens.add(new Token(")", TokenType.RPAREN));
        checkTokens(inputStr, expectedTokens);
    }

    @Test
    public void testInvalidInput1() {
        String inputStr = "\nw32_=_b +-.e+.5 /\n a/72a\t";
        String expectedException;
        ArrayList<Token> actualTokens = new ArrayList<>();
        ArrayList<Token> expectedTokens = new ArrayList<>();

        try {
            extractTokens(inputStr, actualTokens);
        } catch (SyntaxError | IOException e) {
            // Check the tokens that have been read before the exception is thrown
            expectedTokens.add(new Token("w32_", TokenType.ID));
            expectedTokens.add(new Token("=", TokenType.ASSIGNMENT));
            expectedTokens.add(new Token("_b", TokenType.ID));
            expectedTokens.add(new Token("+", TokenType.ADD));
            expectedTokens.add(new Token("-", TokenType.SUB));
            expectedTokens.add(new Token("0.0e+0.5", TokenType.FLOAT_LITERAL));
            expectedTokens.add(new Token("/", TokenType.DIV));
            expectedTokens.add(new Token("a", TokenType.ID));
            expectedTokens.add(new Token("/", TokenType.DIV));
            assertEquals(expectedTokens, actualTokens);
            // Check the exception
            expectedException = "Invalid numeric expression after '72' on line 3";
            assertEquals(expectedException, e.getMessage());
        }
    }
}