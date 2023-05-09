package Parser;

import Exceptions.SyntaxError;
import Lexer.Lexer;
import Nodes.Node;
import Tokens.Token;
import Tokens.TokenType;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

public class SrcParser {
    private final BufferedReader reader;
    private final BufferedWriter writer;
    private final Lexer lexer;
    private final ExprParser exprParser;
    private final DeclParser declParser;
    private final StatementParser stmParser;
    private final BlockParser blockParser;
    private final ScopeParser scopeParser;


    public SrcParser(BufferedReader reader, BufferedWriter writer) {
        this.reader = reader;
        this.writer = writer;
        lexer = new Lexer(reader);
        exprParser = new ExprParser(lexer);
        declParser = new DeclParser(lexer, exprParser);
        stmParser = new StatementParser(declParser);
        blockParser = new BlockParser(lexer, stmParser);
        scopeParser = new ScopeParser(lexer, stmParser, blockParser);
    }

    /**
     * Parses the source code using the provided reader and writer.
     */
    public void parseSrc() {
        Block globalScope = new Block(null);
        Token tok;
        Node node;
        String jsonStr;

        try {
            writer.write("[\n");

            while ((tok = lexer.lookahead()) != null && tok.getType() != TokenType.EOF) {
                node = scopeParser.parseScope(globalScope);
                if (node != null) {
                    jsonStr = "{\n" + node.toJson() + "\n},\n";
                    writer.write(jsonStr);
                } else {
                    throw new SyntaxError("Invalid syntax error at '" + tok.getValue() + "'", lexer.getCurrLine());
                }
            }

            writer.write("\n]");
            writer.close();
        } catch (IOException | SyntaxError e) {
            e.printStackTrace();
        }
    }
}
