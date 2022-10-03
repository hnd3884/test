package org.antlr.v4.runtime;

import java.util.Iterator;
import org.antlr.v4.runtime.atn.ATNConfig;
import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.atn.ATNConfigSet;
import java.util.BitSet;
import org.antlr.v4.runtime.dfa.DFA;

public class DiagnosticErrorListener extends BaseErrorListener
{
    protected final boolean exactOnly;
    
    public DiagnosticErrorListener() {
        this(true);
    }
    
    public DiagnosticErrorListener(final boolean exactOnly) {
        this.exactOnly = exactOnly;
    }
    
    @Override
    public void reportAmbiguity(final Parser recognizer, final DFA dfa, final int startIndex, final int stopIndex, final boolean exact, final BitSet ambigAlts, final ATNConfigSet configs) {
        if (this.exactOnly && !exact) {
            return;
        }
        final String format = "reportAmbiguity d=%s: ambigAlts=%s, input='%s'";
        final String decision = this.getDecisionDescription(recognizer, dfa);
        final BitSet conflictingAlts = this.getConflictingAlts(ambigAlts, configs);
        final String text = recognizer.getTokenStream().getText(Interval.of(startIndex, stopIndex));
        final String message = String.format(format, decision, conflictingAlts, text);
        recognizer.notifyErrorListeners(message);
    }
    
    @Override
    public void reportAttemptingFullContext(final Parser recognizer, final DFA dfa, final int startIndex, final int stopIndex, final BitSet conflictingAlts, final ATNConfigSet configs) {
        final String format = "reportAttemptingFullContext d=%s, input='%s'";
        final String decision = this.getDecisionDescription(recognizer, dfa);
        final String text = recognizer.getTokenStream().getText(Interval.of(startIndex, stopIndex));
        final String message = String.format(format, decision, text);
        recognizer.notifyErrorListeners(message);
    }
    
    @Override
    public void reportContextSensitivity(final Parser recognizer, final DFA dfa, final int startIndex, final int stopIndex, final int prediction, final ATNConfigSet configs) {
        final String format = "reportContextSensitivity d=%s, input='%s'";
        final String decision = this.getDecisionDescription(recognizer, dfa);
        final String text = recognizer.getTokenStream().getText(Interval.of(startIndex, stopIndex));
        final String message = String.format(format, decision, text);
        recognizer.notifyErrorListeners(message);
    }
    
    protected String getDecisionDescription(final Parser recognizer, final DFA dfa) {
        final int decision = dfa.decision;
        final int ruleIndex = dfa.atnStartState.ruleIndex;
        final String[] ruleNames = recognizer.getRuleNames();
        if (ruleIndex < 0 || ruleIndex >= ruleNames.length) {
            return String.valueOf(decision);
        }
        final String ruleName = ruleNames[ruleIndex];
        if (ruleName == null || ruleName.isEmpty()) {
            return String.valueOf(decision);
        }
        return String.format("%d (%s)", decision, ruleName);
    }
    
    protected BitSet getConflictingAlts(final BitSet reportedAlts, final ATNConfigSet configs) {
        if (reportedAlts != null) {
            return reportedAlts;
        }
        final BitSet result = new BitSet();
        for (final ATNConfig config : configs) {
            result.set(config.alt);
        }
        return result;
    }
}
