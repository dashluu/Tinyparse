package Parser;

import Exceptions.SyntaxError;
import Lexer.Lexer;
import Nodes.*;
import Operators.BinaryOperatorCompat;
import Operators.OperatorCompat;
import Operators.UnaryOperatorCompat;
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
     * Parses an expression and analyzes its semantics in a scope.
     *
     * @param scope the currently surrounding scope.
     * @return an AST node if an expression is parsed successfully, otherwise, null is returned.
     * @throws SyntaxError if there is a syntax error.
     * @throws IOException if there is an IO exception.
     */
    public DataTypeNode parseExpr(Block scope) throws SyntaxError, IOException {
        DataTypeNode root = parseExpr(scope, false);
        // Consume ';'
        parseTok(TokenType.SEMICOLON);
        analyzeSemantics(root);
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
    private DataTypeNode parseExpr(Block scope, boolean inParen) throws SyntaxError, IOException {
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
    private UnaryNode parsePrefixOp() throws SyntaxError, IOException {
        Token tok = lexer.lookahead();
        TokenType tokType = tok.getType();
        if (!opTable.isPrefix(tokType)) {
            return null;
        }
        lexer.consume();
        return new UnaryNode(tok, NodeType.UNARY_OP, null);
    }

    /**
     * Consumes a postfix operator.
     *
     * @return an AST Node if a postfix operator is present and null otherwise.
     * @throws SyntaxError if there is a syntax error.
     * @throws IOException if there is an IO exception.
     */
    private UnaryNode parsePostfixOp() throws SyntaxError, IOException {
        Token tok = lexer.lookahead();
        TokenType tokType = tok.getType();
        if (!opTable.isPostfix(tokType)) {
            return null;
        }
        lexer.consume();
        return new UnaryNode(tok, NodeType.UNARY_OP, null);
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
    private DataTypeNode parsePrimaryExpr(Block scope) throws SyntaxError, IOException {
        DataTypeNode node = parseId(scope);
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
    private DataTypeNode parseId(Block scope) throws SyntaxError, IOException {
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
    private DataTypeNode parseLiteral() throws SyntaxError, IOException {
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
    private DataTypeNode parseParenExpr(Block scope) throws SyntaxError, IOException {
        if (parseTok(TokenType.LPAREN) == null) {
            return null;
        }
        DataTypeNode exprNode = parseExpr(scope, true);
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
    private DataTypeNode parsePrefixExpr(Block scope) throws SyntaxError, IOException {
        UnaryNode root = parsePrefixOp();

        // There can be zero or many prefix operators
        UnaryNode parentOpNode = root;
        UnaryNode childOpNode;
        if (parentOpNode != null) {
            while ((childOpNode = parsePrefixOp()) != null) {
                parentOpNode.setChild(childOpNode);
                parentOpNode = childOpNode;
            }
        }

        DataTypeNode postfixExprNode = parsePostfixExpr(scope);
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
        parentOpNode.setChild(postfixExprNode);
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
    private DataTypeNode parsePostfixExpr(Block scope) throws SyntaxError, IOException {
        DataTypeNode primaryExprNode = parsePrimaryExpr(scope);
        if (primaryExprNode == null) {
            return null;
        }
        UnaryNode postfixOpNode = parsePostfixOp();
        if (postfixOpNode == null) {
            return primaryExprNode;
        }
        postfixOpNode.setChild(primaryExprNode);
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
    private DataTypeNode parseInfixExpr(Block scope, boolean inParen) throws SyntaxError, IOException {
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
    private DataTypeNode recurParseInfixExpr(Block scope, Node prevOp, boolean inParen)
            throws SyntaxError, IOException {
        // First, fetch the left operand
        DataTypeNode currLeft = parsePrefixExpr(scope);
        Token opTok;
        TokenType opType;
        DataTypeNode right;
        BinaryNode newLeft;

        while (true) {
            opTok = lexer.lookahead();
            opType = opTok.getType();

            if (opType == TokenType.EOF) {
                if (prevOp != null) {
                    if (currLeft == null) {
                        // Missing an operand after an operator
                        throw new SyntaxError("Expected an operand after '" + prevOp.getTok().getValue() + "'",
                                lexer.getCurrLine());
                    } else if (inParen) {
                        // The expression ends without ')'
                        throw new SyntaxError("Missing ')'", lexer.getCurrLine());
                    } else {
                        // An operand exists but the expression ends without ';'
                        throw new SyntaxError("Missing ';'", lexer.getCurrLine());
                    }
                } else {
                    if (currLeft == null) {
                        // It can be an empty line or other type of statement
                        return null;
                    } else if (inParen) {
                        // The expression ends without ')'
                        throw new SyntaxError("Missing ')'", lexer.getCurrLine());
                    } else {
                        // An operand exists but the expression ends without ';'
                        throw new SyntaxError("Missing ';'", lexer.getCurrLine());
                    }
                }
            }

            if (opType == TokenType.SEMICOLON) {
                if (prevOp != null) {
                    if (currLeft == null) {
                        // Missing an operand after an operator
                        throw new SyntaxError("Expected an operand after '" + prevOp.getTok().getValue() + "'",
                                lexer.getCurrLine());
                    } else if (inParen) {
                        // The expression ends without ')'
                        throw new SyntaxError("Missing ')'", lexer.getCurrLine());
                    } else {
                        // Deal with the case '... operator operand;'
                        return currLeft;
                    }
                } else {
                    if (currLeft != null) {
                        // Deal with the case 'operand;'
                        return currLeft;
                    } else if (inParen) {
                        // The expression ends without ')'
                        throw new SyntaxError("Missing ')'", lexer.getCurrLine());
                    } else {
                        // Return a node representing an empty statement(sentinel), or the case ';'
                        return new DataTypeNode(null, NodeType.EMPTY, null);
                    }
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
                    return null;
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
            newLeft = new BinaryNode(opTok, NodeType.BINARY_OP, null);
            right = recurParseInfixExpr(scope, newLeft, inParen);
            newLeft.setLeft(currLeft);
            newLeft.setRight(right);
            currLeft = newLeft;
        }
    }

    /**
     * Analyzes the semantics of an expression.
     *
     * @param root the expression's AST root.
     * @throws SyntaxError if there is a syntax error.
     */
    private void analyzeSemantics(DataTypeNode root) throws SyntaxError {
        if (root == null || root.getType() == NodeType.TERMINAL || root.getType() == NodeType.EMPTY) {
            return;
        }

        TypeInfo resultDataType;
        Token op = root.getTok();
        TokenType opId = op.getType();
        OperatorCompat opCompat;

        if (root.getType() == NodeType.UNARY_OP) {
            UnaryNode unaryNode = (UnaryNode) root;
            DataTypeNode childNode = unaryNode.getChild();

            // Recursively analyze the semantics of the child node
            analyzeSemantics(childNode);

            // Get the operand's data type
            TypeInfo operandDataType = childNode.getDataType();

            // Check the result's data type after applying the operator
            opCompat = new UnaryOperatorCompat(opId, operandDataType);
            resultDataType = opTable.getCompatDataType(opCompat);
            if (resultDataType == null) {
                throw new SyntaxError("Operator '" + op.getValue() + "' is not compatible with type '" +
                        operandDataType.getId() + "'", lexer.getCurrLine());
            }

            // Set the current node's data type to that of the result
            unaryNode.setDataType(resultDataType);
        } else {
            // Expression root must be a node containing a binary operator
            assert root instanceof BinaryNode;
            BinaryNode binaryNode = (BinaryNode) root;
            DataTypeNode leftNode = binaryNode.getLeft();
            DataTypeNode rightNode = binaryNode.getRight();

            // Recursively analyze the semantics of the left and right node
            analyzeSemantics(leftNode);
            analyzeSemantics(rightNode);

            // Get the left and right node's data type
            TypeInfo leftDataType = leftNode.getDataType();
            TypeInfo rightDataType = rightNode.getDataType();

            // Check the result's data type after applying the operator
            opCompat = new BinaryOperatorCompat(opId, leftDataType, rightDataType);
            resultDataType = opTable.getCompatDataType(opCompat);
            if (resultDataType == null) {
                throw new SyntaxError("Operator '" + op.getValue() + "' is not compatible with type '" +
                        leftDataType.getId() + "' and type '" + rightDataType.getId() + "'", lexer.getCurrLine());
            }

            // Set the current node's data type to that of the result
            binaryNode.setDataType(resultDataType);
        }
    }

}
