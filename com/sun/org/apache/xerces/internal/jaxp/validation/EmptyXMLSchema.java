package com.sun.org.apache.xerces.internal.jaxp.validation;

import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarDescription;
import com.sun.org.apache.xerces.internal.xni.grammars.Grammar;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarPool;

final class EmptyXMLSchema extends AbstractXMLSchema implements XMLGrammarPool
{
    private static final Grammar[] ZERO_LENGTH_GRAMMAR_ARRAY;
    
    public EmptyXMLSchema() {
    }
    
    @Override
    public Grammar[] retrieveInitialGrammarSet(final String grammarType) {
        return EmptyXMLSchema.ZERO_LENGTH_GRAMMAR_ARRAY;
    }
    
    @Override
    public void cacheGrammars(final String grammarType, final Grammar[] grammars) {
    }
    
    @Override
    public Grammar retrieveGrammar(final XMLGrammarDescription desc) {
        return null;
    }
    
    @Override
    public void lockPool() {
    }
    
    @Override
    public void unlockPool() {
    }
    
    @Override
    public void clear() {
    }
    
    @Override
    public XMLGrammarPool getGrammarPool() {
        return this;
    }
    
    @Override
    public boolean isFullyComposed() {
        return true;
    }
    
    static {
        ZERO_LENGTH_GRAMMAR_ARRAY = new Grammar[0];
    }
}
