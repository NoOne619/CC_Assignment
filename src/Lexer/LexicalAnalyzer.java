package Lexer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LexicalAnalyzer {
    
       private final String input;
    private int position = 0;
    private int lineNumber = 1; // Track the current line number
    private final SymbolTable symbolTable = new SymbolTable(); // Symbol table for identifiers

    public LexicalAnalyzer(String input) {
        this.input = input;
    }

    public List<Token> tokenize() {
        List<Token> tokens = new ArrayList<>();

        while (position < input.length()) {
            char currentChar = input.charAt(position);

            // Skip whitespace
            if (Character.isWhitespace(currentChar)) {
                if (currentChar == '\n') lineNumber++; // Track line numbers
                position++;
                continue;
            }

            // Handle character literals (e.g., 'a')
            if (currentChar == '\'') {
                String charLiteral = readCharacterLiteral();
                tokens.add(new Token("CHAR_LITERAL", charLiteral));
                continue;
            }

            if (currentChar == '#') {
                skipSingleLineComment(tokens);
                continue;
            }
    
            // Handle multi-line comments (@...@)
            if (currentChar == '@') {
                skipMultiLineComment(tokens);
                continue;
            }
    
            // Handle function calls input(x) and output(x)
            if (startsWith("input(") || startsWith("output(")) {
                readFunctionCall(tokens);
                continue;
            }
            

            // Handle function calls input(x) and output(x)
            if (startsWith("input(") || startsWith("output(")) {
                readFunctionCall(tokens);
                continue;
            }

            // Check for identifiers or keywords
            if (Character.isLetter(currentChar)) {
                String token = readIdentifier();
                if (isValidToken(token, "KEYWORD")) {
                    tokens.add(new Token("KEYWORD", token));
                } else if (isValidToken(token, "IDENTIFIER")) {
                    tokens.add(new Token("IDENTIFIER", token));
                    // Add identifier to symbol table
                    symbolTable.addSymbol(token, "UNKNOWN", lineNumber); // Default type is UNKNOWN
                } else {
                    throw new RuntimeException("Invalid identifier at line " + lineNumber + ": " + token);
                }
                continue;
            }

            // Check for numbers (whole or decimal)
            if (Character.isDigit(currentChar)) {
                String token = readNumber();
                if (isValidToken(token, "DECIMAL")) {
                    tokens.add(new Token("DECIMAL", token));
                } else if (isValidToken(token, "WHOLE_NUMBER")) {
                    tokens.add(new Token("WHOLE_NUMBER", token));
                } else {
                    throw new RuntimeException("Invalid number at line " + lineNumber + ": " + token);
                }
                continue;
            }

            // Handle operators
            if (isValidToken(String.valueOf(currentChar), "OPERATOR")) {
                tokens.add(new Token("OPERATOR", String.valueOf(currentChar)));
                position++;
                continue;
            }

            // Handle symbols and punctuation
            if (!isValidToken(String.valueOf(currentChar), "SYMBOL")) {
                throw new RuntimeException("Invalid token at line " + lineNumber + ": " + currentChar);
            }
            tokens.add(new Token("SYMBOL", String.valueOf(currentChar)));
            position++;
        }

        // Print the symbol table after tokenization
        symbolTable.printSymbolTable();

        return tokens;
    }
    private String readStringLiteral() {
        StringBuilder sb = new StringBuilder();
        
        if (input.charAt(position) != '"') {
            throw new RuntimeException("Invalid string literal at line " + lineNumber);
        }
    
        sb.append('"');
        position++; // Skip opening quote
    
        while (position < input.length()) {
            char currentChar = input.charAt(position);
            
            if (currentChar == '"') { // Closing quote
                sb.append('"');
                position++;
                return sb.toString();
            }
            
            sb.append(currentChar);
            position++;
        }
    
        throw new RuntimeException("Unclosed string literal at line " + lineNumber);
    }
    
    private String readCharacterLiteral() {
        if (position + 2 >= input.length() || input.charAt(position) != '\'') {
            throw new RuntimeException("Invalid character literal at line " + lineNumber);
        }
    
        char charValue = input.charAt(position + 1); // Extract character inside quotes
    
        // Ensure closing single quote
        if (input.charAt(position + 2) != '\'') {
            throw new RuntimeException("Unclosed character literal at line " + lineNumber);
        }
    
        position += 3; // Move past the full character literal
        return "'" + charValue + "'";
    }
    
    // Skips everything after #
    private void skipSingleLineComment(List<Token> tokens) {
        StringBuilder sb = new StringBuilder();
        sb.append("#"); // Include '#' in the token
    
        position++; // Move past the '#'
    
        while (position < input.length() && input.charAt(position) != '\n') {
            sb.append(input.charAt(position));
            position++;
        }
    
        tokens.add(new Token("COMMENT", sb.toString())); // Store entire comment
    }
    

    // Skips everything between @...@
    private void skipMultiLineComment(List<Token> tokens) {
        StringBuilder sb = new StringBuilder();
        sb.append("@"); // Include opening '@' in the token
    
        position++; // Move past the first '@'
    
        while (position < input.length()) {
            if (input.charAt(position) == '@') {  
                sb.append("@"); // Include closing '@' in the token
                position++; // Move past the closing '@'
                tokens.add(new Token("MultiCOMMENT", sb.toString())); // Store entire comment
                return;
            }
    
            if (input.charAt(position) == '\n') {
                lineNumber++; // Track line numbers correctly
            }
    
            sb.append(input.charAt(position));
            position++;
        }
    
        // If we reach here, it means we never found the closing '@'
        throw new RuntimeException("Unclosed multi-line comment at line " + lineNumber);
    }
    
    private String readIdentifier() {
    StringBuilder sb = new StringBuilder();

    // Read only valid identifier characters
    while (position < input.length() && Character.isLetter(input.charAt(position))) {
        sb.append(input.charAt(position));
        position++;
    }

    // Stop reading if we hit an operator or invalid character
    if (position < input.length() && !Character.isDigit(input.charAt(position))) {
        char nextChar = input.charAt(position);
        if (isValidToken(String.valueOf(nextChar), "OPERATOR") || isValidToken(String.valueOf(nextChar), "SYMBOL")) {
            return sb.toString(); // Stop at operator/symbol
        }
    }

    // If number follows the identifier, check if it's valid
    while (position < input.length() && Character.isDigit(input.charAt(position))) {
        sb.append(input.charAt(position));
        position++;
    }

    return sb.toString();
}


    private String readNumber() {
        StringBuilder sb = new StringBuilder();
        boolean hasDecimal = false;
    
        while (position < input.length()) {
            char currentChar = input.charAt(position);
    
            if (Character.isDigit(currentChar)) {
                sb.append(currentChar);
            } else if (currentChar == '.' && !hasDecimal) {
                hasDecimal = true;
                sb.append(currentChar);
            } else {
                break;
            }
            position++;
        }
    
        // Check if the next character is a letter (invalid variable name like '3x')
        if (position < input.length() && Character.isLetter(input.charAt(position))) {
            throw new RuntimeException("Invalid identifier at line " + lineNumber + ": " + sb.toString() + input.charAt(position));
        }
    
        return sb.toString();
    }
    
    // Reads input(x) or output(x)
    private void readFunctionCall(List<Token> tokens) {
        StringBuilder sb = new StringBuilder();
        int startPos = position;
    
        // Read function name (input or output)
        while (position < input.length() && Character.isLetter(input.charAt(position))) {
            sb.append(input.charAt(position));
            position++;
        }
    
        String functionName = sb.toString();
        if (!functionName.equals("input") && !functionName.equals("output")) {
            throw new RuntimeException("Invalid function at line " + lineNumber + ": " + functionName);
        }
    
        tokens.add(new Token("FUNCTION", functionName)); // Add FUNCTION token
    
        // Ensure '(' follows immediately
        if (position < input.length() && input.charAt(position) == '(') {
            tokens.add(new Token("SYMBOL", "("));
            position++;
        } else {
            throw new RuntimeException("Expected '(' after function name at line " + lineNumber);
        }
    
        // Read argument (identifier or string)
        if (position < input.length()) {
            char nextChar = input.charAt(position);
    
            if (Character.isLetter(nextChar)) {
                // Read identifier
                String arg = readIdentifier();
                tokens.add(new Token("IDENTIFIER", arg));
            } else if (nextChar == '"') {
                // Read string literal
                String arg = readStringLiteral();
                tokens.add(new Token("STRING_LITERAL", arg));
            } else {
                throw new RuntimeException("Invalid function argument at line " + lineNumber);
            }
        }
    
        // Ensure ')' follows
        if (position < input.length() && input.charAt(position) == ')') {
            tokens.add(new Token("SYMBOL", ")"));
            position++;
        } else {
            throw new RuntimeException("Expected ')' at the end of function call at line " + lineNumber);
        }
    }
         
    private boolean startsWith(String prefix) {
        return input.startsWith(prefix, position);
    }

    private boolean isValidToken(String token, String tokenType) {
        DFA dfa = DFAStore.getDFA(tokenType);
        if (dfa == null) {
            throw new RuntimeException("No DFA found for token type: " + tokenType);
        }
       // DFAConverter.printDFATransitionTable(dfa);
        State currentState = dfa.startState;
        for (char c : token.toCharArray()) {
            Map<Character, State> transitions = dfa.transitionTable.get(currentState);
            if (transitions == null || !transitions.containsKey(c)) {
                return false;
            }
            currentState = transitions.get(c);
        }
        return dfa.finalStates.contains(currentState);
    }
}