package Global;

public class Keywords {
    public static String MUTABLE_ID_DECL = "var";
    public static String IMMUTABLE_ID_DECL = "const";
    public static String GLOBAL_SCOPE_ID = "global";
    public static Block globalScope = new Block(GLOBAL_SCOPE_ID, null);
    public static String INT_TYPE_ID = "Int";
    public static String FLOAT_TYPE_ID = "Float";
    public static String BOOL_TYPE_ID = "Bool";
    public static String BOOL_FALSE = "false";
    public static String BOOL_TRUE = "true";
    public static String ADD = "+";
    public static String SUB = "-";
    public static String MULT = "*";
    public static String DIV = "/";
    public static String POW = "**";
    public static String MOD = "%";
    public static String BITWISE_NOT = "~";
    public static String BITWISE_AND = "&";
    public static String BITWISE_OR = "|";
    public static String LOGICAL_NOT = "!";
    public static String LOGICAL_AND = "&&";
    public static String LOGICAL_OR = "||";
    public static String GREATER = ">";
    public static String LESS = "<";
    public static String EQ = "==";
    public static String NOT_EQ = "!=";
    public static String GEQ = ">=";
    public static String LEQ = "<=";
    public static String LPAREN = "(";
    public static String RPAREN = ")";
    public static String COLON = ":";
    public static String SEMICOLON = ";";
    public static String DOT = ".";
    public static String ASSIGNMENT = "=";
}
