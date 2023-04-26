package Parser;

import Exceptions.SyntaxError;
import Lexer.Lexer;
import Tokens.Token;
import Tokens.TokenType;

import java.io.IOException;

public class DeclParser extends BaseParser {
    private final ExprParser exprParser;

    public DeclParser(Lexer lexer, ExprParser exprParser) {
        super(lexer);
        this.exprParser = exprParser;
    }

    public Node parseVarDecl(Block scope) throws SyntaxError, IOException {
        // Try parsing the head
        if (parseTok(TokenType.VAR_DECL) == null) {
            return null;
        }
        // Check for variable name
        Token varNameToken = lexer.consume();
        if (varNameToken.getType() != TokenType.ID) {
            throw new SyntaxError("Expected a variable name", lexer.getCurrLine());
        }
        Node varNameNode = new Node(varNameToken, NodeType.TERMINAL);
        Node lhs = varNameNode;
        // Check for type annotation, OK if it isn't there
        Node typeNode = parseType();
        if (typeNode != null) {
            // Create a new tree if there is a data type
            typeNode.addChild(varNameNode);
            lhs = typeNode;
        }
        // Check for rhs initialization
        Node initializationNode = parseInitializer(lhs, scope);
        return initializationNode == null ? lhs : initializationNode;
    }

    private Node parseType() throws SyntaxError, IOException {
        // Check for ':'
        if (parseTok(TokenType.COLON) == null) {
            return null;
        }
        // Check if the next token is a data type
        Token token = lexer.consume();
        if (token.getType() != TokenType.TYPE_ID) {
            throw new SyntaxError("Expected a type after ':'", lexer.getCurrLine());
        }
        return new Node(token, NodeType.TYPE);
    }

    private Node parseInitializer(Node lhs, Block scope) throws SyntaxError, IOException {
        // Check for '='
        Token assignmentToken = parseTok(TokenType.ASSIGNMENT);
        if (assignmentToken == null) {
            return null;
        }
        // Parse an expression using expression parser
        Node exprNode = exprParser.parseExpr(scope);
        if (exprNode == null) {
            throw new SyntaxError("Expected a nonempty expression after '='", lexer.getCurrLine());
        }
        // Construct a new tree whose root is '='
        Node assignmentNode = new Node(assignmentToken, NodeType.DECL_ASSIGNMENT);
        assignmentNode.addChild(lhs);
        assignmentNode.addChild(exprNode);
        return assignmentNode;
    }
}
