package Lexer;

import java.util.HashMap;
import java.util.Map;

public class SymbolTable {
    private final Map<String, Symbol> symbols = new HashMap<>();

    // Add a new symbol to the table
    public void addSymbol(String name, String type, int lineNumber) {
        if (symbols.containsKey(name)) {
            //throw new RuntimeException("Duplicate identifier at line " + lineNumber + ": " + name);
            return;
        }
        symbols.put(name, new Symbol(name, type, lineNumber));
    }

    // Look up a symbol by name
    public Symbol getSymbol(String name) {
        return symbols.get(name);
    }

    // Update the value of a symbol
    public void updateSymbolValue(String name, String value) {
        Symbol symbol = symbols.get(name);
        if (symbol == null) {
            throw new RuntimeException("Undefined identifier: " + name);
        }
        symbol.setValue(value);
    }

    // Print the symbol table
    public void printSymbolTable() {
        System.out.println("Symbol Table:");
        System.out.println("Name\tValue\tType\tLine Number");
        for (Symbol symbol : symbols.values()) {
            System.out.println(symbol.getName() + "\t" +
                    (symbol.getValue() != null ? symbol.getValue() : "null") + "\t" +
                    symbol.getType() + "\t" +
                    symbol.getLineNumber());
        }
    }
}