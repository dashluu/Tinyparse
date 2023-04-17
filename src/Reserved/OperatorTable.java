package Reserved;

import Tokens.TokenType;

import java.util.HashMap;
import java.util.HashSet;

public class OperatorTable {
    private final HashSet<TokenType> prefixOps = new HashSet<>();
    private final HashSet<TokenType> infixOps = new HashSet<>();
    private final HashSet<TokenType> postfixOps = new HashSet<>();
    private final HashMap<TokenType, Integer> precedMap = new HashMap<>();
    // Associativity table, true means left-to-right, false means right-to-left
    private final HashMap<TokenType, Boolean> associativityMap = new HashMap<>();
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

            // Initializes associativity table
            INSTANCE.associativityMap.put(TokenType.ADD, true);
            INSTANCE.associativityMap.put(TokenType.SUB, true);
            INSTANCE.associativityMap.put(TokenType.MULT, true);
            INSTANCE.associativityMap.put(TokenType.DIV, true);

            init = true;
        }
        return INSTANCE;
    }

    /**
     * Checks if a token is a prefix operator.
     *
     * @param opType operator's token type.
     * @return true if the token is a prefix operator and false otherwise.
     */
    public boolean isPrefix(TokenType opType) {
        return prefixOps.contains(opType);
    }

    /**
     * Checks if a token is an infix operator.
     *
     * @param opType operator's token type.
     * @return true if the token is an infix operator and false otherwise.
     */
    public boolean isInfix(TokenType opType) {
        return infixOps.contains(opType);
    }

    /**
     * Checks if a token is a postfix operator.
     *
     * @param opType operator's token type.
     * @return true if the token is a postfix operator and false otherwise.
     */
    public boolean isPostfix(TokenType opType) {
        return postfixOps.contains(opType);
    }

    /**
     * Gets the precedence of the given operator.
     *
     * @param opType operator's token type.
     * @return an int value representing the operator precedence.
     */
    public int getPreced(TokenType opType) {
        Integer preced = precedMap.get(opType);
        return preced == null ? -1 : preced;
    }

    /**
     * Gets the associativity of the given operator.
     *
     * @param opType operator's token type.
     * @return true if the operator left-to-right, otherwise, return false.
     */
    public boolean getAssociativity(TokenType opType) {
        return associativityMap.get(opType);
    }

    /**
     * Compares the precedences of two operators.
     *
     * @param opType1 the first operator's token type.
     * @param opType2 the second operator's token type.
     * @return 1 if the first operator has higher priority, otherwise, return -1.
     */
    public int cmpPreced(TokenType opType1, TokenType opType2) {
        int preced1 = getPreced(opType1);
        int preced2 = getPreced(opType2);
        if (preced1 != preced2) {
            // If the two precedences are not the same,
            // return 1 if the first operator has higher precedence, otherwise, return -1
            return Integer.compare(preced1, preced2);
        }
        // Get the associativity of the first operator
        boolean isOp1LToR = getAssociativity(opType1);
        // Return 1 if the first operator is left-to-right, otherwise, return -1
        return isOp1LToR ? 1 : -1;
    }
}
