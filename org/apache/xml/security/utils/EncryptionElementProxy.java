package org.apache.xml.security.utils;

import org.apache.xml.security.exceptions.XMLSecurityException;
import org.w3c.dom.Element;
import org.w3c.dom.Document;

public abstract class EncryptionElementProxy extends ElementProxy
{
    public EncryptionElementProxy(final Document document) {
        super(document);
    }
    
    public EncryptionElementProxy(final Element element, final String s) throws XMLSecurityException {
        super(element, s);
    }
    
    public final String getBaseNamespace() {
        return "http://www.w3.org/2001/04/xmlenc#";
    }
}
