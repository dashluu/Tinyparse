package Exceptions;

public class SyntaxError extends Exception {
    public SyntaxError(String msg, int line) {
        super(msg + " on line " + line);
    }
}
