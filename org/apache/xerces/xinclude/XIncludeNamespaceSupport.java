package org.apache.xerces.xinclude;

import org.apache.xerces.xni.NamespaceContext;

public class XIncludeNamespaceSupport extends MultipleScopeNamespaceSupport
{
    private boolean[] fValidContext;
    
    public XIncludeNamespaceSupport() {
        this.fValidContext = new boolean[8];
    }
    
    public XIncludeNamespaceSupport(final NamespaceContext namespaceContext) {
        super(namespaceContext);
        this.fValidContext = new boolean[8];
    }
    
    public void pushContext() {
        super.pushContext();
        if (this.fCurrentContext + 1 == this.fValidContext.length) {
            final boolean[] fValidContext = new boolean[this.fValidContext.length * 2];
            System.arraycopy(this.fValidContext, 0, fValidContext, 0, this.fValidContext.length);
            this.fValidContext = fValidContext;
        }
        this.fValidContext[this.fCurrentContext] = true;
    }
    
    public void setContextInvalid() {
        this.fValidContext[this.fCurrentContext] = false;
    }
    
    public String getURIFromIncludeParent(final String s) {
        int n;
        for (n = this.fCurrentContext - 1; n > 0 && !this.fValidContext[n]; --n) {}
        return this.getURI(s, n);
    }
}
