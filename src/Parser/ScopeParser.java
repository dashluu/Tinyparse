package Parser;

import Exceptions.SyntaxError;
import Lexer.Lexer;
import Nodes.Node;
import Nodes.NodeType;
import Nodes.ScopeNode;

import java.io.IOException;

public class ScopeParser extends BaseParser {
    protected final StatementParser stmParser;
    protected final BlockParser blockParser;

    public ScopeParser(Lexer lexer, StatementParser stmParser, BlockParser blockParser) {
        super(lexer);
        this.stmParser = stmParser;
        this.blockParser = blockParser;
    }

    /**
     * Parses code components, including statements, if-else, loops, etc. in a scope.
     *
     * @param scope the currently surrounding scope.
     * @return an AST node that stores other components' AST roots.
     * @throws SyntaxError if there is a syntax error.
     * @throws IOException if there is an IO exception.
     */
    public ScopeNode parseScope(Block scope) throws SyntaxError, IOException {
        Node stmRoot;
        ScopeNode blockRoot;
        ScopeNode scopeRoot = new ScopeNode();
        boolean endFlag = false;

        while (!endFlag) {
            // Try parsing a statement
            stmRoot = stmParser.parseStatement(scope);
            endFlag = stmRoot == null;

            if (!endFlag) {
                if (stmRoot.getType() != NodeType.EMPTY) {
                    // Add statement node to scope node only if it is not an empty statement
                    scopeRoot.addChild(stmRoot);
                }
            } else {
                // If it does not work, try parsing a block of code
                blockRoot = blockParser.parseBlock(scope);
                endFlag = blockRoot == null;
                if (!endFlag) {
                    if (blockRoot.countChildren() > 0) {
                        // Add block node to scope node only if it is not an empty block
                        scopeRoot.addChild(blockRoot);
                    }
                }
            }
        }

        return scopeRoot.countChildren() == 0 ? null : scopeRoot;
    }
}
