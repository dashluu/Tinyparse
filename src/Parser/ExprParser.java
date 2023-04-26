package Parser;

import Exceptions.SyntaxError;
import Lexer.Lexer;
import Symbols.SymbolInfo;
import Tokens.Token;
import Tokens.TokenType;

import java.io.IOException;

public class ExprParser extends BaseParser {

    public ExprParser(Lexer lexer) {
        super(lexer);
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
     * Consumes a prefix operator.
     *
     * @return an AST Node if a prefix operator is present and null otherwise.
     * @throws SyntaxError if there is a syntax error.
     * @throws IOException if there is an IO exception.
     */
    private Node parsePrefixOp() throws SyntaxError, IOException {
        Token tok = lexer.lookahead();
        TokenType tokType = tok.getType();
        if (!opTable.isPrefix(tokType)) {
            return null;
        }
        lexer.consume();
        return new Node(tok, NodeType.UNARY);
    }

    /**
     * Consumes a postfix operator.
     *
     * @return an AST Node if a postfix operator is present and null otherwise.
     * @throws SyntaxError if there is a syntax error.
     * @throws IOException if there is an IO exception.
     */
    private Node parsePostfixOp() throws SyntaxError, IOException {
        Token tok = lexer.lookahead();
        TokenType tokType = tok.getType();
        if (!opTable.isPostfix(tokType)) {
            return null;
        }
        lexer.consume();
        return new Node(tok, NodeType.UNARY);
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
        return parseParenExpr(scope);
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
        Token tok = lexer.lookahead();
        if (tok.getType() != TokenType.ID) {
            // If the token is not an ID, return
            return null;
        }
        String id = tok.getValue();
        SymbolInfo symbolInfo = scope.getSymbolTable().getSymbol(id);
        if (symbolInfo == null) {
            // If the ID is not found(not valid), throw an exception
            throw new SyntaxError("Invalid ID '" + id + "'", lexer.getCurrLine());
        }
        return new Node(tok, NodeType.TERMINAL);
    }

    /**
     * Parses a literal expression.
     *
     * @return an AST node that contains the literal token if successful and null otherwise.
     * @throws SyntaxError if there is a syntax error.
     * @throws IOException if there is an IO exception.
     */
    private Node parseLiteral() throws SyntaxError, IOException {
        Token tok = lexer.lookahead();
        TokenType tokType = tok.getType();
        if (tokType != TokenType.BOOL_LITERAL &&
                tokType != TokenType.INT_LITERAL &&
                tokType != TokenType.FLOAT_LITERAL) {
            return null;
        }
        lexer.consume();
        return new Node(tok, NodeType.TERMINAL);
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
    private Node parseParenExpr(Block scope) throws SyntaxError, IOException {
        if (parseTok(TokenType.LPAREN) == null) {
            return null;
        }
        lexer.consume();
        Node exprNode = parseExpr(scope);
        if (parseTok(TokenType.RPAREN) == null) {
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
        Node root = parsePrefixOp();

        // There can be zero or many prefix operators
        Node parentOpNode = root;
        Node childOpNode;
        if (parentOpNode != null) {
            while ((childOpNode = parsePrefixOp()) != null) {
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
        Node postfixOpNode = parsePostfixOp();
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
        Token opTok;
        TokenType opType;
        Node right, newLeft;

        while (true) {
            opTok = lexer.lookahead();
            opType = opTok.getType();
            // Check if the next token is ';'
            if (opType == TokenType.SEMICOLON) {
                if (prevLeft == null || currLeft != null) {
                    // Deal with the cases: ; or operand;
                    return currLeft;
                } else {
                    // Throw an exception for the case operand operator ;
                    throw new SyntaxError("Expected an operand after '" + prevLeft.getTok().getValue() + "'",
                            lexer.getCurrLine());
                }
            }
            // Check if the next token is an infix operator
            if (opTable.getId(opTok.getValue()) == null || !opTable.isInfix(opType)) {
                throw new SyntaxError("Expected an infix operator or ';' after '" + currLeft.getTok().getValue() + "'",
                        lexer.getCurrLine());
            }
            if (prevOpType != TokenType.UNKNOWN && opTable.cmpPreced(opType, prevOpType) < 0) {
                return currLeft;
            }
            lexer.consume();
            newLeft = new Node(opTok, NodeType.BINARY);
            right = recurParseInfixExpr(scope, newLeft, opType);
            newLeft.addChild(currLeft);
            newLeft.addChild(right);
            currLeft = newLeft;
        }
    }

}
