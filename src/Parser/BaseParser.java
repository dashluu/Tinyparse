package Parser;

import Exceptions.SyntaxError;
import Lexer.Lexer;
import Operators.OperatorTable;
import Tokens.Token;
import Tokens.TokenType;
import Types.TypeTable;

import java.io.IOException;

public abstract class BaseParser {
    protected final Lexer lexer;
    protected final OperatorTable opTable = OperatorTable.getInstance();
    protected final TypeTable typeTable = TypeTable.getInstance();

    public BaseParser(Lexer lexer) {
        this.lexer = lexer;
    }

    /**
     * Consumes the next token if its type is as expected.
     *
     * @param tokType the token type to be expected.
     * @return a token if the next token's type is as expected and null otherwise.
     * @throws SyntaxError if there is a syntax error.
     * @throws IOException if there is an IO exception.
     */
    protected Token parseTok(TokenType tokType) throws SyntaxError, IOException {
        Token tok = lexer.lookahead();
        if (tok.getType() != tokType) {
            return null;
        }
        lexer.consume();
        return tok;
    }
}
