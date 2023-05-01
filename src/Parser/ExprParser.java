package Parser;

import Exceptions.SyntaxError;
import Lexer.Lexer;
import Nodes.DataTypeNode;
import Nodes.Node;
import Nodes.NodeType;
import Nodes.VarNode;
import Symbols.VarInfo;
import Tokens.Token;
import Tokens.TokenType;
import Types.TypeInfo;

import java.io.IOException;

public class ExprParser extends BaseParser {

    public ExprParser(Lexer lexer) {
        super(lexer);
    }

    // Parse general expressions

    /**
     * Parses an expression in a scope.
     *
     * @param scope the currently surrounding scope.
     * @return an AST node if an expression is parsed successfully, otherwise, null is returned.
     * @throws SyntaxError if there is a syntax error.
     * @throws IOException if there is an IO exception.
     */
    public Node parseExpr(Block scope) throws SyntaxError, IOException {
        Node root = parseExpr(scope, false);
        // Consume ';'
        parseTok(TokenType.SEMICOLON);
        return root;
    }

    /**
     * Parses an expression in a scope by indicating if it is inside a pair of parentheses.
     *
     * @param scope   the currently surrounding scope.
     * @param inParen true if the expression is inside parentheses and false otherwise.
     * @return an AST node if an expression is parsed successfully, otherwise, null is returned.
     * @throws SyntaxError if there is a syntax error.
     * @throws IOException if there is an IO exception.
     */
    private Node parseExpr(Block scope, boolean inParen) throws SyntaxError, IOException {
        return parseInfixExpr(scope, inParen);
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
        return new DataTypeNode(tok, NodeType.UNARY);
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
        return new DataTypeNode(tok, NodeType.UNARY);
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
        VarInfo varInfo = (VarInfo) scope.getSymbolTable().getSymbol(id);
        if (varInfo == null) {
            // If the ID is not found(not valid), throw an exception
            throw new SyntaxError("Invalid ID '" + id + "'", lexer.getCurrLine());
        }
        lexer.consume();
        TypeInfo dataType = varInfo.getDataType();
        boolean mutable = varInfo.isMutable();
        return new VarNode(tok, NodeType.TERMINAL, dataType, mutable);
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
        // Try mapping a literal to its data type
        TypeInfo dataType = typeTable.getType(tokType);
        if (dataType == null) {
            return null;
        }
        lexer.consume();
        return new DataTypeNode(tok, NodeType.TERMINAL, dataType);
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
        Node exprNode = parseExpr(scope, true);
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
     * @param scope   the currently surrounding scope.
     * @param inParen true if the expression is inside parentheses and false otherwise.
     * @return an AST node if an infix expression is parsed successfully, otherwise, null is returned.
     * @throws SyntaxError if there is a syntax error.
     * @throws IOException if there is an IO exception.
     */
    private Node parseInfixExpr(Block scope, boolean inParen) throws SyntaxError, IOException {
        return recurParseInfixExpr(scope, null, inParen);
    }

    /**
     * Recursively parses an infix expression using Pratt Parsing.
     *
     * @param scope   the currently surrounding scope.
     * @param prevOp  the previous left operator node.
     * @param inParen true if the expression is inside parentheses and false otherwise.
     * @return an AST node if an infix expression is parsed successfully, otherwise, null is returned.
     * @throws SyntaxError if there is a syntax error.
     * @throws IOException if there is an IO exception.
     */
    private Node recurParseInfixExpr(Block scope, Node prevOp, boolean inParen)
            throws SyntaxError, IOException {
        // First, fetch the left operand
        Node currLeft = parsePrefixExpr(scope);
        Token opTok;
        TokenType opType;
        Node right, newLeft;

        while (true) {
            opTok = lexer.lookahead();
            opType = opTok.getType();

            if (opType == TokenType.EOF) {
                if (inParen) {
                    throw new SyntaxError("Missing ')'", lexer.getCurrLine());
                } else {
                    throw new SyntaxError("Missing ';'", lexer.getCurrLine());
                }
            }

            if (opType == TokenType.SEMICOLON) {
                if (prevOp != null && currLeft == null) {
                    // Throw an exception for the case operand operator ;
                    throw new SyntaxError("Expected an operand after '" + prevOp.getTok().getValue() + "'",
                            lexer.getCurrLine());
                } else {
                    // Deal with the cases: ; or operand;
                    return currLeft;
                }
            }

            if (opType == TokenType.RPAREN) {
                // If the next token is ')', make sure the expression is in parentheses
                if (inParen) {
                    return currLeft;
                } else {
                    throw new SyntaxError("Unexpected ')'", lexer.getCurrLine());
                }
            }

            if (opTable.getId(opTok.getValue()) == null || !opTable.isInfix(opType)) {
                if (currLeft == null) {
                    // Undetected operator with no preceding operand
                    return currLeft;
                } else {
                    // If the next operator is neither valid nor infix and there is an operand, throw an exception
                    throw new SyntaxError("Expected an infix operator after '" + currLeft.getTok().getValue() + "'",
                            lexer.getCurrLine());
                }
            } else if (currLeft == null) {
                // Missing an operand before the infix operator
                throw new SyntaxError("Missing an operand before the operator '" + opTok.getValue() + "'",
                        lexer.getCurrLine());
            }

            // currLeft is assured to be non-null
            if (opType == TokenType.ASSIGNMENT) {
                if (currLeft.getTok().getType() != TokenType.ID) {
                    throw new SyntaxError("Expected a variable before '='", lexer.getCurrLine());
                }

                String id = currLeft.getTok().getValue();
                VarInfo varInfo = (VarInfo) scope.getSymbolTable().getSymbol(id);

                if (varInfo == null) {
                    // Variable is not valid, that is, it does not exist
                    throw new SyntaxError("Invalid variable '" + id + "'", lexer.getCurrLine());
                } else if (!varInfo.isMutable()) {
                    // Id is a constant so cannot be reassigned
                    throw new SyntaxError("'" + id + "' is a constant", lexer.getCurrLine());
                }
            }

            if (prevOp != null && opTable.cmpPreced(opType, prevOp.getTok().getType()) < 0) {
                // The current operator has lower precedence than the previous operator
                return currLeft;
            }

            lexer.consume();
            newLeft = new DataTypeNode(opTok, NodeType.BINARY);
            right = recurParseInfixExpr(scope, newLeft, inParen);
            newLeft.addChild(currLeft);
            newLeft.addChild(right);
            currLeft = newLeft;
        }
    }

}
