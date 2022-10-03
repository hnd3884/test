package org.apache.xerces.jaxp.validation;

import org.apache.xerces.xni.grammars.XMLGrammarDescription;
import org.apache.xerces.xni.grammars.Grammar;
import org.apache.xerces.xni.grammars.XMLGrammarPool;

final class EmptyXMLSchema extends AbstractXMLSchema implements XMLGrammarPool
{
    private static final Grammar[] ZERO_LENGTH_GRAMMAR_ARRAY;
    
    public EmptyXMLSchema(final String s) {
        super(s);
    }
    
    public Grammar[] retrieveInitialGrammarSet(final String s) {
        return EmptyXMLSchema.ZERO_LENGTH_GRAMMAR_ARRAY;
    }
    
    public void cacheGrammars(final String s, final Grammar[] array) {
    }
    
    public Grammar retrieveGrammar(final XMLGrammarDescription xmlGrammarDescription) {
        return null;
    }
    
    public void lockPool() {
    }
    
    public void unlockPool() {
    }
    
    public void clear() {
    }
    
    public XMLGrammarPool getGrammarPool() {
        return this;
    }
    
    public boolean isFullyComposed() {
        return true;
    }
    
    static {
        ZERO_LENGTH_GRAMMAR_ARRAY = new Grammar[0];
    }
}
