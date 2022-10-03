package org.apache.xerces.impl.validation;

import java.util.ArrayList;

public class ValidationManager
{
    protected final ArrayList fVSs;
    protected boolean fGrammarFound;
    protected boolean fCachedDTD;
    
    public ValidationManager() {
        this.fVSs = new ArrayList();
        this.fGrammarFound = false;
        this.fCachedDTD = false;
    }
    
    public final void addValidationState(final ValidationState validationState) {
        this.fVSs.add(validationState);
    }
    
    public final void setEntityState(final EntityState entityState) {
        for (int i = this.fVSs.size() - 1; i >= 0; --i) {
            ((ValidationState)this.fVSs.get(i)).setEntityState(entityState);
        }
    }
    
    public final void setGrammarFound(final boolean fGrammarFound) {
        this.fGrammarFound = fGrammarFound;
    }
    
    public final boolean isGrammarFound() {
        return this.fGrammarFound;
    }
    
    public final void setCachedDTD(final boolean fCachedDTD) {
        this.fCachedDTD = fCachedDTD;
    }
    
    public final boolean isCachedDTD() {
        return this.fCachedDTD;
    }
    
    public final void reset() {
        this.fVSs.clear();
        this.fGrammarFound = false;
        this.fCachedDTD = false;
    }
}
