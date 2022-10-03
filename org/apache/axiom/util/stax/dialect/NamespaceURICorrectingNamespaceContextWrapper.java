package org.apache.axiom.util.stax.dialect;

import java.util.Iterator;
import javax.xml.namespace.NamespaceContext;

class NamespaceURICorrectingNamespaceContextWrapper implements NamespaceContext
{
    private final NamespaceContext parent;
    
    public NamespaceURICorrectingNamespaceContextWrapper(final NamespaceContext parent) {
        this.parent = parent;
    }
    
    public String getNamespaceURI(final String prefix) {
        final String namespaceURI = this.parent.getNamespaceURI(prefix);
        return (namespaceURI == null) ? "" : namespaceURI;
    }
    
    public String getPrefix(final String namespaceURI) {
        return this.parent.getPrefix(namespaceURI);
    }
    
    public Iterator getPrefixes(final String namespaceURI) {
        return this.parent.getPrefixes(namespaceURI);
    }
}
