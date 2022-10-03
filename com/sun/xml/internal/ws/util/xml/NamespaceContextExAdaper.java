package com.sun.xml.internal.ws.util.xml;

import java.util.Iterator;
import javax.xml.namespace.NamespaceContext;
import com.sun.xml.internal.org.jvnet.staxex.NamespaceContextEx;

public class NamespaceContextExAdaper implements NamespaceContextEx
{
    private final NamespaceContext nsContext;
    
    public NamespaceContextExAdaper(final NamespaceContext nsContext) {
        this.nsContext = nsContext;
    }
    
    @Override
    public Iterator<Binding> iterator() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public String getNamespaceURI(final String prefix) {
        return this.nsContext.getNamespaceURI(prefix);
    }
    
    @Override
    public String getPrefix(final String namespaceURI) {
        return this.nsContext.getPrefix(namespaceURI);
    }
    
    @Override
    public Iterator getPrefixes(final String namespaceURI) {
        return this.nsContext.getPrefixes(namespaceURI);
    }
}
