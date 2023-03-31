package Lexer;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayDeque;

public class CharBuffer {
    private final ArrayDeque<Integer> buffer = new ArrayDeque<>();
    private final BufferedReader reader;

    public CharBuffer(BufferedReader reader) {
        this.reader = reader;
    }

    /**
     * Peeks without extracting a character from the internal buffer. If the internal buffer is empty, read a character
     * from the stream into the buffer.
     *
     * @return the peeked character(as an int).
     * @throws IOException if there is an error while reading from the stream.
     */
    public int peek() throws IOException {
        if (buffer.isEmpty()) {
            buffer.addLast(reader.read());
        }
        // This buffer will never be empty
        assert !buffer.isEmpty();
        return buffer.peekFirst();
    }

    /**
     * Peeks and extracts the first character in the internal buffer.
     *
     * @return the extracted character if there is any.
     * @throws IOException if there is an error while reading from the stream.
     */
    public int read() throws IOException {
        int c = peek();
        buffer.pop();
        return c;
    }

    /**
     * Puts back a valid string into the internal buffer.
     *
     * @param str the string to be put back.
     */
    public void putBack(String str) throws IllegalArgumentException {
        if (str == null) {
            throw new IllegalArgumentException("Null string cannot be put back into the lexer buffer");
        }
        if (str.isEmpty()) {
            throw new IllegalArgumentException("Empty string cannot be put back into the lexer buffer");
        }
        for (int i = str.length() - 1; i >= 0; --i) {
            buffer.addFirst((int) str.charAt(i));
        }
    }
}
