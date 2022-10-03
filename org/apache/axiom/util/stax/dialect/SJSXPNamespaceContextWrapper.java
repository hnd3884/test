package org.apache.axiom.util.stax.dialect;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import javax.xml.namespace.NamespaceContext;

class SJSXPNamespaceContextWrapper implements NamespaceContext
{
    private final NamespaceContext parent;
    
    public SJSXPNamespaceContextWrapper(final NamespaceContext parent) {
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
        final List prefixes = new ArrayList(5);
        final Iterator it = this.parent.getPrefixes(namespaceURI);
        while (it.hasNext()) {
            final String prefix = it.next();
            final String actualNamespaceURI = this.parent.getNamespaceURI(prefix);
            if (namespaceURI == actualNamespaceURI || (namespaceURI != null && namespaceURI.equals(actualNamespaceURI))) {
                prefixes.add(prefix);
            }
        }
        return prefixes.iterator();
    }
}
