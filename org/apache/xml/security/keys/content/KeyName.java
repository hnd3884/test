package org.apache.xml.security.keys.content;

import org.w3c.dom.Document;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.w3c.dom.Element;
import org.apache.xml.security.utils.SignatureElementProxy;

public class KeyName extends SignatureElementProxy implements KeyInfoContent
{
    public KeyName(final Element element, final String s) throws XMLSecurityException {
        super(element, s);
    }
    
    public KeyName(final Document document, final String s) {
        super(document);
        this.addText(s);
    }
    
    public String getKeyName() {
        return this.getTextFromTextChild();
    }
    
    public String getBaseLocalName() {
        return "KeyName";
    }
}
