package com.sun.org.apache.xml.internal.security.utils;

import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import org.w3c.dom.Element;
import org.w3c.dom.Document;

public abstract class Signature11ElementProxy extends ElementProxy
{
    protected Signature11ElementProxy() {
    }
    
    public Signature11ElementProxy(final Document document) {
        if (document == null) {
            throw new RuntimeException("Document is null");
        }
        this.setDocument(document);
        this.setElement(XMLUtils.createElementInSignature11Space(document, this.getBaseLocalName()));
        final String defaultPrefix = ElementProxy.getDefaultPrefix(this.getBaseNamespace());
        if (defaultPrefix == null || defaultPrefix.length() == 0) {
            this.getElement().setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns", this.getBaseNamespace());
        }
        else {
            this.getElement().setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:" + defaultPrefix, this.getBaseNamespace());
        }
    }
    
    public Signature11ElementProxy(final Element element, final String s) throws XMLSecurityException {
        super(element, s);
    }
    
    @Override
    public String getBaseNamespace() {
        return "http://www.w3.org/2009/xmldsig11#";
    }
}
