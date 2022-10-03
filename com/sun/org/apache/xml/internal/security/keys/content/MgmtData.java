package com.sun.org.apache.xml.internal.security.keys.content;

import org.w3c.dom.Document;
import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import org.w3c.dom.Element;
import com.sun.org.apache.xml.internal.security.utils.SignatureElementProxy;

public class MgmtData extends SignatureElementProxy implements KeyInfoContent
{
    public MgmtData(final Element element, final String s) throws XMLSecurityException {
        super(element, s);
    }
    
    public MgmtData(final Document document, final String s) {
        super(document);
        this.addText(s);
    }
    
    public String getMgmtData() {
        return this.getTextFromTextChild();
    }
    
    @Override
    public String getBaseLocalName() {
        return "MgmtData";
    }
}
