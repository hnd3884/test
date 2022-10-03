package com.sun.org.apache.xerces.internal.jaxp.validation;

import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarDescription;
import com.sun.org.apache.xerces.internal.xni.grammars.Grammar;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarPool;

final class SimpleXMLSchema extends AbstractXMLSchema implements XMLGrammarPool
{
    private static final Grammar[] ZERO_LENGTH_GRAMMAR_ARRAY;
    private Grammar fGrammar;
    private Grammar[] fGrammars;
    private XMLGrammarDescription fGrammarDescription;
    
    public SimpleXMLSchema(final Grammar grammar) {
        this.fGrammar = grammar;
        this.fGrammars = new Grammar[] { grammar };
        this.fGrammarDescription = grammar.getGrammarDescription();
    }
    
    @Override
    public Grammar[] retrieveInitialGrammarSet(final String grammarType) {
        return "http://www.w3.org/2001/XMLSchema".equals(grammarType) ? this.fGrammars.clone() : SimpleXMLSchema.ZERO_LENGTH_GRAMMAR_ARRAY;
    }
    
    @Override
    public void cacheGrammars(final String grammarType, final Grammar[] grammars) {
    }
    
    @Override
    public Grammar retrieveGrammar(final XMLGrammarDescription desc) {
        return this.fGrammarDescription.equals(desc) ? this.fGrammar : null;
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
