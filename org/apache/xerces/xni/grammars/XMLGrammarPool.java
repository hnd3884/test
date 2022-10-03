package org.apache.xerces.xni.grammars;

public interface XMLGrammarPool
{
    Grammar[] retrieveInitialGrammarSet(final String p0);
    
    void cacheGrammars(final String p0, final Grammar[] p1);
    
    Grammar retrieveGrammar(final XMLGrammarDescription p0);
    
    void lockPool();
    
    void unlockPool();
    
    void clear();
}
