package com.sun.org.apache.xml.internal.security.keys.content;

import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import org.w3c.dom.Element;
import com.sun.org.apache.xml.internal.security.utils.SignatureElementProxy;

public class PGPData extends SignatureElementProxy implements KeyInfoContent
{
    public PGPData(final Element element, final String s) throws XMLSecurityException {
        super(element, s);
    }
    
    @Override
    public String getBaseLocalName() {
        return "PGPData";
    }
}
