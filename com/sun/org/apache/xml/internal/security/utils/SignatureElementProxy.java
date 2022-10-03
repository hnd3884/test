package com.sun.org.apache.xml.internal.security.utils;

import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import org.w3c.dom.Element;
import org.w3c.dom.Document;

public abstract class SignatureElementProxy extends ElementProxy
{
    protected SignatureElementProxy() {
    }
    
    public SignatureElementProxy(final Document document) {
        if (document == null) {
            throw new RuntimeException("Document is null");
        }
        this.setDocument(document);
        this.setElement(XMLUtils.createElementInSignatureSpace(document, this.getBaseLocalName()));
    }
    
    public SignatureElementProxy(final Element element, final String s) throws XMLSecurityException {
        super(element, s);
    }
    
    @Override
    public String getBaseNamespace() {
        return "http://www.w3.org/2000/09/xmldsig#";
    }
}
