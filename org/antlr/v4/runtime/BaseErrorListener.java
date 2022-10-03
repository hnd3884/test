package org.antlr.v4.runtime;

import org.antlr.v4.runtime.atn.ATNConfigSet;
import java.util.BitSet;
import org.antlr.v4.runtime.dfa.DFA;

public class BaseErrorListener implements ANTLRErrorListener
{
    @Override
    public void syntaxError(final Recognizer<?, ?> recognizer, final Object offendingSymbol, final int line, final int charPositionInLine, final String msg, final RecognitionException e) {
    }
    
    @Override
    public void reportAmbiguity(final Parser recognizer, final DFA dfa, final int startIndex, final int stopIndex, final boolean exact, final BitSet ambigAlts, final ATNConfigSet configs) {
    }
    
    @Override
    public void reportAttemptingFullContext(final Parser recognizer, final DFA dfa, final int startIndex, final int stopIndex, final BitSet conflictingAlts, final ATNConfigSet configs) {
    }
    
    @Override
    public void reportContextSensitivity(final Parser recognizer, final DFA dfa, final int startIndex, final int stopIndex, final int prediction, final ATNConfigSet configs) {
    }
}
