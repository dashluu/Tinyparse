package Lexer;

import Exceptions.SyntaxError;
import Global.Token;
import Global.TokenType;
import Symbols.SymbolInfo;
import Symbols.SymbolType;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayDeque;

public class Lexer {
    private final CharBuffer charBuffer;
    private final ArrayDeque<Token> tokenBuffer = new ArrayDeque<>();
    private final LexerTable lexerTable = LexerTable.getInstance();
    private final static String SPECIAL_CHARS = "()+-*/%~!&|<>=,.;:_";
    private final static int EOS = -1;
    private int currLine = 1;

    public Lexer(BufferedReader reader) {
        this.charBuffer = new CharBuffer(reader);
    }

    /**
     * Get the current line in the stream.
     *
     * @return the current line in the stream.
     */
    public int getCurrentLine() {
        return currLine;
    }

    /**
     * Skips the spaces until a non-space character is encountered.
     *
     * @throws IOException if the read operation causes an IO error.
     */
    private void skipSpaces() throws IOException {
        int c;
        while ((c = charBuffer.peek()) != EOS && isSpace(c)) {
            if (c == '\n') {
                ++currLine;
            }
            charBuffer.read();
        }
    }

    /**
     * Determines if the character is a space.
     *
     * @param c the character to be checked.
     * @return true if the character is a space and false otherwise.
     */
    private boolean isSpace(int c) {
        return Character.isWhitespace(c);
    }

    /**
     * Determines if the character is an alphanumeric or an underscore.
     *
     * @param c the character to be checked.
     * @return true if the character is an alphanumeric or an underscore and false otherwise.
     */
    private boolean isAlnumUnderscore(int c) {
        return Character.isAlphabetic(c) || Character.isDigit(c) || c == '_';
    }

    /**
     * Determines if the character is a valid special character.
     *
     * @param c the character to be checked.
     * @return true if the character is a valid special character and false otherwise.
     */
    private boolean isSpecialChar(int c) {
        return SPECIAL_CHARS.indexOf(c) >= 0;
    }

    /**
     * Determines if the character is a valid separator.
     *
     * @param c the character to be checked.
     * @return true if the character is a valid separator and false otherwise.
     */
    private boolean isSeparator(int c) {
        return c == EOS || isSpace(c) || c == ';';
    }

    /**
     * Peeks and removes the next token from the buffer.
     *
     * @return the next token in the buffer.
     * @throws SyntaxError if there is a syntax error.
     * @throws IOException if the read operation causes an IO error.
     */
    public Token readToken() throws SyntaxError, IOException {
        Token token = peekToken();
        tokenBuffer.removeFirst();
        return token;
    }

    /**
     * Peeks the next token without removing it from the stream.
     *
     * @return the next token in the buffer.
     * @throws SyntaxError if there is a syntax error.
     * @throws IOException if the read operation causes an IO error.
     */
    public Token peekToken() throws SyntaxError, IOException {
        // Reads from the token buffer before extracting characters from the stream
        if (!tokenBuffer.isEmpty()) {
            return tokenBuffer.peekFirst();
        }

        skipSpaces();

        // Check if the token is EOF
        Token token = getEOFToken();
        if (token != null) {
            tokenBuffer.addLast(token);
            return token;
        }
        // Check if the token consists of only alphanumerics or underscores
        token = getAlnumUnderscoreToken();
        if (token != null) {
            String tokenStr = token.getValue();
            SymbolInfo symbol = lexerTable.get(tokenStr);
            if (symbol == null) {
                // If the key cannot be found in the lexer table, it is a var
                token.setType(TokenType.VAR);
                tokenBuffer.addLast(token);
                return token;
            }
            // Check if the token is a keyword, if it is, change its token type
            if (symbol.getSymbolType() == SymbolType.KEYWORD) {
                token.setType(symbol.getTokenType());
                tokenBuffer.addLast(token);
                return token;
            }
            // Otherwise, the token must be a data type
            token.setType(symbol.getTokenType());
            tokenBuffer.addLast(token);
            return token;
        }
        // Check if the token is a scientific real number
        token = getScientificNumberToken();
        if (token != null) {
            tokenBuffer.addLast(token);
            return token;
        }
        // Check if the token is an operator
        token = getOperatorToken();
        if (token != null) {
            tokenBuffer.addLast(token);
            return token;
        }
        throw new SyntaxError("Unable to get next token because of invalid syntax at '" +
                (char) charBuffer.peek() + "'", getCurrentLine());
    }

    /**
     * Creates a token specifically for end-of-file mark.
     *
     * @return a token whose type is EOF.
     * @throws IOException if the read operation causes an error.
     */
    private Token getEOFToken() throws IOException {
        if (charBuffer.peek() != EOS) {
            return null;
        }
        return new Token(null, TokenType.EOF);
    }

    /**
     * Reads alphanumeric and underscore characters into a string and stores it in a token.
     *
     * @return a token containing a string of alphanumeric and underscore characters.
     * @throws IOException if the read operation causes an error.
     * @throws SyntaxError if there is an invalid character.
     */
    private Token getAlnumUnderscoreToken() throws IOException, SyntaxError {
        int c;

        // Check if the first character is end of stream or a letter or '_'
        if ((c = charBuffer.peek()) == EOS || (!Character.isAlphabetic(c) && c != '_')) {
            return null;
        }

        StringBuilder tokenStr = new StringBuilder();
        boolean end = false;

        // Consume the character from the stream until it is a separator or a valid special character
        while (!isSeparator(c) && !end) {
            if (isAlnumUnderscore(c)) {
                tokenStr.append((char) c);
                charBuffer.read();
            } else if (isSpecialChar(c)) {
                end = true;
            } else {
                throw new SyntaxError("Invalid character '" + c + "' after '" + tokenStr + "'", getCurrentLine());
            }
            c = charBuffer.peek();
        }

        // The string cannot be empty
        return new Token(tokenStr.toString(), TokenType.UNKNOWN, currLine);
    }

    /**
     * Reads an operator (can be multiple characters) and creates a token that stores the operator if the operation
     * succeeds.
     *
     * @return a token that stores the operator.
     * @throws IOException if the read operation causes an error.
     */
    private Token getOperatorToken() throws IOException {
        int c;
        StringBuilder tokenStr = new StringBuilder();
        String str;
        SymbolInfo tempSymbol, savedSymbol = null;
        boolean end = false;

        while ((c = charBuffer.peek()) != EOS && !end) {
            str = tokenStr.toString() + (char) c;
            // Find the token type corresponding to the string
            tempSymbol = lexerTable.get(str);
            end = tempSymbol == null;
            if (!end) {
                // Check if the retrieved token type is an operator
                end = tempSymbol.getSymbolType() != SymbolType.OPERATOR;
            }
            if (!end) {
                tokenStr.append((char) c);
                savedSymbol = tempSymbol;
                charBuffer.read();
            }
        }

        if (tokenStr.isEmpty()) {
            return null;
        }

        assert savedSymbol != null;
        return new Token(tokenStr.toString(), savedSymbol.getTokenType(), currLine);
    }

    /**
     * Reads a string and stores it in a new token if it matches the given string. Otherwise, it puts everything
     * that has been read back to the buffer.
     *
     * @param strToMatch the string to match.
     * @param tokenType  the type of the token to assign if one is present.
     * @return a token containing the string if the operation succeeds and null otherwise.
     * @throws IOException if there is an error while reading.
     */
    private Token getStrToken(String strToMatch, TokenType tokenType)
            throws IOException {
        int c;
        int i = 0;
        boolean end = false;
        StringBuilder buffer = new StringBuilder();

        while (i < strToMatch.length() && (c = charBuffer.peek()) != EOS && !end) {
            end = (char) c != strToMatch.charAt(i);
            if (!end) {
                buffer.append((char) c);
                ++i;
                charBuffer.read();
            }
        }

        if (!strToMatch.equals(buffer.toString())) {
            // Put back what has been read
            if (!buffer.isEmpty()) {
                charBuffer.putBack(buffer.toString());
            }
            return null;
        }

        return new Token(strToMatch, tokenType, currLine);
    }

    /**
     * Reads a sequence of digits, adds them to a string and creates a token that contains
     * the string if successful.
     *
     * @return a token containing a sequence of digits.
     * @throws IOException if the read operation causes an error.
     */
    private Token getDigitSeqToken() throws IOException {
        int c;
        StringBuilder tokenStr = new StringBuilder();

        while ((c = charBuffer.peek()) != EOS && Character.isDigit(c)) {
            tokenStr.append((char) c);
            charBuffer.read();
        }

        if (tokenStr.isEmpty()) {
            return null;
        }

        return new Token(tokenStr.toString(), TokenType.INT_LITERAL, currLine);
    }

    /**
     * Reads a floating-point number and stores it in a new token if one exists.
     *
     * @return a token containing the floating-point number.
     * @throws IOException if the read operation causes an error.
     */
    private Token getNumberToken() throws IOException {
        StringBuilder tokenStr = new StringBuilder();

        // Reads the integer part
        Token intToken = getDigitSeqToken();
        boolean missingInt = intToken == null;
        if (missingInt) {
            tokenStr.append("0");
        } else {
            tokenStr.append(intToken.getValue());
        }

        // Reads '.'
        Token decPointToken = getStrToken(".", TokenType.UNKNOWN);
        boolean missingDecPoint = decPointToken == null;
        if (!missingDecPoint) {
            tokenStr.append(".");
        }

        if (missingInt && missingDecPoint) {
            return null;
        }

        // Reads the fraction part if there is a decimal point
        if (!missingDecPoint) {
            Token fractionToken = getDigitSeqToken();
            if (fractionToken == null) {
                tokenStr.append("0");
            } else {
                tokenStr.append(fractionToken.getValue());
            }
        }

        TokenType tokenType = missingDecPoint ? TokenType.INT_LITERAL : TokenType.FLOAT_LITERAL;
        return new Token(tokenStr.toString(), tokenType, currLine);
    }

    /**
     * Reads a scientific floating-point number and stores it in a new token if one exists.
     *
     * @return a token containing the scientific floating-point number.
     * @throws SyntaxError if the numeric expression is invalid.
     * @throws IOException if the read operation causes an error.
     */
    private Token getScientificNumberToken() throws IOException, SyntaxError {
        skipSpaces();

        // Get a floating-point number
        Token tempToken = getNumberToken();
        if (tempToken == null) {
            return null;
        }

        int c;
        StringBuilder tokenStr = new StringBuilder();
        TokenType tokenType = tempToken.getType();

        tokenStr.append(tempToken.getValue());
        // Get 'e'
        tempToken = getStrToken("e", TokenType.UNKNOWN);
        if (tempToken == null) {
            if ((c = charBuffer.peek()) == EOS || isSpace(c) || isSpecialChar(c) && c != '.') {
                return new Token(tokenStr.toString(), tokenType, currLine);
            } else {
                throw new SyntaxError("Invalid numeric expression after '" + tokenStr + "'", getCurrentLine());
            }
        }
        tokenStr.append("e");

        // Get +/-
        tempToken = getStrToken("+", TokenType.UNKNOWN);
        if (tempToken == null) {
            // Reads '-' if '+' is not present
            tempToken = getStrToken("-", TokenType.UNKNOWN);
            if (tempToken != null) {
                tokenStr.append(tempToken.getValue());
            }
        } else {
            tokenStr.append(tempToken.getValue());
        }

        // Get the exponent
        tempToken = getNumberToken();
        if (tempToken == null) {
            throw new SyntaxError("Invalid numeric expression after '" + tokenStr + "'", getCurrentLine());
        }
        tokenStr.append(tempToken.getValue());
        return new Token(tokenStr.toString(), TokenType.FLOAT_LITERAL, currLine);
    }
}
