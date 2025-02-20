package Lexer;

import java.util.*;

public class Parser {
    String pattern;
    int index = 0;

    public Parser(String pattern) {
        this.pattern = pattern;
    }

    public RegexNode parse() {
        return parseUnion();
    }

    private RegexNode parseUnion() {
        List<RegexNode> alternatives = new ArrayList<>();
        alternatives.add(parseConcat());
        while (peek() == '|') {
            consume();
            alternatives.add(parseConcat());
        }
        return alternatives.size() == 1 ? alternatives.get(0) : new UnionNode(alternatives);
    }

    private RegexNode parseConcat() {
        List<RegexNode> nodes = new ArrayList<>();
        while (hasMore() && peek() != ')' && peek() != '|') {
            nodes.add(parseRepetition());
        }
        return nodes.size() == 1 ? nodes.get(0) : new ConcatNode(nodes);
    }

    private RegexNode parseRepetition() {
        RegexNode node = parsePrimary();
        while (hasMore()) {
            char next = peek();
            if (next == '*') {
                consume();
                node = new StarNode(node);
            } else if (next == '+') {
                consume();
                node = new PlusNode(node);
            } else {
                break;
            }
        }
        return node;
    }

    private RegexNode parsePrimary() {
        if (!hasMore())
            throw new RuntimeException("Unexpected end of pattern");
        char curr = peek();
        if (curr == '(') {
            consume();
            RegexNode node = parseUnion();
            if (peek() == ')') {
                consume();
            } else {
                throw new RuntimeException("Missing closing parenthesis");
            }
            return node;
        } else if (curr == '[') {
            return parseCharClass();
        } else {
            return new LiteralNode(parseLiteral());
        }
    }

    private char parseLiteral() {
        char curr = consume();
        if (curr == '\\') {
            if (!hasMore())
                throw new RuntimeException("Dangling escape at end of pattern");
            return consume();
        }
        return curr;
    }

    private RegexNode parseCharClass() {
        consume();
        Set<Character> chars = new HashSet<>();
        boolean negate = false;
        if (peek() == '^') {
            negate = true;
            consume();
        }
        while (hasMore() && peek() != ']') {
            char start = consume();
            if (start == '\\') {
                if (!hasMore())
                    throw new RuntimeException("Dangling escape in char class");
                start = consume();
            }
            if (hasMore() && peek() == '-' && lookahead(1) != ']') {
                consume();
                char end = consume();
                if (end == '\\') {
                    if (!hasMore())
                        throw new RuntimeException("Dangling escape in char class range");
                    end = consume();
                }
                for (char c = start; c <= end; c++) {
                    chars.add(c);
                }
            } else {
                chars.add(start);
            }
        }
        if (hasMore() && peek() == ']') {
            consume();
        } else {
            throw new RuntimeException("Unclosed character class");
        }
        return new CharClassNode(chars);
    }

    private char peek() {
        return hasMore() ? pattern.charAt(index) : '\0';
    }

    private char lookahead(int offset) {
        return (index + offset < pattern.length()) ? pattern.charAt(index + offset) : '\0';
    }

    private char consume() {
        return pattern.charAt(index++);
    }

    private boolean hasMore() {
        return index < pattern.length();
    }
}