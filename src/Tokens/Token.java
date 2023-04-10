package Tokens;

public class Token {
    private final String value;
    private TokenType type;
    private final int lineNum;

    public Token(String value, TokenType type, int lineNum) {
        this.value = value;
        this.type = type;
        this.lineNum = lineNum;
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

    public int getLineNum() {
        return lineNum;
    }

    public void setType(TokenType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Token: " + value + ", Token type: " + type + ", Line number: " + lineNum;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Token token)) {
            return false;
        }
        return value.equals(token.getValue()) && type == token.type;
    }
}
