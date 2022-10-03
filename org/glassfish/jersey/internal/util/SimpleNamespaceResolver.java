package org.glassfish.jersey.internal.util;

import java.util.Iterator;
import javax.xml.namespace.NamespaceContext;

public class SimpleNamespaceResolver implements NamespaceContext
{
    private final String prefix;
    private final String nsURI;
    
    public SimpleNamespaceResolver(final String prefix, final String nsURI) {
        this.prefix = prefix;
        this.nsURI = nsURI;
    }
    
    @Override
    public String getNamespaceURI(final String prefix) {
        if (prefix.equals(this.prefix)) {
            return this.nsURI;
        }
        return "";
    }
    
    @Override
    public String getPrefix(final String namespaceURI) {
        if (namespaceURI.equals(this.nsURI)) {
            return this.prefix;
        }
        return null;
    }
    
    @Override
    public Iterator getPrefixes(final String namespaceURI) {
        return null;
    }
}
