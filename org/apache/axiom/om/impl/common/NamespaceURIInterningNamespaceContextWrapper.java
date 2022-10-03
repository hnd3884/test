package org.apache.axiom.om.impl.common;

import java.util.Iterator;
import javax.xml.namespace.NamespaceContext;

class NamespaceURIInterningNamespaceContextWrapper implements NamespaceContext
{
    private final NamespaceContext parent;
    
    NamespaceURIInterningNamespaceContextWrapper(final NamespaceContext parent) {
        this.parent = parent;
    }
    
    NamespaceContext getParent() {
        return this.parent;
    }
    
    private static String intern(final String s) {
        return (s == null) ? null : s.intern();
    }
    
    public String getNamespaceURI(final String prefix) {
        return intern(this.parent.getNamespaceURI(prefix));
    }
    
    public String getPrefix(final String namespaceURI) {
        return this.parent.getPrefix(namespaceURI);
    }
    
    public Iterator getPrefixes(final String namespaceURI) {
        return this.parent.getPrefixes(namespaceURI);
    }
}
