package org.apache.lucene.util.automaton;

public class TooComplexToDeterminizeException extends RuntimeException
{
    private final transient Automaton automaton;
    private final transient RegExp regExp;
    private final transient int maxDeterminizedStates;
    
    public TooComplexToDeterminizeException(final RegExp regExp, final TooComplexToDeterminizeException cause) {
        super("Determinizing " + regExp.getOriginalString() + " would result in more than " + cause.maxDeterminizedStates + " states.", cause);
        this.regExp = regExp;
        this.automaton = cause.automaton;
        this.maxDeterminizedStates = cause.maxDeterminizedStates;
    }
    
    public TooComplexToDeterminizeException(final Automaton automaton, final int maxDeterminizedStates) {
        super("Determinizing automaton with " + automaton.getNumStates() + " states and " + automaton.getNumTransitions() + " transitions would result in more than " + maxDeterminizedStates + " states.");
        this.automaton = automaton;
        this.regExp = null;
        this.maxDeterminizedStates = maxDeterminizedStates;
    }
    
    public Automaton getAutomaton() {
        return this.automaton;
    }
    
    public RegExp getRegExp() {
        return this.regExp;
    }
    
    public int getMaxDeterminizedStates() {
        return this.maxDeterminizedStates;
    }
}
