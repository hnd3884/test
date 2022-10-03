package com.sun.org.apache.xml.internal.security.signature;

import org.w3c.dom.Node;
import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import org.w3c.dom.Element;
import org.w3c.dom.Document;
import com.sun.org.apache.xml.internal.security.utils.SignatureElementProxy;

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
        if (s != null) {
            this.setLocalIdAttribute("Id", s);
        }
    }
    
    public String getId() {
        return this.getLocalAttribute("Id");
    }
    
    public void setTarget(final String s) {
        if (s != null) {
            this.setLocalAttribute("Target", s);
        }
    }
    
    public String getTarget() {
        return this.getLocalAttribute("Target");
    }
    
    public Node appendChild(final Node node) {
        this.appendSelf(node);
        return node;
    }
    
    @Override
    public String getBaseLocalName() {
        return "SignatureProperty";
    }
}
