package Operators;

import Tokens.TokenType;

import java.util.HashMap;
import java.util.HashSet;

public class OperatorTable {
    private final HashMap<String, TokenType> opMap = new HashMap<>();
    private final HashSet<TokenType> prefixSet = new HashSet<>();
    private final HashSet<TokenType> infixSet = new HashSet<>();
    private final HashSet<TokenType> postfixSet = new HashSet<>();
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
            // Add operators to table
            INSTANCE.opMap.put("+", TokenType.ADD);
            INSTANCE.opMap.put("-", TokenType.SUB);
            INSTANCE.opMap.put("*", TokenType.MULT);
            INSTANCE.opMap.put("/", TokenType.DIV);
            INSTANCE.opMap.put("%", TokenType.MOD);
            INSTANCE.opMap.put(".", TokenType.DOT);
            INSTANCE.opMap.put(":", TokenType.COLON);
            INSTANCE.opMap.put("=", TokenType.ASSIGNMENT);
            INSTANCE.opMap.put("(", TokenType.LPAREN);
            INSTANCE.opMap.put(")", TokenType.RPAREN);
            INSTANCE.opMap.put(";", TokenType.SEMICOLON);

            // Initialize prefix table
            INSTANCE.prefixSet.add(TokenType.ADD);
            INSTANCE.prefixSet.add(TokenType.SUB);

            // Initialize infix table
            INSTANCE.infixSet.add(TokenType.ADD);
            INSTANCE.infixSet.add(TokenType.SUB);
            INSTANCE.infixSet.add(TokenType.MULT);
            INSTANCE.infixSet.add(TokenType.DIV);
            INSTANCE.infixSet.add(TokenType.MOD);
            INSTANCE.infixSet.add(TokenType.ASSIGNMENT);

            // Initialize postfix table

            // Initialize precedence table
            INSTANCE.precedMap.put(TokenType.ADD, 10);
            INSTANCE.precedMap.put(TokenType.SUB, 10);
            INSTANCE.precedMap.put(TokenType.MULT, 20);
            INSTANCE.precedMap.put(TokenType.DIV, 20);
            INSTANCE.precedMap.put(TokenType.ASSIGNMENT, 5);

            // Initialize associativity table
            INSTANCE.associativityMap.put(TokenType.ADD, true);
            INSTANCE.associativityMap.put(TokenType.SUB, true);
            INSTANCE.associativityMap.put(TokenType.MULT, true);
            INSTANCE.associativityMap.put(TokenType.DIV, true);
            INSTANCE.associativityMap.put(TokenType.ASSIGNMENT, false);

            init = true;
        }
        return INSTANCE;
    }

    /**
     * Gets the operator's id associated with the given string.
     *
     * @param opStr a string associated with an operator.
     * @return a TokenType object as the operator's id if it exists, otherwise, return null.
     */
    public TokenType getId(String opStr) {
        return opMap.get(opStr);
    }

    /**
     * Checks if a token is a prefix operator.
     *
     * @param id operator's id.
     * @return true if the token is a prefix operator and false otherwise.
     */
    public boolean isPrefix(TokenType id) {
        return prefixSet.contains(id);
    }

    /**
     * Checks if a token is an infix operator.
     *
     * @param id operator's id.
     * @return true if the token is an infix operator and false otherwise.
     */
    public boolean isInfix(TokenType id) {
        return infixSet.contains(id);
    }

    /**
     * Checks if a token is a postfix operator.
     *
     * @param id operator's id.
     * @return true if the token is a postfix operator and false otherwise.
     */
    public boolean isPostfix(TokenType id) {
        return postfixSet.contains(id);
    }

    /**
     * Gets the precedence of the given operator.
     *
     * @param id operator's id.
     * @return an int value representing the operator precedence.
     */
    public int getPreced(TokenType id) {
        Integer preced = precedMap.get(id);
        return preced == null ? -1 : preced;
    }

    /**
     * Gets the associativity of the given operator.
     *
     * @param id operator's id.
     * @return true if the operator left-to-right, otherwise, return false.
     */
    public boolean getAssociativity(TokenType id) {
        return associativityMap.get(id);
    }

    /**
     * Compares the precedences of two operators.
     *
     * @param id1 the first operator's id.
     * @param id2 the second operator's id.
     * @return 1 if the first operator has higher priority, otherwise, return -1.
     */
    public int cmpPreced(TokenType id1, TokenType id2) {
        int preced1 = getPreced(id1);
        int preced2 = getPreced(id2);
        if (preced1 != preced2) {
            // If the two precedences are not the same,
            // return 1 if the first operator has higher precedence, otherwise, return -1
            return Integer.compare(preced1, preced2);
        }
        // Get the associativity of the first operator
        boolean isOp1LToR = getAssociativity(id1);
        // Return 1 if the first operator is left-to-right, otherwise, return -1
        return isOp1LToR ? 1 : -1;
    }
}
