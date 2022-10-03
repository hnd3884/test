package org.antlr.v4.runtime;

import java.util.Locale;
import org.antlr.v4.runtime.atn.PredicateTransition;
import org.antlr.v4.runtime.atn.AbstractPredicateTransition;
import org.antlr.v4.runtime.atn.ParserATNSimulator;
import org.antlr.v4.runtime.atn.ATNState;

public class FailedPredicateException extends RecognitionException
{
    private final int ruleIndex;
    private final int predicateIndex;
    private final String predicate;
    
    public FailedPredicateException(final Parser recognizer) {
        this(recognizer, null);
    }
    
    public FailedPredicateException(final Parser recognizer, final String predicate) {
        this(recognizer, predicate, null);
    }
    
    public FailedPredicateException(final Parser recognizer, final String predicate, final String message) {
        super(formatMessage(predicate, message), recognizer, recognizer.getInputStream(), recognizer._ctx);
        final ATNState s = ((Recognizer<Symbol, ParserATNSimulator>)recognizer).getInterpreter().atn.states.get(recognizer.getState());
        final AbstractPredicateTransition trans = (AbstractPredicateTransition)s.transition(0);
        if (trans instanceof PredicateTransition) {
            this.ruleIndex = ((PredicateTransition)trans).ruleIndex;
            this.predicateIndex = ((PredicateTransition)trans).predIndex;
        }
        else {
            this.ruleIndex = 0;
            this.predicateIndex = 0;
        }
        this.predicate = predicate;
        this.setOffendingToken(recognizer.getCurrentToken());
    }
    
    public int getRuleIndex() {
        return this.ruleIndex;
    }
    
    public int getPredIndex() {
        return this.predicateIndex;
    }
    
    public String getPredicate() {
        return this.predicate;
    }
    
    private static String formatMessage(final String predicate, final String message) {
        if (message != null) {
            return message;
        }
        return String.format(Locale.getDefault(), "failed predicate: {%s}?", predicate);
    }
}
