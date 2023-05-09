package Parser;

import Parser.SrcParser;

import java.io.*;

public class Main {
    public static void main(String[] args) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("src.txt"));
            BufferedWriter writer = new BufferedWriter(new FileWriter("ast.txt"));
            SrcParser srcParser = new SrcParser(reader, writer);
            srcParser.parseSrc();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
