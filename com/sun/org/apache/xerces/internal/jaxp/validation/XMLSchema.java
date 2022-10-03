package com.sun.org.apache.xerces.internal.jaxp.validation;

import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarPool;

final class XMLSchema extends AbstractXMLSchema
{
    private final XMLGrammarPool fGrammarPool;
    
    public XMLSchema(final XMLGrammarPool grammarPool) {
        this.fGrammarPool = grammarPool;
    }
    
    @Override
    public XMLGrammarPool getGrammarPool() {
        return this.fGrammarPool;
    }
    
    @Override
    public boolean isFullyComposed() {
        return true;
    }
}
