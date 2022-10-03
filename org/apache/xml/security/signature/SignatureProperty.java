package org.apache.xml.security.signature;

import org.w3c.dom.Node;
import org.apache.xml.security.utils.IdResolver;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.w3c.dom.Element;
import org.w3c.dom.Document;
import org.apache.xml.security.utils.SignatureElementProxy;

public class SignatureProperty extends SignatureElementProxy
{
    public SignatureProperty(final Document document, final String s) {
        this(document, s, null);
    }
    
    public SignatureProperty(final Document document, final String target, final String id) {
        super(document);
        this.setTarget(target);
        this.setId(id);
    }
    
    public SignatureProperty(final Element element, final String s) throws XMLSecurityException {
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
    
    public void setTarget(final String s) {
        if (super._state == 0 && s != null) {
            super._constructionElement.setAttributeNS(null, "Target", s);
        }
    }
    
    public String getTarget() {
        return super._constructionElement.getAttributeNS(null, "Target");
    }
    
    public Node appendChild(final Node node) {
        return super._constructionElement.appendChild(node);
    }
    
    public String getBaseLocalName() {
        return "SignatureProperty";
    }
}
