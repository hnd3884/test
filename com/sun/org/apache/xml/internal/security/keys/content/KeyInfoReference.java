package com.sun.org.apache.xml.internal.security.keys.content;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import org.w3c.dom.Element;
import com.sun.org.apache.xml.internal.security.utils.Signature11ElementProxy;

public class KeyInfoReference extends Signature11ElementProxy implements KeyInfoContent
{
    public KeyInfoReference(final Element element, final String s) throws XMLSecurityException {
        super(element, s);
    }
    
    public KeyInfoReference(final Document document, final String s) {
        super(document);
        this.setLocalAttribute("URI", s);
    }
    
    public Attr getURIAttr() {
        return this.getElement().getAttributeNodeNS(null, "URI");
    }
    
    public String getURI() {
        return this.getURIAttr().getNodeValue();
    }
    
    public void setId(final String s) {
        this.setLocalIdAttribute("Id", s);
    }
    
    public String getId() {
        return this.getLocalAttribute("Id");
    }
    
    @Override
    public String getBaseLocalName() {
        return "KeyInfoReference";
    }
}
