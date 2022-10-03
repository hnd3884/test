package org.apache.xml.security.utils;

import org.apache.xml.security.exceptions.XMLSecurityException;
import org.w3c.dom.Element;
import org.w3c.dom.Document;

public abstract class SignatureElementProxy extends ElementProxy
{
    protected SignatureElementProxy() {
    }
    
    public SignatureElementProxy(final Document doc) {
        if (doc == null) {
            throw new RuntimeException("Document is null");
        }
        super._doc = doc;
        super._state = 0;
        super._constructionElement = XMLUtils.createElementInSignatureSpace(super._doc, this.getBaseLocalName());
    }
    
    public SignatureElementProxy(final Element element, final String s) throws XMLSecurityException {
        super(element, s);
    }
    
    public String getBaseNamespace() {
        return "http://www.w3.org/2000/09/xmldsig#";
    }
}
