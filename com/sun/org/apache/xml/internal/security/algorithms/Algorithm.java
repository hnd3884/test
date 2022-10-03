package com.sun.org.apache.xml.internal.security.algorithms;

import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import org.w3c.dom.Element;
import org.w3c.dom.Document;
import com.sun.org.apache.xml.internal.security.utils.SignatureElementProxy;

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
        return this.getLocalAttribute("Algorithm");
    }
    
    protected void setAlgorithmURI(final String s) {
        if (s != null) {
            this.setLocalAttribute("Algorithm", s);
        }
    }
}
