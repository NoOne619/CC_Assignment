package Lexer;
public class NFA {
    State startState;
    State finalState;

    public NFA(State startState, State finalState) {
        this.startState = startState;
        this.finalState = finalState;
    }
}