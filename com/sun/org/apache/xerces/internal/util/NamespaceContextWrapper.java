package com.sun.org.apache.xerces.internal.util;

import java.util.Vector;
import java.util.Iterator;
import javax.xml.namespace.NamespaceContext;

public class NamespaceContextWrapper implements NamespaceContext
{
    private com.sun.org.apache.xerces.internal.xni.NamespaceContext fNamespaceContext;
    
    public NamespaceContextWrapper(final NamespaceSupport namespaceContext) {
        this.fNamespaceContext = namespaceContext;
    }
    
    @Override
    public String getNamespaceURI(final String prefix) {
        if (prefix == null) {
            throw new IllegalArgumentException("Prefix can't be null");
        }
        return this.fNamespaceContext.getURI(prefix.intern());
    }
    
    @Override
    public String getPrefix(final String namespaceURI) {
        if (namespaceURI == null) {
            throw new IllegalArgumentException("URI can't be null.");
        }
        return this.fNamespaceContext.getPrefix(namespaceURI.intern());
    }
    
    @Override
    public Iterator getPrefixes(final String namespaceURI) {
        if (namespaceURI == null) {
            throw new IllegalArgumentException("URI can't be null.");
        }
        final Vector vector = ((NamespaceSupport)this.fNamespaceContext).getPrefixes(namespaceURI.intern());
        return vector.iterator();
    }
    
    public com.sun.org.apache.xerces.internal.xni.NamespaceContext getNamespaceContext() {
        return this.fNamespaceContext;
    }
}
