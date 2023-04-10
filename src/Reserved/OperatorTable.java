package Reserved;

import Tokens.TokenType;

import java.util.HashMap;
import java.util.HashSet;

public class OperatorTable {
    private final HashSet<TokenType> prefixOps = new HashSet<>();
    private final HashSet<TokenType> infixOps = new HashSet<>();
    private final HashSet<TokenType> postfixOps = new HashSet<>();
    private final HashMap<TokenType, Integer> precedMap = new HashMap<>();
    private final static OperatorTable INSTANCE = new OperatorTable();
    private static boolean init = false;

    private OperatorTable() {
    }

    /**
     * Initializes the only instance of OperatorTable if it has not been initialized and then returns it.
     *
     * @return an OperatorTable object.
     */
    public static OperatorTable getInstance() {
        if (!init) {
            // Initializes prefix table
            INSTANCE.prefixOps.add(TokenType.ADD);
            INSTANCE.prefixOps.add(TokenType.SUB);

            // Initializes infix table
            INSTANCE.infixOps.add(TokenType.ADD);
            INSTANCE.infixOps.add(TokenType.SUB);
            INSTANCE.infixOps.add(TokenType.MULT);
            INSTANCE.infixOps.add(TokenType.DIV);
            INSTANCE.infixOps.add(TokenType.MOD);

            // Initializes postfix table

            // Initializes precedence table
            INSTANCE.precedMap.put(TokenType.ADD, 10);
            INSTANCE.precedMap.put(TokenType.SUB, 10);
            INSTANCE.precedMap.put(TokenType.MULT, 20);
            INSTANCE.precedMap.put(TokenType.DIV, 20);

            init = true;
        }
        return INSTANCE;
    }

    /**
     * Checks if a token is a prefix operator.
     *
     * @param tokenType the type of the token.
     * @return true if the token is a prefix operator and false otherwise.
     */
    public boolean isPrefix(TokenType tokenType) {
        return prefixOps.contains(tokenType);
    }

    /**
     * Checks if a token is an infix operator.
     *
     * @param tokenType the type of the token.
     * @return true if the token is an infix operator and false otherwise.
     */
    public boolean isInfix(TokenType tokenType) {
        return infixOps.contains(tokenType);
    }

    /**
     * Checks if a token is a postfix operator.
     *
     * @param tokenType the type of the token.
     * @return true if the token is a postfix operator and false otherwise.
     */
    public boolean isPostfix(TokenType tokenType) {
        return postfixOps.contains(tokenType);
    }

    /**
     * Gets the precedence of the given operator.
     *
     * @param tokenType the type of the operator token.
     * @return an int value representing the operator precedence.
     */
    public int getPreced(TokenType tokenType) {
        Integer preced = precedMap.get(tokenType);
        return preced == null ? -1 : preced;
    }
}
