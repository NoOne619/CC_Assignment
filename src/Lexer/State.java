package Lexer;

import java.util.*;
import java.util.*;

public class State {
    int id;
    boolean isFinal;
    Map<Character, Set<State>> transitions = new HashMap<>();

    public State(int id, boolean isFinal) {
        this.id = id;
        this.isFinal = isFinal;
    }

    public void addTransition(char symbol, State nextState) {
        transitions.computeIfAbsent(symbol, k -> new HashSet<>()).add(nextState);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        State state = (State) o;
        return id == state.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}