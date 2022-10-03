package org.apache.xml.security.algorithms;

import org.apache.xml.security.exceptions.XMLSecurityException;
import org.w3c.dom.Element;
import org.w3c.dom.Document;
import org.apache.xml.security.utils.SignatureElementProxy;

public abstract class Algorithm extends SignatureElementProxy
{
    public Algorithm(final Document document, final String algorithmURI) {
        super(document);
        this.setAlgorithmURI(algorithmURI);
    }
    
    public Algorithm(final Element element, final String s) throws XMLSecurityException {
        super(element, s);
    }
    
    public String getAlgorithmURI() {
        return super._constructionElement.getAttributeNS(null, "Algorithm");
    }
    
    protected void setAlgorithmURI(final String s) {
        if (super._state == 0 && s != null) {
            super._constructionElement.setAttributeNS(null, "Algorithm", s);
        }
    }
}
