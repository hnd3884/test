package org.antlr.v4.runtime;

public interface ANTLRErrorStrategy
{
    void reset(final Parser p0);
    
    Token recoverInline(final Parser p0) throws RecognitionException;
    
    void recover(final Parser p0, final RecognitionException p1) throws RecognitionException;
    
    void sync(final Parser p0) throws RecognitionException;
    
    boolean inErrorRecoveryMode(final Parser p0);
    
    void reportMatch(final Parser p0);
    
    void reportError(final Parser p0, final RecognitionException p1);
}
