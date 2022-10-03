package com.sun.org.apache.xml.internal.dtm.ref;

import com.sun.org.apache.xml.internal.utils.XMLString;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.ext.LexicalHandler;
import com.sun.org.apache.xml.internal.utils.NodeConsumer;
import org.xml.sax.SAXException;
import com.sun.org.apache.xml.internal.dtm.DTM;
import org.xml.sax.ContentHandler;

public class DTMTreeWalker
{
    private ContentHandler m_contentHandler;
    protected DTM m_dtm;
    boolean nextIsRaw;
    
    public void setDTM(final DTM dtm) {
        this.m_dtm = dtm;
    }
    
    public ContentHandler getcontentHandler() {
        return this.m_contentHandler;
    }
    
    public void setcontentHandler(final ContentHandler ch) {
        this.m_contentHandler = ch;
    }
    
    public DTMTreeWalker() {
        this.m_contentHandler = null;
        this.nextIsRaw = false;
    }
    
    public DTMTreeWalker(final ContentHandler contentHandler, final DTM dtm) {
        this.m_contentHandler = null;
        this.nextIsRaw = false;
        this.m_contentHandler = contentHandler;
        this.m_dtm = dtm;
    }
    
    public void traverse(int pos) throws SAXException {
        final int top = pos;
        while (-1 != pos) {
            this.startNode(pos);
            int nextNode = this.m_dtm.getFirstChild(pos);
            while (-1 == nextNode) {
                this.endNode(pos);
                if (top == pos) {
                    break;
                }
                nextNode = this.m_dtm.getNextSibling(pos);
                if (-1 != nextNode) {
                    continue;
                }
                pos = this.m_dtm.getParent(pos);
                if (-1 == pos || top == pos) {
                    if (-1 != pos) {
                        this.endNode(pos);
                    }
                    nextNode = -1;
                    break;
                }
            }
            pos = nextNode;
        }
    }
    
    public void traverse(int pos, final int top) throws SAXException {
        while (-1 != pos) {
            this.startNode(pos);
            int nextNode = this.m_dtm.getFirstChild(pos);
            while (-1 == nextNode) {
                this.endNode(pos);
                if (-1 != top && top == pos) {
                    break;
                }
                nextNode = this.m_dtm.getNextSibling(pos);
                if (-1 != nextNode) {
                    continue;
                }
                pos = this.m_dtm.getParent(pos);
                if (-1 == pos || (-1 != top && top == pos)) {
                    nextNode = -1;
                    break;
                }
            }
            pos = nextNode;
        }
    }
    
    private final void dispatachChars(final int node) throws SAXException {
        this.m_dtm.dispatchCharactersEvents(node, this.m_contentHandler, false);
    }
    
    protected void startNode(final int node) throws SAXException {
        if (this.m_contentHandler instanceof NodeConsumer) {}
        switch (this.m_dtm.getNodeType(node)) {
            case 8: {
                final XMLString data = this.m_dtm.getStringValue(node);
                if (this.m_contentHandler instanceof LexicalHandler) {
                    final LexicalHandler lh = (LexicalHandler)this.m_contentHandler;
                    data.dispatchAsComment(lh);
                }
            }
            case 9: {
                this.m_contentHandler.startDocument();
                break;
            }
            case 1: {
                final DTM dtm = this.m_dtm;
                for (int nsn = dtm.getFirstNamespaceNode(node, true); -1 != nsn; nsn = dtm.getNextNamespaceNode(node, nsn, true)) {
                    final String prefix = dtm.getNodeNameX(nsn);
                    this.m_contentHandler.startPrefixMapping(prefix, dtm.getNodeValue(nsn));
                }
                String ns = dtm.getNamespaceURI(node);
                if (null == ns) {
                    ns = "";
                }
                final AttributesImpl attrs = new AttributesImpl();
                for (int i = dtm.getFirstAttribute(node); i != -1; i = dtm.getNextAttribute(i)) {
                    attrs.addAttribute(dtm.getNamespaceURI(i), dtm.getLocalName(i), dtm.getNodeName(i), "CDATA", dtm.getNodeValue(i));
                }
                this.m_contentHandler.startElement(ns, this.m_dtm.getLocalName(node), this.m_dtm.getNodeName(node), attrs);
                break;
            }
            case 7: {
                final String name = this.m_dtm.getNodeName(node);
                if (name.equals("xslt-next-is-raw")) {
                    this.nextIsRaw = true;
                }
                else {
                    this.m_contentHandler.processingInstruction(name, this.m_dtm.getNodeValue(node));
                }
                break;
            }
            case 4: {
                final boolean isLexH = this.m_contentHandler instanceof LexicalHandler;
                final LexicalHandler lh2 = isLexH ? ((LexicalHandler)this.m_contentHandler) : null;
                if (isLexH) {
                    lh2.startCDATA();
                }
                this.dispatachChars(node);
                if (isLexH) {
                    lh2.endCDATA();
                }
                break;
            }
            case 3: {
                if (this.nextIsRaw) {
                    this.nextIsRaw = false;
                    this.m_contentHandler.processingInstruction("javax.xml.transform.disable-output-escaping", "");
                    this.dispatachChars(node);
                    this.m_contentHandler.processingInstruction("javax.xml.transform.enable-output-escaping", "");
                    break;
                }
                this.dispatachChars(node);
                break;
            }
            case 5: {
                if (this.m_contentHandler instanceof LexicalHandler) {
                    ((LexicalHandler)this.m_contentHandler).startEntity(this.m_dtm.getNodeName(node));
                    break;
                }
                break;
            }
        }
    }
    
    protected void endNode(final int node) throws SAXException {
        switch (this.m_dtm.getNodeType(node)) {
            case 9: {
                this.m_contentHandler.endDocument();
                break;
            }
            case 1: {
                String ns = this.m_dtm.getNamespaceURI(node);
                if (null == ns) {
                    ns = "";
                }
                this.m_contentHandler.endElement(ns, this.m_dtm.getLocalName(node), this.m_dtm.getNodeName(node));
                for (int nsn = this.m_dtm.getFirstNamespaceNode(node, true); -1 != nsn; nsn = this.m_dtm.getNextNamespaceNode(node, nsn, true)) {
                    final String prefix = this.m_dtm.getNodeNameX(nsn);
                    this.m_contentHandler.endPrefixMapping(prefix);
                }
            }
            case 5: {
                if (this.m_contentHandler instanceof LexicalHandler) {
                    final LexicalHandler lh = (LexicalHandler)this.m_contentHandler;
                    lh.endEntity(this.m_dtm.getNodeName(node));
                    break;
                }
                break;
            }
        }
    }
}
