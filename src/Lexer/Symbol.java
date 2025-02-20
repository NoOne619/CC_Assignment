package Lexer;

public class Symbol {
    private final String name;
    private String value;
    private final String type;
    private final int lineNumber;

    public Symbol(String name, String type, int lineNumber) {
        this.name = name;
        this.type = type;
        this.lineNumber = lineNumber;
        this.value = null; // Default value is null
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    @Override
    public String toString() {
        return "Symbol{" +
                "name='" + name + '\'' +
                ", value='" + value + '\'' +
                ", type='" + type + '\'' +
                ", lineNumber=" + lineNumber +
                '}';
    }
}