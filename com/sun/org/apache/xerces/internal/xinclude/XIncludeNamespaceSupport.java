package com.sun.org.apache.xerces.internal.xinclude;

import com.sun.org.apache.xerces.internal.xni.NamespaceContext;

public class XIncludeNamespaceSupport extends MultipleScopeNamespaceSupport
{
    private boolean[] fValidContext;
    
    public XIncludeNamespaceSupport() {
        this.fValidContext = new boolean[8];
    }
    
    public XIncludeNamespaceSupport(final NamespaceContext context) {
        super(context);
        this.fValidContext = new boolean[8];
    }
    
    @Override
    public void pushContext() {
        super.pushContext();
        if (this.fCurrentContext + 1 == this.fValidContext.length) {
            final boolean[] contextarray = new boolean[this.fValidContext.length * 2];
            System.arraycopy(this.fValidContext, 0, contextarray, 0, this.fValidContext.length);
            this.fValidContext = contextarray;
        }
        this.fValidContext[this.fCurrentContext] = true;
    }
    
    public void setContextInvalid() {
        this.fValidContext[this.fCurrentContext] = false;
    }
    
    public String getURIFromIncludeParent(final String prefix) {
        int lastValidContext;
        for (lastValidContext = this.fCurrentContext - 1; lastValidContext > 0 && !this.fValidContext[lastValidContext]; --lastValidContext) {}
        return this.getURI(prefix, lastValidContext);
    }
}
