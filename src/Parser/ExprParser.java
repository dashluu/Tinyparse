package Parser;

import Exceptions.SyntaxError;
import Lexer.Lexer;
import Reserved.OperatorTable;
import Reserved.ReservedInfo;
import Reserved.ReservedTable;
import Reserved.ReservedType;
import Symbols.SymbolInfo;
import Tokens.Token;
import Tokens.TokenType;

import java.io.IOException;

public class ExprParser {
    private final Lexer lexer;
    private final ReservedTable reservedTable = ReservedTable.getInstance();
    private final OperatorTable opTable = OperatorTable.getInstance();

    public ExprParser(Lexer lexer) {
        this.lexer = lexer;
    }

    // Parse general expressions

    /**
     * Parses an expression.
     *
     * @param scope the currently surrounding scope.
     * @return an AST node if an expression is parsed successfully, otherwise, null is returned.
     * @throws SyntaxError if there is a syntax error.
     * @throws IOException if there is an IO exception.
     */
    public Node parseExpr(Block scope) throws SyntaxError, IOException {
        return parseInfixExpr(scope);
    }

    // Helper and utility methods

    /**
     * Consumes the next token if its type is as expected.
     *
     * @param expected the token type to be expected.
     * @return true if the next token's type is as expected and false otherwise.
     * @throws SyntaxError if there is a syntax error.
     * @throws IOException if there is an IO exception.
     */
    private boolean parseToken(TokenType expected) throws SyntaxError, IOException {
        TokenType actual = lexer.lookahead().getType();
        if (actual != expected) {
            return false;
        }
        lexer.consume();
        return true;
    }

    /**
     * Consumes a prefix operator.
     *
     * @return an AST Node if a prefix operator is present and null otherwise.
     * @throws SyntaxError if there is a syntax error.
     * @throws IOException if there is an IO exception.
     */
    private Node parsePrefixOperator() throws SyntaxError, IOException {
        Token token = lexer.lookahead();
        TokenType tokenType = token.getType();
        if (!opTable.isPrefix(tokenType)) {
            return null;
        }
        lexer.consume();
        return new Node(token, NodeType.UNARY);
    }

    /**
     * Consumes a postfix operator.
     *
     * @return an AST Node if a postfix operator is present and null otherwise.
     * @throws SyntaxError if there is a syntax error.
     * @throws IOException if there is an IO exception.
     */
    private Node parsePostfixOperator() throws SyntaxError, IOException {
        Token token = lexer.lookahead();
        TokenType tokenType = token.getType();
        if (!opTable.isPostfix(tokenType)) {
            return null;
        }
        lexer.consume();
        return new Node(token, NodeType.UNARY);
    }

    // Primary expressions

    /**
     * Parses a primary expression.
     * Grammar:
     * primary-expression: identifier | literal-expression | parenthesized-expression
     *
     * @param scope the currently surrounding scope.
     * @return an AST node associated with the primary expression.
     * @throws SyntaxError if there is a syntax error.
     * @throws IOException if there is an IO exception.
     */
    private Node parsePrimaryExpr(Block scope) throws SyntaxError, IOException {
        Node node = parseId(scope);
        if (node != null) {
            return node;
        }
        node = parseLiteral();
        if (node != null) {
            return node;
        }
        return parseParenthesizedExpr(scope);
    }

    /**
     * Parses an ID token.
     *
     * @param scope the currently surrounding scope.
     * @return an AST node that contains the ID token if one exists, otherwise, null is returned.
     * @throws SyntaxError if there is a syntax error.
     * @throws IOException if there is an IO exception.
     */
    private Node parseId(Block scope) throws SyntaxError, IOException {
        Token token = lexer.lookahead();
        if (token.getType() != TokenType.ID) {
            // If the token is not an ID, return
            return null;
        }
        String id = token.getValue();
        SymbolInfo symbolInfo = scope.getSymbolTable().get(id);
        if (symbolInfo == null) {
            // If the ID is not found(not valid), throw an exception
            throw new SyntaxError("Invalid ID '" + id + "'", lexer.getCurrLine());
        }
        return new Node(token, NodeType.TERM);
    }

    /**
     * Parses a literal expression.
     *
     * @return an AST node that contains the literal token if successful and null otherwise.
     * @throws SyntaxError if there is a syntax error.
     * @throws IOException if there is an IO exception.
     */
    private Node parseLiteral() throws SyntaxError, IOException {
        Token token = lexer.lookahead();
        TokenType tokenType = token.getType();
        if (tokenType != TokenType.BOOL_LITERAL &&
                tokenType != TokenType.INT_LITERAL &&
                tokenType != TokenType.FLOAT_LITERAL) {
            return null;
        }
        lexer.consume();
        return new Node(token, NodeType.TERM);
    }

    /**
     * Parses a parenthesized expression.
     * Grammar:
     * parenthesized-expression: '(' expression ')'
     *
     * @param scope the currently surrounding scope.
     * @return an AST node associated with the expression inside the parentheses if successful and null otherwise.
     * @throws SyntaxError if there is a syntax error.
     * @throws IOException if there is an IO exception.
     */
    private Node parseParenthesizedExpr(Block scope) throws SyntaxError, IOException {
        if (!parseToken(TokenType.LPAREN)) {
            return null;
        }
        lexer.consume();
        Node exprNode = parseExpr(scope);
        if (!parseToken(TokenType.RPAREN)) {
            throw new SyntaxError("Missing ')'", lexer.getCurrLine());
        }
        return exprNode;
    }

    // Prefix expression

    /**
     * Parses a prefix expression.
     * Grammar:
     * prefix-expression: prefix-operator* postfix-expression
     *
     * @param scope the currently surrounding scope.
     * @return an AST node if the prefix expression is parsed successfully, otherwise, return null.
     * @throws SyntaxError if there is a syntax error.
     * @throws IOException if there is an IO exception.
     */
    private Node parsePrefixExpr(Block scope) throws SyntaxError, IOException {
        Node root = parsePrefixOperator();

        // There can be zero or many prefix operators
        Node parentOpNode = root;
        Node childOpNode;
        if (parentOpNode != null) {
            while ((childOpNode = parsePrefixOperator()) != null) {
                parentOpNode.addChild(childOpNode);
                parentOpNode = childOpNode;
            }
        }

        Node postfixExprNode = parsePostfixExpr(scope);
        if (parentOpNode == null) {
            // If there are no prefix operators, the root of the tree is the postfix expression
            // Note that the postfix expression does or does not exist
            return postfixExprNode;
        }
        if (postfixExprNode == null) {
            // If there is at least one prefix operator but no postfix expression,
            // throw a syntax error
            throw new SyntaxError("Expected an expression following the prefix operator",
                    lexer.getCurrLine());
        }
        parentOpNode.addChild(postfixExprNode);
        return root;
    }

    // Postfix expressions

    /**
     * Parses a postfix expression.
     * Grammar:
     * postfix-expression: primary-expression postfix-operator
     *
     * @param scope the currently surrounding scope.
     * @return an AST node if the postfix expression is parsed successfully, otherwise, return null.
     * @throws SyntaxError if there is a syntax error.
     * @throws IOException if there is an IO exception.
     */
    private Node parsePostfixExpr(Block scope) throws SyntaxError, IOException {
        Node primaryExprNode = parsePrimaryExpr(scope);
        if (primaryExprNode == null) {
            return null;
        }
        Node postfixOpNode = parsePostfixOperator();
        if (postfixOpNode == null) {
            return primaryExprNode;
        }
        postfixOpNode.addChild(primaryExprNode);
        return postfixOpNode;
    }

    // Infix expressions

    /**
     * Parses an infix expression.
     * Grammar:
     * infix-expression: prefix-expression infix-operator infix-expression
     *
     * @param scope the currently surrounding scope.
     * @return an AST node if an infix expression is parsed successfully, otherwise, null is returned.
     * @throws SyntaxError if there is a syntax error.
     * @throws IOException if there is an IO exception.
     */
    private Node parseInfixExpr(Block scope) throws SyntaxError, IOException {
        return recurParseInfixExpr(scope, null, TokenType.UNKNOWN);
    }

    /**
     * Recursively parses an infix expression using Pratt Parsing.
     *
     * @param scope      the currently surrounding scope.
     * @param prevLeft   the previous left operand node.
     * @param prevOpType the previous operator's token type.
     * @return an AST node if an infix expression is parsed successfully, otherwise, null is returned.
     * @throws SyntaxError if there is a syntax error.
     * @throws IOException if there is an IO exception.
     */
    private Node recurParseInfixExpr(Block scope, Node prevLeft, TokenType prevOpType)
            throws SyntaxError, IOException {
        // First, fetch the left operand
        Node currLeft = parsePrefixExpr(scope);
        Token opToken;
        TokenType opType;
        Node right, newLeft;
        ReservedInfo reservedInfo;

        while (true) {
            opToken = lexer.lookahead();
            opType = opToken.getType();
            if (opType == TokenType.EOF) {
                throw new SyntaxError("Missing delimiter after the expression", lexer.getCurrLine());
            }
            if (opType == TokenType.SEMICOLON) {
                if (prevLeft == null || currLeft != null) {
                    // Deal with the cases: ; or operand;
                    return currLeft;
                } else {
                    throw new SyntaxError("Expected an operand after '" + prevLeft.getToken().getValue() + "'",
                            lexer.getCurrLine());
                }
            }
            // Check if the next token is an operator
            reservedInfo = reservedTable.getReservedInfo(opType);
            if (reservedInfo == null || reservedInfo.getType() != ReservedType.OPERATOR || !opTable.isInfix(opType)) {
                // If it is neither an infix operator nor ';', throw an exception
                throw new SyntaxError("Expected an infix operator or ';' after '" + currLeft.getToken().getValue() + "'",
                        lexer.getCurrLine());
            }
            if (prevOpType != TokenType.UNKNOWN && opTable.cmpPreced(opType, prevOpType) < 0) {
                return currLeft;
            }
            lexer.consume();
            newLeft = new Node(opToken, NodeType.BINARY);
            right = recurParseInfixExpr(scope, newLeft, opType);
            newLeft.addChild(currLeft);
            newLeft.addChild(right);
            currLeft = newLeft;
        }
    }

}
