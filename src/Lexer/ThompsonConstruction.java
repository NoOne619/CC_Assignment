package Lexer;

import java.util.*;

public class ThompsonConstruction {
    private static int stateCounter = 1;
    private static String[][] tokenRegexes = {
        {"BOOLEAN", "false|true"},
        {"IDENTIFIER", "[a-z]+"},
        {"WHOLE_NUMBER", "\\-?[0-9]+"},
        {"DECIMAL", "\\-?[0-9]+(\\.[0-9]+)*"},
        {"KEYWORD", "int|float|global|char|bool"},
        {"CHAR", "'[a-z0-9]'"},
        {"OPERATOR", "[%+\\-@=*^]"},
        {"SINGLE_LINE_COMMENT", "#[a-z]*"},
        {"MULTI_LINE_COMMENT", "@[a-zA-Z0-9]*@"},
        {"String", "\"[^\"]*\""},
        {"SYMBOL", "[;()]"},
        {"OPEN_BRACKET", "[{]"},
        {"CLOSE_BRACKET", "[}]"},
        {"INPUT/OUTPUT", "input|output"},
    };

    public static NFA buildNFAFromRegex(RegexNode node) {
        
        if (node instanceof LiteralNode) {
            return buildLiteralNFA(((LiteralNode) node).literal);
        } else if (node instanceof CharClassNode) {
            return buildCharClassNFA((CharClassNode) node);
        } else if (node instanceof ConcatNode) {
            return buildConcatNFA((ConcatNode) node);
        } else if (node instanceof UnionNode) {
            return buildUnionNFA((UnionNode) node);
        } else if (node instanceof StarNode) {
            return buildStarNFA(((StarNode) node).node);
        } else if (node instanceof PlusNode) {
            return buildPlusNFA(((PlusNode) node).node);
        } else {
            throw new RuntimeException("Unknown RegexNode type");
        }
        
    }

    private static NFA buildLiteralNFA(char literal) {
        State start = new State(stateCounter++, false);
        State end = new State(stateCounter++, true);
        start.addTransition(literal, end);
        return new NFA(start, end);
    }

    private static NFA buildCharClassNFA(CharClassNode node) {
        List<RegexNode> literals = new ArrayList<>();
        for (char c : node.characters) {
            literals.add(new LiteralNode(c));
        }
        UnionNode unionNode = new UnionNode(literals);
        return buildUnionNFA(unionNode);
    }

    private static NFA buildConcatNFA(ConcatNode node) {
        List<RegexNode> nodes = node.nodes;
        if (nodes.isEmpty()) {
            State start = new State(stateCounter++, true);
            return new NFA(start, start);
        }
        NFA result = buildNFAFromRegex(nodes.get(0));
        for (int i = 1; i < nodes.size(); i++) {
            NFA next = buildNFAFromRegex(nodes.get(i));
            result.finalState.isFinal = false;
            result.finalState.addTransition('\0', next.startState);
            result = new NFA(result.startState, next.finalState);
        }
        return result;
    }

    private static NFA buildUnionNFA(UnionNode node) {
        State start = new State(stateCounter++, false);
        State end = new State(stateCounter++, true);
        for (RegexNode alternative : node.alternatives) {
            NFA altNFA = buildNFAFromRegex(alternative);
            start.addTransition('\0', altNFA.startState);
            altNFA.finalState.isFinal = false;
            altNFA.finalState.addTransition('\0', end);
        }
        return new NFA(start, end);
    }

    private static NFA buildStarNFA(RegexNode node) {
        NFA inner = buildNFAFromRegex(node);
        State start = new State(stateCounter++, false);
        State end = new State(stateCounter++, true);
        start.addTransition('\0', inner.startState);
        start.addTransition('\0', end);
        inner.finalState.isFinal = false;
        inner.finalState.addTransition('\0', inner.startState);
        inner.finalState.addTransition('\0', end);
        return new NFA(start, end);
    }

    private static NFA buildPlusNFA(RegexNode node) {
        NFA inner = buildNFAFromRegex(node);
        State start = new State(stateCounter++, false);
        State end = new State(stateCounter++, true);
        start.addTransition('\0', inner.startState);
        inner.finalState.isFinal = false;
        inner.finalState.addTransition('\0', inner.startState);
        inner.finalState.addTransition('\0', end);
        return new NFA(start, end);
    }

    public static void printTransitionTable(NFA nfa) {
        System.out.println("Transition Table:");
        System.out.println("-----------------");
        System.out.println("State\tSymbol\tNext State");

        Queue<State> queue = new LinkedList<>();
        Set<Integer> visited = new HashSet<>();
        queue.add(nfa.startState);

        while (!queue.isEmpty()) {
            State current = queue.poll();
            if (visited.contains(current.id))
                continue;
            visited.add(current.id);

            for (Map.Entry<Character, Set<State>> entry : current.transitions.entrySet()) {
                char symbol = entry.getKey();
                for (State next : entry.getValue()) {
                    System.out.println(current.id + "\t" + (symbol == '\0' ? "ε" : symbol) + "\t" + next.id);
                    queue.add(next);
                }
            }
        }
    }

   
    public static NFA buildNFAFromRegex(String regex) {
        Parser parser = new Parser(regex);
        RegexNode ast = parser.parse();
        return buildNFAFromRegex(ast);
    }
    public static void showTransitionTable() {
        // 2D array where each entry contains [tokenType, regex]
      
        for (int i = 0; i < tokenRegexes.length; i++) {
            stateCounter = 1; // Reset state counter for each regex
            String tokenType = tokenRegexes[i][0];
            String regex = tokenRegexes[i][1];
    
            System.out.println("NFA for Token Type: " + tokenType + ", Regex: " + regex);
            NFA nfa = buildNFAFromRegex(regex);
            printTransitionTable(nfa);
            System.out.println("Starting State: " + nfa.startState.id);
            System.out.println("Accepting State: " + nfa.finalState.id);
            System.out.println();
    
            // Convert NFA to DFA and print its transition table
            DFAfromNFA(nfa);
            System.out.println();
        }
    }
    
    public static void DFAfromNFA(NFA nfa) {
         DFAConverter.convertNFAtoDFA(nfa);
        DFA dfa = DFAConverter.convertNFAtoDFA(nfa);
        DFAConverter.printDFATransitionTable(dfa);

    }
}