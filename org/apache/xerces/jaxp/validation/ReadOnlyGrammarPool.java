package org.apache.xerces.jaxp.validation;

import org.apache.xerces.xni.grammars.XMLGrammarDescription;
import org.apache.xerces.xni.grammars.Grammar;
import org.apache.xerces.xni.grammars.XMLGrammarPool;

final class ReadOnlyGrammarPool implements XMLGrammarPool
{
    private final XMLGrammarPool core;
    
    public ReadOnlyGrammarPool(final XMLGrammarPool core) {
        this.core = core;
    }
    
    public void cacheGrammars(final String s, final Grammar[] array) {
    }
    
    public void clear() {
    }
    
    public void lockPool() {
    }
    
    public Grammar retrieveGrammar(final XMLGrammarDescription xmlGrammarDescription) {
        return this.core.retrieveGrammar(xmlGrammarDescription);
    }
    
    public Grammar[] retrieveInitialGrammarSet(final String s) {
        return this.core.retrieveInitialGrammarSet(s);
    }
    
    public void unlockPool() {
    }
}
