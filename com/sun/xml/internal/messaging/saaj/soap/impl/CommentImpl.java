package com.sun.xml.internal.messaging.saaj.soap.impl;

import org.w3c.dom.DOMException;
import org.w3c.dom.Node;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPElement;
import com.sun.org.apache.xerces.internal.dom.CoreDocumentImpl;
import com.sun.xml.internal.messaging.saaj.soap.SOAPDocumentImpl;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import org.w3c.dom.Comment;
import javax.xml.soap.Text;

public class CommentImpl extends com.sun.org.apache.xerces.internal.dom.CommentImpl implements Text, Comment
{
    protected static final Logger log;
    protected static ResourceBundle rb;
    
    public CommentImpl(final SOAPDocumentImpl ownerDoc, final String text) {
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
    public void setParentElement(final SOAPElement element) throws SOAPException {
        if (element == null) {
            CommentImpl.log.severe("SAAJ0112.impl.no.null.to.parent.elem");
            throw new SOAPException("Cannot pass NULL to setParentElement");
        }
        ((ElementImpl)element).addNode(this);
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
        return true;
    }
    
    @Override
    public org.w3c.dom.Text splitText(final int offset) throws DOMException {
        CommentImpl.log.severe("SAAJ0113.impl.cannot.split.text.from.comment");
        throw new UnsupportedOperationException("Cannot split text from a Comment Node.");
    }
    
    @Override
    public org.w3c.dom.Text replaceWholeText(final String content) throws DOMException {
        CommentImpl.log.severe("SAAJ0114.impl.cannot.replace.wholetext.from.comment");
        throw new UnsupportedOperationException("Cannot replace Whole Text from a Comment Node.");
    }
    
    @Override
    public String getWholeText() {
        throw new UnsupportedOperationException("Not Supported");
    }
    
    @Override
    public boolean isElementContentWhitespace() {
        throw new UnsupportedOperationException("Not Supported");
    }
    
    static {
        log = Logger.getLogger("com.sun.xml.internal.messaging.saaj.soap.impl", "com.sun.xml.internal.messaging.saaj.soap.impl.LocalStrings");
        CommentImpl.rb = CommentImpl.log.getResourceBundle();
    }
}
