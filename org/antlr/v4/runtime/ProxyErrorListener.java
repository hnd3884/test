package org.antlr.v4.runtime;

import org.antlr.v4.runtime.atn.ATNConfigSet;
import java.util.BitSet;
import org.antlr.v4.runtime.dfa.DFA;
import java.util.Iterator;
import java.util.Collection;

public class ProxyErrorListener implements ANTLRErrorListener
{
    private final Collection<? extends ANTLRErrorListener> delegates;
    
    public ProxyErrorListener(final Collection<? extends ANTLRErrorListener> delegates) {
        if (delegates == null) {
            throw new NullPointerException("delegates");
        }
        this.delegates = delegates;
    }
    
    @Override
    public void syntaxError(final Recognizer<?, ?> recognizer, final Object offendingSymbol, final int line, final int charPositionInLine, final String msg, final RecognitionException e) {
        for (final ANTLRErrorListener listener : this.delegates) {
            listener.syntaxError(recognizer, offendingSymbol, line, charPositionInLine, msg, e);
        }
    }
    
    @Override
    public void reportAmbiguity(final Parser recognizer, final DFA dfa, final int startIndex, final int stopIndex, final boolean exact, final BitSet ambigAlts, final ATNConfigSet configs) {
        for (final ANTLRErrorListener listener : this.delegates) {
            listener.reportAmbiguity(recognizer, dfa, startIndex, stopIndex, exact, ambigAlts, configs);
        }
    }
    
    @Override
    public void reportAttemptingFullContext(final Parser recognizer, final DFA dfa, final int startIndex, final int stopIndex, final BitSet conflictingAlts, final ATNConfigSet configs) {
        for (final ANTLRErrorListener listener : this.delegates) {
            listener.reportAttemptingFullContext(recognizer, dfa, startIndex, stopIndex, conflictingAlts, configs);
        }
    }
    
    @Override
    public void reportContextSensitivity(final Parser recognizer, final DFA dfa, final int startIndex, final int stopIndex, final int prediction, final ATNConfigSet configs) {
        for (final ANTLRErrorListener listener : this.delegates) {
            listener.reportContextSensitivity(recognizer, dfa, startIndex, stopIndex, prediction, configs);
        }
    }
}
