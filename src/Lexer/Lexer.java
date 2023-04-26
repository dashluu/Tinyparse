package Lexer;

import Exceptions.SyntaxError;
import Keywords.KeywordTable;
import Operators.OperatorTable;
import Tokens.Token;
import Tokens.TokenType;
import Types.TypeTable;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayDeque;

public class Lexer {
    private final CharBuffer charBuff;
    private final ArrayDeque<Token> tokBuff = new ArrayDeque<>();
    private final OperatorTable opTable = OperatorTable.getInstance();
    private final KeywordTable kwTable = KeywordTable.getInstance();
    private final TypeTable typeTable = TypeTable.getInstance();
    private final static String SPECIAL_CHARS = "()+-*/%~!&|<>=,.;:_";
    private final static int EOS = -1;
    private int currLine = 1;

    public Lexer(BufferedReader reader) {
        this.charBuff = new CharBuffer(reader);
    }

    /**
     * Gets the current line in the stream.
     *
     * @return the current line in the stream.
     */
    public int getCurrLine() {
        return currLine;
    }

    /**
     * Skips the spaces until a non-space character is encountered.
     *
     * @throws IOException if the read operation causes an IO error.
     */
    private void skipSpaces() throws IOException {
        int c;
        while ((c = charBuff.peek()) != EOS && isSpace(c)) {
            if (c == '\n') {
                ++currLine;
            }
            charBuff.read();
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
    private boolean isSep(int c) {
        return c == EOS || isSpace(c) || c == ';';
    }

    /**
     * Looks ahead to and removes the next token from the buffer.
     *
     * @return the next token in the buffer.
     * @throws SyntaxError if there is a syntax error.
     * @throws IOException if the read operation causes an IO error.
     */
    public Token consume() throws SyntaxError, IOException {
        Token tok = lookahead();
        tokBuff.removeFirst();
        return tok;
    }

    /**
     * Looks ahead to the next token without removing it from the stream.
     *
     * @return the next token in the buffer.
     * @throws SyntaxError if there is a syntax error.
     * @throws IOException if the read operation causes an IO error.
     */
    public Token lookahead() throws SyntaxError, IOException {
        // Reads from the token buffer before extracting characters from the stream
        if (!tokBuff.isEmpty()) {
            return tokBuff.peekFirst();
        }

        skipSpaces();

        // Check if the token is EOF
        Token tok = getEOF();
        if (tok != null) {
            tokBuff.addLast(tok);
            return tok;
        }
        // Check if the token consists of only alphanumerics or underscores
        tok = getAlnumUnderscore();
        if (tok != null) {
            String tokStr = tok.getValue();
            // Check if the token is a keyword, if it is, change its token type
            TokenType tokType = kwTable.getId(tokStr);
            if (tokType != null) {
                tok.setType(tokType);
                tokBuff.addLast(tok);
                return tok;
            }
            // Check if the token is a data type, if it is, change its token type
            if (typeTable.getType(tokStr) != null) {
                tok.setType(TokenType.TYPE_ID);
                tokBuff.addLast(tok);
                return tok;
            }
            // Otherwise, the token must be an ID
            tok.setType(TokenType.ID);
            tokBuff.addLast(tok);
            return tok;
        }
        // Check if the token is a scientific real number
        tok = getScientificNum();
        if (tok != null) {
            tokBuff.addLast(tok);
            return tok;
        }
        // Check if the token is an operator
        tok = getOp();
        if (tok != null) {
            tokBuff.addLast(tok);
            return tok;
        }
        throw new SyntaxError("Unable to get next token because of invalid syntax at '" +
                (char) charBuff.peek() + "'", getCurrLine());
    }

    /**
     * Creates a token specifically for end-of-file mark.
     *
     * @return a token whose type is EOF.
     * @throws IOException if the read operation causes an error.
     */
    private Token getEOF() throws IOException {
        if (charBuff.peek() != EOS) {
            return null;
        }
        return new Token(null, TokenType.EOF);
    }

    /**
     * Reads alphanumeric and underscore characters into a string and stores it in a token.
     * Grammar: ('_' | ('a'-'z') | ('A'-'Z'))('_' | ('a'-'z') | ('A'-'Z') | ('0'-'9'))*
     *
     * @return a token containing a string of alphanumeric and underscore characters.
     * @throws IOException if the read operation causes an error.
     * @throws SyntaxError if there is an invalid character.
     */
    private Token getAlnumUnderscore() throws IOException, SyntaxError {
        int c;

        // Check if the first character is end of stream or a letter or '_'
        if ((c = charBuff.peek()) == EOS || (!Character.isAlphabetic(c) && c != '_')) {
            return null;
        }

        StringBuilder tokStr = new StringBuilder();
        boolean end = false;

        // Consume the character from the stream until it is a separator or a valid special character
        while (!isSep(c) && !end) {
            if (isAlnumUnderscore(c)) {
                tokStr.append((char) c);
                charBuff.read();
            } else if (isSpecialChar(c)) {
                end = true;
            } else {
                throw new SyntaxError("Invalid character '" + c + "' after '" + tokStr + "'", getCurrLine());
            }
            c = charBuff.peek();
        }

        // The string cannot be empty
        return new Token(tokStr.toString(), TokenType.UNKNOWN, currLine);
    }

    /**
     * Reads an operator (can be multiple characters) and creates a token that stores the operator if the operation
     * succeeds.
     *
     * @return a token that stores the operator.
     * @throws IOException if the read operation causes an error.
     */
    private Token getOp() throws IOException {
        int c;
        StringBuilder tokStr = new StringBuilder();
        String tmpStr;
        TokenType tmpTokType, opId = TokenType.UNKNOWN;
        boolean end = false;

        while ((c = charBuff.peek()) != EOS && !end) {
            tmpStr = tokStr.toString() + (char) c;
            // Find the token type corresponding to the string
            tmpTokType = opTable.getId(tmpStr);
            end = tmpTokType == null;
            if (!end) {
                tokStr.append((char) c);
                opId = tmpTokType;
                charBuff.read();
            }
        }
        if (tokStr.isEmpty()) {
            return null;
        }
        return new Token(tokStr.toString(), opId, currLine);
    }

    /**
     * Reads a string and stores it in a new token if it matches the given string. Otherwise, it puts everything
     * that has been read back to the buffer.
     *
     * @param strToMatch the string to match.
     * @param tokType    the type of the token to assign if one is present.
     * @return a token containing the string if the operation succeeds and null otherwise.
     * @throws IOException if there is an error while reading.
     */
    private Token getStrTok(String strToMatch, TokenType tokType) throws IOException {
        int c;
        int i = 0;
        boolean end = false;
        StringBuilder buffer = new StringBuilder();

        while (i < strToMatch.length() && (c = charBuff.peek()) != EOS && !end) {
            end = (char) c != strToMatch.charAt(i);
            if (!end) {
                buffer.append((char) c);
                ++i;
                charBuff.read();
            }
        }

        if (!strToMatch.contentEquals(buffer)) {
            // Put back what has been read
            if (!buffer.isEmpty()) {
                charBuff.putBack(buffer.toString());
            }
            return null;
        }

        return new Token(strToMatch, tokType, currLine);
    }

    /**
     * Reads a sequence of digits, adds them to a string and creates a token that contains
     * the string if successful.
     * Grammar: ('0'-'9')+
     *
     * @return a token containing a sequence of digits.
     * @throws IOException if the read operation causes an error.
     */
    private Token getDigits() throws IOException {
        int c;
        StringBuilder tokStr = new StringBuilder();

        while ((c = charBuff.peek()) != EOS && Character.isDigit(c)) {
            tokStr.append((char) c);
            charBuff.read();
        }

        if (tokStr.isEmpty()) {
            return null;
        }

        return new Token(tokStr.toString(), TokenType.INT_LITERAL, currLine);
    }

    /**
     * Reads a floating-point number and stores it in a new token if one exists.
     * Grammar: ('0'-'9')+ | ('0'-'9')*'.'('0'-'9')*
     *
     * @return a token containing the floating-point number.
     * @throws IOException if the read operation causes an error.
     */
    private Token getNum() throws IOException {
        StringBuilder tokStr = new StringBuilder();

        // Reads the integer part
        Token intTok = getDigits();
        boolean missingInt = intTok == null;
        if (missingInt) {
            tokStr.append("0");
        } else {
            tokStr.append(intTok.getValue());
        }

        // Reads '.'
        Token decPtTok = getStrTok(".", TokenType.UNKNOWN);
        boolean missingDecPt = decPtTok == null;
        if (!missingDecPt) {
            tokStr.append(".");
        }

        if (missingInt && missingDecPt) {
            return null;
        }

        // Reads the fraction part if there is a decimal point
        if (!missingDecPt) {
            Token fracTok = getDigits();
            if (fracTok == null) {
                tokStr.append("0");
            } else {
                tokStr.append(fracTok.getValue());
            }
        }

        TokenType tokType = missingDecPt ? TokenType.INT_LITERAL : TokenType.FLOAT_LITERAL;
        return new Token(tokStr.toString(), tokType, currLine);
    }

    /**
     * Reads a scientific floating-point number and stores it in a new token if one exists.
     * Grammar: (('0'-'9')+ | ('0'-'9')*'.'('0'-'9')*)('.' 'e' ('+' | '-')?(('0'-'9')+ | ('0'-'9')*'.'('0'-'9')*))?
     *
     * @return a token containing the scientific floating-point number.
     * @throws SyntaxError if the numeric expression is invalid.
     * @throws IOException if the read operation causes an error.
     */
    private Token getScientificNum() throws IOException, SyntaxError {
        skipSpaces();

        // Get a floating-point number
        Token tmpTok = getNum();
        if (tmpTok == null) {
            return null;
        }

        int c;
        StringBuilder tokStr = new StringBuilder();
        TokenType tokType = tmpTok.getType();

        tokStr.append(tmpTok.getValue());
        // Get 'e'
        tmpTok = getStrTok("e", TokenType.UNKNOWN);
        if (tmpTok == null) {
            if ((c = charBuff.peek()) == EOS || isSpace(c) || isSpecialChar(c) && c != '.') {
                return new Token(tokStr.toString(), tokType, currLine);
            } else {
                throw new SyntaxError("Invalid numeric expression after '" + tokStr + "'", getCurrLine());
            }
        }
        tokStr.append("e");

        // Get +/-
        tmpTok = getStrTok("+", TokenType.UNKNOWN);
        if (tmpTok == null) {
            // Reads '-' if '+' is not present
            tmpTok = getStrTok("-", TokenType.UNKNOWN);
            if (tmpTok != null) {
                tokStr.append(tmpTok.getValue());
            }
        } else {
            tokStr.append(tmpTok.getValue());
        }

        // Get the exponent
        tmpTok = getNum();
        if (tmpTok == null) {
            throw new SyntaxError("Invalid numeric expression after '" + tokStr + "'", getCurrLine());
        }
        tokStr.append(tmpTok.getValue());
        return new Token(tokStr.toString(), TokenType.FLOAT_LITERAL, currLine);
    }
}
