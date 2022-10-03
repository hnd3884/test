package com.sun.xml.internal.messaging.saaj.soap.impl;

import org.w3c.dom.Node;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPElement;
import com.sun.org.apache.xerces.internal.dom.CoreDocumentImpl;
import com.sun.xml.internal.messaging.saaj.soap.SOAPDocumentImpl;
import java.util.logging.Logger;
import javax.xml.soap.Text;

public class TextImpl extends com.sun.org.apache.xerces.internal.dom.TextImpl implements Text, org.w3c.dom.Text
{
    protected static final Logger log;
    
    public TextImpl(final SOAPDocumentImpl ownerDoc, final String text) {
        super(ownerDoc, text);
    }
    
    @Override
    public String getValue() {
        final String nodeValue = this.getNodeValue();
        return nodeValue.equals("") ? null : nodeValue;
    }
    
    @Override
    public void setValue(final String text) {
        this.setNodeValue(text);
    }
    
    @Override
    public void setParentElement(final SOAPElement parent) throws SOAPException {
        if (parent == null) {
            TextImpl.log.severe("SAAJ0126.impl.cannot.locate.ns");
            throw new SOAPException("Cannot pass NULL to setParentElement");
        }
        ((ElementImpl)parent).addNode(this);
    }
    
    @Override
    public SOAPElement getParentElement() {
        return (SOAPElement)this.getParentNode();
    }
    
    @Override
    public void detachNode() {
        final Node parent = this.getParentNode();
        if (parent != null) {
            parent.removeChild(this);
        }
    }
    
    @Override
    public void recycleNode() {
        this.detachNode();
    }
    
    @Override
    public boolean isComment() {
        final String txt = this.getNodeValue();
        return txt != null && txt.startsWith("<!--") && txt.endsWith("-->");
    }
    
    static {
        log = Logger.getLogger("com.sun.xml.internal.messaging.saaj.soap.impl", "com.sun.xml.internal.messaging.saaj.soap.impl.LocalStrings");
    }
}
