package org.apache.xerces.impl.xs;

import org.apache.xerces.impl.validation.ConfigurableValidationState;

final class XSValidationState extends ConfigurableValidationState
{
    private IDContext fIDContext;
    
    public XSValidationState() {
    }
    
    public void addId(final String s) {
        if (this.fIDContext == null) {
            super.addId(s);
        }
        else if (this.fIdIdrefChecking) {
            this.fIDContext.add(s);
        }
    }
    
    public boolean isIdDeclared(final String s) {
        if (this.fIDContext == null) {
            return super.isIdDeclared(s);
        }
        return this.fIdIdrefChecking && this.fIDContext.isDeclared(s);
    }
    
    void setIDContext(final IDContext fidContext) {
        this.fIDContext = fidContext;
    }
    
    protected boolean containsID(final String s) {
        return (this.fIDContext == null) ? super.containsID(s) : this.fIDContext.containsID(s);
    }
}
