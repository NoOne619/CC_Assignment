package Lexer;

import java.util.*;

public class DFAConverter {
    public static DFA convertNFAtoDFA(NFA nfa) {
        State dfaStart = new State(1, false);
        Set<State> dfaFinals = new HashSet<>();
        Map<State, Map<Character, State>> transitionTable = new HashMap<>();

        Map<Set<State>, State> stateMap = new HashMap<>();
        Queue<Set<State>> queue = new LinkedList<>();

        Set<State> startClosure = epsilonClosure(Collections.singleton(nfa.startState));
        stateMap.put(startClosure, dfaStart);
        queue.add(startClosure);

        while (!queue.isEmpty()) {
            Set<State> currentStates = queue.poll();
            State currentDFAState = stateMap.get(currentStates);

            // Check if current state is final
            boolean isFinal = currentStates.stream().anyMatch(s -> s.isFinal);
            if (isFinal) {
                dfaFinals.add(currentDFAState);
            }

            // Collect all possible symbols (excluding epsilon)
            Set<Character> symbols = new HashSet<>();
            for (State s : currentStates) {
                symbols.addAll(s.transitions.keySet());
            }
            symbols.remove('\0');

            for (char sym : symbols) {
                Set<State> nextStates = move(currentStates, sym);
                Set<State> nextClosure = epsilonClosure(nextStates);

                if (!stateMap.containsKey(nextClosure)) {
                    State newState = new State(stateMap.size() + 1, false);
                    stateMap.put(nextClosure, newState);
                    queue.add(nextClosure);
                }
                State nextDFAState = stateMap.get(nextClosure);
                transitionTable.computeIfAbsent(currentDFAState, k -> new HashMap<>()).put(sym, nextDFAState);
            }
        }

        DFA dfa = new DFA(dfaStart, dfaFinals);
        dfa.transitionTable = transitionTable;

        // Merge final states into one
        if (dfa.finalStates.size() > 1) {
            mergeFinalStates(dfa);
        }

        return dfa;
    }

    private static void mergeFinalStates(DFA dfa) {
        State singleFinal = new State(getNextStateId(dfa), true);
        Set<State> oldFinals = new HashSet<>(dfa.finalStates);
        dfa.finalStates.clear();
        dfa.finalStates.add(singleFinal);

        Map<State, Map<Character, State>> newTransitions = new HashMap<>();

        for (State from : dfa.transitionTable.keySet()) {
            Map<Character, State> transitions = new HashMap<>(dfa.transitionTable.get(from));
            for (Map.Entry<Character, State> entry : transitions.entrySet()) {
                if (oldFinals.contains(entry.getValue())) {
                    transitions.put(entry.getKey(), singleFinal);
                }
            }
            if (oldFinals.contains(from)) {
                newTransitions.merge(singleFinal, transitions, (old, curr) -> {
                    old.putAll(curr);
                    return old;
                });
            } else {
                newTransitions.put(from, transitions);
            }
        }

        if (!newTransitions.containsKey(singleFinal)) {
            newTransitions.put(singleFinal, new HashMap<>());
        }

        dfa.transitionTable = newTransitions;

        if (oldFinals.contains(dfa.startState)) {
            dfa.startState = singleFinal;
        }
    }

    private static int getNextStateId(DFA dfa) {
        return dfa.transitionTable.keySet().stream()
                .mapToInt(s -> s.id)
                .max().orElse(0) + 1;
    }

    private static Set<State> epsilonClosure(Set<State> states) {
        Set<State> closure = new HashSet<>(states);
        Queue<State> queue = new LinkedList<>(states);

        while (!queue.isEmpty()) {
            State s = queue.poll();
            Set<State> epsilonTrans = s.transitions.getOrDefault('\0', Collections.emptySet());
            for (State t : epsilonTrans) {
                if (!closure.contains(t)) {
                    closure.add(t);
                    queue.add(t);
                }
            }
        }
        return closure;
    }

    private static Set<State> move(Set<State> states, char sym) {
        Set<State> result = new HashSet<>();
        for (State s : states) {
            Set<State> trans = s.transitions.getOrDefault(sym, Collections.emptySet());
            result.addAll(trans);
        }
        return result;
    }

    public static void printDFATransitionTable(DFA dfa) {
        System.out.println("DFA Transition Table:");
        System.out.println("State\tSymbol\tNext State");
        for (State from : dfa.transitionTable.keySet()) {
            for (Map.Entry<Character, State> entry : dfa.transitionTable.get(from).entrySet()) {
                System.out.printf("%d\t%c\t%d%n", from.id, entry.getKey(), entry.getValue().id);
            }
        }
        System.out.println("Start State: " + dfa.startState.id);
        System.out.println("Final State: " + dfa.finalStates.iterator().next().id);
    }
}