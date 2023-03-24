package Exceptions;

public class SyntaxError extends Exception {
    public SyntaxError(String message, int line) {
        super(message + " on line " + line);
    }
}
