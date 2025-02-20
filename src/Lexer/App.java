package Lexer;

import java.io.IOException;
import java.nio.file.*;
import java.net.URISyntaxException;

public class App {
    public static void main(String[] args) {
        try {
            // Load file from the classpath
            String filePath = Paths.get(App.class.getResource("/Lexer/program.cmx").toURI()).toString();

            // Read file content
            String code = new String(Files.readAllBytes(Paths.get(filePath)));

            // Initialize lexer
            LexicalAnalyzer analyzer = new LexicalAnalyzer(code);
            analyzer.tokenize().forEach(System.out::println);

        } catch (IOException | URISyntaxException e) {
            System.out.println("Error reading the file: " + e.getMessage());
        }
    }
}
