package Parser;

import Exceptions.SyntaxError;
import Lexer.Lexer;
import Nodes.ScopeNode;
import Tokens.TokenType;

import java.io.IOException;

public class BlockParser extends BaseParser {
    private final ScopeParser scopeParser;

    public BlockParser(Lexer lexer, StatementParser stmParser) {
        super(lexer);
        scopeParser = new ScopeParser(lexer, stmParser, this);
    }

    /**
     * Parses code components, including statements, if-else, loops, etc. in a block using the scope parser.
     *
     * @param scope the current scope that surrounds the new block.
     * @return an AST node that stores other components' AST roots.
     * @throws SyntaxError if there is a syntax error.
     * @throws IOException if there is an IO exception.
     */
    public ScopeNode parseBlock(Block scope) throws SyntaxError, IOException {
        Block newScope = new Block(scope);

        // Try parsing left brackets
        if (parseTok(TokenType.LBRACKETS) == null) {
            return null;
        }

        // Try parsing code in the scope
        ScopeNode root = scopeParser.parseScope(newScope);

        // Try parsing right brackets
        if (parseTok(TokenType.RBRACKETS) == null) {
            throw new SyntaxError("Missing '}'", lexer.getCurrLine());
        }

        return root;
    }
}
