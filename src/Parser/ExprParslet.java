package Parser;

import Exceptions.SyntaxError;
import Lexer.Lexer;
import Symbols.SymbolTable;
import Tokens.Token;
import Tokens.TokenType;

import java.io.IOException;

public class ExprParslet {

    private final Lexer lexer;

    public ExprParslet(Lexer lexer) {
        this.lexer = lexer;
    }

    /**
     * Parses non-terminal primary block in the grammar specifications.
     *
     * @param scope the currently surrounding scope.
     * @return an AST Node if successful and null otherwise.
     * @throws SyntaxError if there is a syntax error.
     * @throws IOException if there is an IO exception.
     */
    private Node parsePrimary(Block scope) throws SyntaxError, IOException {
        Token token = lexer.peekToken();
        switch (token.getTokenType()) {
            case INT_LITERAL, FLOAT_LITERAL -> {
                return new Node(token);
            }
            case ID -> {
                SymbolTable symbolTable = scope.getSymbolTable();
                String tokenStr = token.getValue();
                if (symbolTable.get(tokenStr) == null) {
                    throw new SyntaxError("Invalid variable '" + tokenStr + "'", lexer.getCurrentLine());
                }
                return new Node(token);
            }
            case LPAREN -> {
                Node exprNode = parseExpr();
                if (!rightParen()) {
                    throw new SyntaxError("Missing ')'", lexer.getCurrentLine());
                }
                return exprNode;
            }
            default -> {
                return null;
            }
        }
    }

    /**
     * Consumes the next token if it is '('.
     *
     * @return true if the next token is '(' and false otherwise.
     * @throws SyntaxError if there is a syntax error.
     * @throws IOException if there is an IO exception.
     */
    private boolean leftParen() throws SyntaxError, IOException {
        Token token = lexer.peekToken();
        if (token.getTokenType() != TokenType.LPAREN) {
            return false;
        }
        lexer.readToken();
        return true;
    }

    /**
     * Consumes the next token if it is ')'.
     *
     * @return true if the next token is ')' and false otherwise.
     * @throws SyntaxError if there is a syntax error.
     * @throws IOException if there is an IO exception.
     */
    private boolean rightParen() throws SyntaxError, IOException {
        Token token = lexer.peekToken();
        if (token.getTokenType() != TokenType.RPAREN) {
            return false;
        }
        lexer.readToken();
        return true;
    }

    private Node parseExpr() {
        return null;
    }
}
