package Global;

public class Token {
    private final String value;
    private TokenType type;
    private final int lineNumber;

    public Token(String value, TokenType type, int lineNumber) {
        this.value = value;
        this.type = type;
        this.lineNumber = lineNumber;
    }

    public Token(String value, TokenType type) {
        this(value, type, 1);
    }

    public Token(String value) {
        this(value, TokenType.UNKNOWN, 1);
    }

    public String getValue() {
        return value;
    }

    public TokenType getType() {
        return type;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public void setType(TokenType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Token: " + value + ", Token type: " + type + ", Line number: " + lineNumber;
    }
}
