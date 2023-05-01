package Parser;

import Exceptions.SyntaxError;
import Nodes.Node;

import java.io.IOException;

public class StatementParser {
    private final DeclParser declParser;

    public StatementParser(DeclParser declParser) {
        this.declParser = declParser;
    }

    /**
     * Parses a statement in a scope.
     *
     * @param scope the scope surrounding the statement.
     * @return an AST node as the tree root representing the statement.
     * @throws SyntaxError if there is a syntax error.
     * @throws IOException if there is an IO exception.
     */
    public Node parseStatement(Block scope) throws SyntaxError, IOException {
        return declParser.parseDecl(scope);
    }
}
