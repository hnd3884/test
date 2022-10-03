package com.sun.org.apache.xerces.internal.impl.dtd;

import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarDescription;
import java.util.HashMap;
import java.util.Map;

public class DTDGrammarBucket
{
    protected Map<XMLDTDDescription, DTDGrammar> fGrammars;
    protected DTDGrammar fActiveGrammar;
    protected boolean fIsStandalone;
    
    public DTDGrammarBucket() {
        this.fGrammars = new HashMap<XMLDTDDescription, DTDGrammar>();
    }
    
    public void putGrammar(final DTDGrammar grammar) {
        final XMLDTDDescription desc = (XMLDTDDescription)grammar.getGrammarDescription();
        this.fGrammars.put(desc, grammar);
    }
    
    public DTDGrammar getGrammar(final XMLGrammarDescription desc) {
        return this.fGrammars.get(desc);
    }
    
    public void clear() {
        this.fGrammars.clear();
        this.fActiveGrammar = null;
        this.fIsStandalone = false;
    }
    
    void setStandalone(final boolean standalone) {
        this.fIsStandalone = standalone;
    }
    
    boolean getStandalone() {
        return this.fIsStandalone;
    }
    
    void setActiveGrammar(final DTDGrammar grammar) {
        this.fActiveGrammar = grammar;
    }
    
    DTDGrammar getActiveGrammar() {
        return this.fActiveGrammar;
    }
}
