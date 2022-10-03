package com.sun.org.apache.xerces.internal.jaxp.validation;

import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarDescription;
import com.sun.org.apache.xerces.internal.xni.grammars.Grammar;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarPool;

final class ReadOnlyGrammarPool implements XMLGrammarPool
{
    private final XMLGrammarPool core;
    
    public ReadOnlyGrammarPool(final XMLGrammarPool pool) {
        this.core = pool;
    }
    
    @Override
    public void cacheGrammars(final String grammarType, final Grammar[] grammars) {
    }
    
    @Override
    public void clear() {
    }
    
    @Override
    public void lockPool() {
    }
    
    @Override
    public Grammar retrieveGrammar(final XMLGrammarDescription desc) {
        return this.core.retrieveGrammar(desc);
    }
    
    @Override
    public Grammar[] retrieveInitialGrammarSet(final String grammarType) {
        return this.core.retrieveInitialGrammarSet(grammarType);
    }
    
    @Override
    public void unlockPool() {
    }
}
