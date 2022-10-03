package org.apache.xml.security.keys.content;

import org.apache.xml.security.exceptions.XMLSecurityException;
import org.w3c.dom.Element;
import org.apache.xml.security.utils.SignatureElementProxy;

public class PGPData extends SignatureElementProxy implements KeyInfoContent
{
    public PGPData(final Element element, final String s) throws XMLSecurityException {
        super(element, s);
    }
    
    public String getBaseLocalName() {
        return "PGPData";
    }
}
