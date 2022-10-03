package org.apache.xerces.jaxp.validation;

import org.apache.xerces.xni.grammars.XMLGrammarPool;

final class XMLSchema extends AbstractXMLSchema
{
    private final XMLGrammarPool fGrammarPool;
    private final boolean fFullyComposed;
    
    public XMLSchema(final XMLGrammarPool xmlGrammarPool, final String s) {
        this(xmlGrammarPool, true, s);
    }
    
    public XMLSchema(final XMLGrammarPool fGrammarPool, final boolean fFullyComposed, final String s) {
        super(s);
        this.fGrammarPool = fGrammarPool;
        this.fFullyComposed = fFullyComposed;
    }
    
    public XMLGrammarPool getGrammarPool() {
        return this.fGrammarPool;
    }
    
    public boolean isFullyComposed() {
        return this.fFullyComposed;
    }
}
