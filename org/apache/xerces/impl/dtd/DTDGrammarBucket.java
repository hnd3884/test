package org.apache.xerces.impl.dtd;

import org.apache.xerces.xni.grammars.XMLGrammarDescription;
import java.util.Hashtable;

public class DTDGrammarBucket
{
    protected final Hashtable fGrammars;
    protected DTDGrammar fActiveGrammar;
    protected boolean fIsStandalone;
    
    public DTDGrammarBucket() {
        this.fGrammars = new Hashtable();
    }
    
    public void putGrammar(final DTDGrammar dtdGrammar) {
        this.fGrammars.put(dtdGrammar.getGrammarDescription(), dtdGrammar);
    }
    
    public DTDGrammar getGrammar(final XMLGrammarDescription xmlGrammarDescription) {
        return this.fGrammars.get(xmlGrammarDescription);
    }
    
    public void clear() {
        this.fGrammars.clear();
        this.fActiveGrammar = null;
        this.fIsStandalone = false;
    }
    
    void setStandalone(final boolean fIsStandalone) {
        this.fIsStandalone = fIsStandalone;
    }
    
    boolean getStandalone() {
        return this.fIsStandalone;
    }
    
    void setActiveGrammar(final DTDGrammar fActiveGrammar) {
        this.fActiveGrammar = fActiveGrammar;
    }
    
    DTDGrammar getActiveGrammar() {
        return this.fActiveGrammar;
    }
}
