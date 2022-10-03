package org.antlr.v4.runtime;

import org.antlr.v4.runtime.atn.ATNConfigSet;
import java.util.BitSet;
import org.antlr.v4.runtime.dfa.DFA;

public interface ANTLRErrorListener
{
    void syntaxError(final Recognizer<?, ?> p0, final Object p1, final int p2, final int p3, final String p4, final RecognitionException p5);
    
    void reportAmbiguity(final Parser p0, final DFA p1, final int p2, final int p3, final boolean p4, final BitSet p5, final ATNConfigSet p6);
    
    void reportAttemptingFullContext(final Parser p0, final DFA p1, final int p2, final int p3, final BitSet p4, final ATNConfigSet p5);
    
    void reportContextSensitivity(final Parser p0, final DFA p1, final int p2, final int p3, final int p4, final ATNConfigSet p5);
}
