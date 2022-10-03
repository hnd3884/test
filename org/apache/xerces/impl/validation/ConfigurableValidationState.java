package org.apache.xerces.impl.validation;

import java.util.Iterator;

public class ConfigurableValidationState extends ValidationState
{
    protected boolean fIdIdrefChecking;
    private boolean fUnparsedEntityChecking;
    
    public ConfigurableValidationState() {
        this.fIdIdrefChecking = true;
        this.fUnparsedEntityChecking = true;
    }
    
    public final void setIdIdrefChecking(final boolean fIdIdrefChecking) {
        this.fIdIdrefChecking = fIdIdrefChecking;
    }
    
    public final void setUnparsedEntityChecking(final boolean fUnparsedEntityChecking) {
        this.fUnparsedEntityChecking = fUnparsedEntityChecking;
    }
    
    public final Iterator checkIDRefID() {
        return this.fIdIdrefChecking ? super.checkIDRefID() : null;
    }
    
    public boolean isIdDeclared(final String s) {
        return this.fIdIdrefChecking && super.isIdDeclared(s);
    }
    
    public final boolean isEntityDeclared(final String s) {
        return !this.fUnparsedEntityChecking || super.isEntityDeclared(s);
    }
    
    public final boolean isEntityUnparsed(final String s) {
        return !this.fUnparsedEntityChecking || super.isEntityUnparsed(s);
    }
    
    public void addId(final String s) {
        if (this.fIdIdrefChecking) {
            super.addId(s);
        }
    }
    
    public final void addIdRef(final String s) {
        if (this.fIdIdrefChecking) {
            super.addIdRef(s);
        }
    }
}
