package com.sun.org.apache.xerces.internal.impl.validation;

import java.util.Vector;

public class ValidationManager
{
    protected final Vector fVSs;
    protected boolean fGrammarFound;
    protected boolean fCachedDTD;
    
    public ValidationManager() {
        this.fVSs = new Vector();
        this.fGrammarFound = false;
        this.fCachedDTD = false;
    }
    
    public final void addValidationState(final ValidationState vs) {
        this.fVSs.addElement(vs);
    }
    
    public final void setEntityState(final EntityState state) {
        for (int i = this.fVSs.size() - 1; i >= 0; --i) {
            this.fVSs.elementAt(i).setEntityState(state);
        }
    }
    
    public final void setGrammarFound(final boolean grammar) {
        this.fGrammarFound = grammar;
    }
    
    public final boolean isGrammarFound() {
        return this.fGrammarFound;
    }
    
    public final void setCachedDTD(final boolean cachedDTD) {
        this.fCachedDTD = cachedDTD;
    }
    
    public final boolean isCachedDTD() {
        return this.fCachedDTD;
    }
    
    public final void reset() {
        this.fVSs.removeAllElements();
        this.fGrammarFound = false;
        this.fCachedDTD = false;
    }
}
