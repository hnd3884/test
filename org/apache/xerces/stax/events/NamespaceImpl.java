package org.apache.xerces.stax.events;

import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.events.Namespace;

public final class NamespaceImpl extends AttributeImpl implements Namespace
{
    private final String fPrefix;
    private final String fNamespaceURI;
    
    public NamespaceImpl(final String s, final String fNamespaceURI, final Location location) {
        super(13, makeAttributeQName(s), fNamespaceURI, null, true, location);
        this.fPrefix = ((s == null) ? "" : s);
        this.fNamespaceURI = fNamespaceURI;
    }
    
    private static QName makeAttributeQName(final String s) {
        if (s == null || s.equals("")) {
            return new QName("http://www.w3.org/2000/xmlns/", "xmlns", "");
        }
        return new QName("http://www.w3.org/2000/xmlns/", s, "xmlns");
    }
    
    public String getPrefix() {
        return this.fPrefix;
    }
    
    public String getNamespaceURI() {
        return this.fNamespaceURI;
    }
    
    public boolean isDefaultNamespaceDeclaration() {
        return this.fPrefix.length() == 0;
    }
}
