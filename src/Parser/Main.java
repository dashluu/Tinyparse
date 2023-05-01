package Parser;

import Exceptions.SyntaxError;
import Lexer.Lexer;
import Nodes.JsonTraversal;
import Nodes.Node;
import Tokens.Token;
import Tokens.TokenType;

import java.io.*;

public class Main {
    public static void main(String[] args) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("src.txt"));
            BufferedWriter writer = new BufferedWriter(new FileWriter("ast.txt"));
            Lexer lexer = new Lexer(reader);
            ExprParser exprParser = new ExprParser(lexer);
            DeclParser declParser = new DeclParser(lexer, exprParser);
            StatementParser stmParser = new StatementParser(declParser);
            Block globalScope = new Block(null);
            JsonTraversal traversal = new JsonTraversal();
            Token tok;
            Node node;
            String jsonStr;

            while ((tok = lexer.lookahead()) != null && tok.getType() != TokenType.EOF) {
                node = stmParser.parseStatement(globalScope);
                if (node != null) {
                    jsonStr = traversal.traverse(node);
                    writer.write(jsonStr);
                }
            }

            writer.close();
        } catch (IOException | SyntaxError e) {
            e.printStackTrace();
        }
    }
}
