package Lexer;

import java.util.Set;
import java.util.HashMap;
import java.util.Map;

public class DFA {
    State startState;
    Set<State> finalStates;
    Map<State, Map<Character, State>> transitionTable = new HashMap<>();

    public DFA(State startState, Set<State> finalStates) {
        this.startState = startState;
        this.finalStates = finalStates;
    }

    public void addTransition(State from, char symbol, State to) {
        transitionTable.computeIfAbsent(from, k -> new HashMap<>()).put(symbol, to);
    }
}