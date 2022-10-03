package org.apache.xml.security.signature;

import org.w3c.dom.Node;
import org.apache.xml.security.utils.IdResolver;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.w3c.dom.Element;
import org.w3c.dom.Document;
import org.apache.xml.security.utils.SignatureElementProxy;

public class ObjectContainer extends SignatureElementProxy
{
    public ObjectContainer(final Document document) {
        super(document);
    }
    
    public ObjectContainer(final Element element, final String s) throws XMLSecurityException {
        super(element, s);
    }
    
    public void setId(final String s) {
        if (super._state == 0 && s != null) {
            super._constructionElement.setAttributeNS(null, "Id", s);
            IdResolver.registerElementById(super._constructionElement, s);
        }
    }
    
    public String getId() {
        return super._constructionElement.getAttributeNS(null, "Id");
    }
    
    public void setMimeType(final String s) {
        if (super._state == 0 && s != null) {
            super._constructionElement.setAttributeNS(null, "MimeType", s);
        }
    }
    
    public String getMimeType() {
        return super._constructionElement.getAttributeNS(null, "MimeType");
    }
    
    public void setEncoding(final String s) {
        if (super._state == 0 && s != null) {
            super._constructionElement.setAttributeNS(null, "Encoding", s);
        }
    }
    
    public String getEncoding() {
        return super._constructionElement.getAttributeNS(null, "Encoding");
    }
    
    public Node appendChild(final Node node) {
        Node appendChild = null;
        if (super._state == 0) {
            appendChild = super._constructionElement.appendChild(node);
        }
        return appendChild;
    }
    
    public String getBaseLocalName() {
        return "Object";
    }
}
