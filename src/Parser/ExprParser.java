package Parser;

import Exceptions.SyntaxError;
import Lexer.Lexer;
import Reserved.OperatorTable;
import Reserved.ReservedInfo;
import Reserved.ReservedTable;
import Reserved.ReservedType;
import Tokens.Token;
import Tokens.TokenType;

import java.io.IOException;

public class ExprParser {
    private final Lexer lexer;
    private final ReservedTable reservedTable = ReservedTable.getInstance();
    private final OperatorTable operatorTable = OperatorTable.getInstance();

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
        if (!operatorTable.isPrefix(tokenType)) {
            return null;
        }
        lexer.consume();
        return new Node(token);
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
        if (!operatorTable.isPostfix(tokenType)) {
            return null;
        }
        lexer.consume();
        return new Node(token);
    }

    // Primary expressions

    /**
     * Parses a primary expression.
     * Grammar:
     * primary-expression: literal-expression | parenthesized-expression
     *
     * @param scope the currently surrounding scope.
     * @return an AST node associated with the primary expression.
     * @throws SyntaxError if there is a syntax error.
     * @throws IOException if there is an IO exception.
     */
    private Node parsePrimaryExpr(Block scope) throws SyntaxError, IOException {
        Node node = parseLiteral();
        return node != null ? node : parseParenthesizedExpr(scope);
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
        return new Node(token);
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
        return recurParseInfixExpr(scope, null, 0);
    }

    /**
     * Recursively parses an infix expression using Pratt Parsing.
     *
     * @param scope      the currently surrounding scope.
     * @param prevLeft   the previous left operand node.
     * @param prevPreced the precedence of the previous operator.
     * @return an AST node if an infix expression is parsed successfully, otherwise, null is returned.
     * @throws SyntaxError if there is a syntax error.
     * @throws IOException if there is an IO exception.
     */
    private Node recurParseInfixExpr(Block scope, Node prevLeft, int prevPreced) throws SyntaxError, IOException {
        // First, fetch the left operand
        Node currLeft = parsePrefixExpr(scope);
        // Look ahead for an operator
        Token opToken = lexer.lookahead();
        TokenType opTokenType = opToken.getType();
        // Check if the next token is ';'
        if (opTokenType == TokenType.SEMICOLON) {
            if (prevLeft == null || currLeft != null) {
                return currLeft;
            } else {
                throw new SyntaxError("Expected an operand after '" + opToken.getValue() + "'",
                        lexer.getCurrLine());
            }
        }
        // Check if the next token is an operator
        ReservedInfo reservedInfo = reservedTable.getReservedInfo(opTokenType);
        if (reservedInfo == null ||
                reservedInfo.getType() != ReservedType.OPERATOR ||
                !operatorTable.isInfix(opTokenType)) {
            // If it is neither an infix operator nor ';', throw an exception
            throw new SyntaxError("Expected an infix operator or ';' after '" + currLeft.getToken().getValue() + "'",
                    lexer.getCurrLine());
        }

        // Consume the current token after peeking it
        lexer.consume();
        Node right, newLeft;
        Token nextToken;
        int currPreced;

        while (operatorTable.getPreced(opTokenType) > prevPreced) {
            // Get the precedence of the current operator
            currPreced = operatorTable.getPreced(opTokenType);
            // Recursively parses whatever is left
            right = recurParseInfixExpr(scope, currLeft, currPreced);
            // Construct a subtree whose root is the current operator and the children are the left operand node
            // and the right subtree
            newLeft = new Node(opToken);
            newLeft.addChild(currLeft);
            newLeft.addChild(right);
            currLeft = newLeft;
            // Look ahead
            nextToken = lexer.lookahead();
            // Check if there is a next token, if not, return immediately
            if (nextToken.getType() == TokenType.SEMICOLON) {
                return currLeft;
            }
        }

        return currLeft;
    }

}
