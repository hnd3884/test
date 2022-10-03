package com.sun.org.apache.xml.internal.security.signature;

import org.w3c.dom.Node;
import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import org.w3c.dom.Element;
import org.w3c.dom.Document;
import com.sun.org.apache.xml.internal.security.utils.SignatureElementProxy;

public class ObjectContainer extends SignatureElementProxy
{
    public ObjectContainer(final Document document) {
        super(document);
    }
    
    public ObjectContainer(final Element element, final String s) throws XMLSecurityException {
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
    
    public void setMimeType(final String s) {
        if (s != null) {
            this.setLocalAttribute("MimeType", s);
        }
    }
    
    public String getMimeType() {
        return this.getLocalAttribute("MimeType");
    }
    
    public void setEncoding(final String s) {
        if (s != null) {
            this.setLocalAttribute("Encoding", s);
        }
    }
    
    public String getEncoding() {
        return this.getLocalAttribute("Encoding");
    }
    
    public Node appendChild(final Node node) {
        this.appendSelf(node);
        return node;
    }
    
    @Override
    public String getBaseLocalName() {
        return "Object";
    }
}
