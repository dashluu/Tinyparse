package Parser;

import Exceptions.SyntaxError;
import Lexer.Lexer;
import Nodes.DataTypeNode;
import Nodes.Node;
import Nodes.NodeType;
import Nodes.VarNode;
import Symbols.SymbolTable;
import Symbols.VarInfo;
import Tokens.Token;
import Tokens.TokenType;
import Types.TypeInfo;

import java.io.IOException;

public class DeclParser extends BaseParser {
    private final ExprParser exprParser;

    public DeclParser(Lexer lexer, ExprParser exprParser) {
        super(lexer);
        this.exprParser = exprParser;
    }

    /**
     * Parses a declaration statement in a scope.
     *
     * @param scope the current scope surrounding the declaration.
     * @return an AST node as the root if successful, otherwise, return null.
     * @throws SyntaxError if there is a syntax error.
     * @throws IOException if there is an IO exception.
     */
    public Node parseDecl(Block scope) throws SyntaxError, IOException {
        DataTypeNode lhs = parseLhs(scope);
        return parseAssignment(lhs, scope);
    }

    /**
     * Parses the left-hand side(lhs) of a declaration.
     *
     * @param scope the current scope surrounding the declaration.
     * @return an AST node representing the lhs if successful, otherwise, return null.
     * @throws SyntaxError if there is a syntax error.
     * @throws IOException if there is an IO exception.
     */
    private DataTypeNode parseLhs(Block scope) throws SyntaxError, IOException {
        boolean mutable;

        // Try parsing the declaration head and determining if it is a variable or a constant
        if (parseTok(TokenType.VAR_DECL) == null) {
            if (parseTok(TokenType.CONST_DECL) == null) {
                return null;
            } else {
                mutable = false;
            }
        } else {
            mutable = true;
        }

        // Check for variable or constant name
        Token idTok = lexer.consume();
        if (idTok.getType() != TokenType.ID) {
            throw new SyntaxError("Expected a variable name", lexer.getCurrLine());
        }

        // Check for type annotation, OK if it isn't there
        TypeInfo dataType = parseType();

        // Check if it is a new variable or constant
        SymbolTable symbolTable = scope.getSymbolTable();
        String id = idTok.getValue();
        if (symbolTable.getSymbol(id) != null) {
            // If the variable or constant has been declared, throw an exception
            throw new SyntaxError("Cannot redeclare a variable or a constant", lexer.getCurrLine());
        } else {
            // Create a new variable or constant if it does not exist
            VarInfo varInfo = new VarInfo(id, dataType, mutable);
            symbolTable.register(varInfo);
        }

        return new VarNode(idTok, NodeType.DECL, dataType, mutable);
    }

    /**
     * Parses the type annotation on the lhs of a declaration.
     *
     * @return a TypeInfo object associated with the lhs's data type.
     * @throws SyntaxError if there is a syntax error.
     * @throws IOException if there is an IO exception.
     */
    private TypeInfo parseType() throws SyntaxError, IOException {
        // Check for ':'
        if (parseTok(TokenType.COLON) == null) {
            return null;
        }
        // Check if the next token is a data type
        Token token = lexer.consume();
        if (token.getType() != TokenType.TYPE_ID) {
            throw new SyntaxError("Expected a type after ':'", lexer.getCurrLine());
        }
        // Get the data type from the table using type id
        return typeTable.getType(token.getValue());
    }

    /**
     * Parses assignment and the right-hand side(rhs) of a declaration.
     *
     * @param lhs   the AST node that is associated with the lhs and contains the variable or constant's data type.
     * @param scope the current scope surrounding the declaration.
     * @return an AST node as the root if successful, otherwise, return null.
     * @throws SyntaxError if there is a syntax error.
     * @throws IOException if there is an IO exception.
     */
    private Node parseAssignment(DataTypeNode lhs, Block scope) throws SyntaxError, IOException {
        // Check for '='
        Token assignmentToken = parseTok(TokenType.ASSIGNMENT);
        if (assignmentToken == null) {
            if (lhs == null) {
                // If there is no lhs, return immediately
                return null;
            } else if (lhs.getDataType() == null) {
                // Check if the data type of lhs has been declared
                // If not, throw an exception
                throw new SyntaxError("Cannot determine the type of '" + lhs.getTok().getValue() + "'",
                        lexer.getCurrLine());
            } else if (parseTok(TokenType.SEMICOLON) == null) {
                // Otherwise, check if ';' is present for the case 'var id:type;'
                throw new SyntaxError("Missing ';'", lexer.getCurrLine());
            }
            return lhs;
        }
        // Parse an expression using expression parser
        Node exprNode = exprParser.parseExpr(scope);
        if (exprNode == null) {
            throw new SyntaxError("Expected a nonempty expression after '='", lexer.getCurrLine());
        }
        // Construct a new tree whose root is '='
        Node assignmentNode = new Node(assignmentToken, NodeType.ASSIGNMENT);
        assignmentNode.addChild(lhs);
        assignmentNode.addChild(exprNode);
        return assignmentNode;
    }
}
