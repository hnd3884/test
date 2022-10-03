package org.apache.xerces.jaxp.validation;

import org.apache.xerces.xni.grammars.XMLGrammarDescription;
import org.apache.xerces.xni.grammars.Grammar;
import org.apache.xerces.xni.grammars.XMLGrammarPool;

final class SimpleXMLSchema extends AbstractXMLSchema implements XMLGrammarPool
{
    private static final Grammar[] ZERO_LENGTH_GRAMMAR_ARRAY;
    private final Grammar fGrammar;
    private final Grammar[] fGrammars;
    private final XMLGrammarDescription fGrammarDescription;
    
    public SimpleXMLSchema(final Grammar fGrammar, final String s) {
        super(s);
        this.fGrammar = fGrammar;
        this.fGrammars = new Grammar[] { fGrammar };
        this.fGrammarDescription = fGrammar.getGrammarDescription();
    }
    
    public Grammar[] retrieveInitialGrammarSet(final String s) {
        return "http://www.w3.org/2001/XMLSchema".equals(s) ? this.fGrammars.clone() : SimpleXMLSchema.ZERO_LENGTH_GRAMMAR_ARRAY;
    }
    
    public void cacheGrammars(final String s, final Grammar[] array) {
    }
    
    public Grammar retrieveGrammar(final XMLGrammarDescription xmlGrammarDescription) {
        return this.fGrammarDescription.equals(xmlGrammarDescription) ? this.fGrammar : null;
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
