package org.apache.axiom.om;

public class OMXMLStreamReaderConfiguration
{
    private boolean preserveNamespaceContext;
    private boolean namespaceURIInterning;
    
    public boolean isPreserveNamespaceContext() {
        return this.preserveNamespaceContext;
    }
    
    public void setPreserveNamespaceContext(final boolean preserveNamespaceContext) {
        this.preserveNamespaceContext = preserveNamespaceContext;
    }
    
    public boolean isNamespaceURIInterning() {
        return this.namespaceURIInterning;
    }
    
    public void setNamespaceURIInterning(final boolean namespaceURIInterning) {
        this.namespaceURIInterning = namespaceURIInterning;
    }
}
