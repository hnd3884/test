package com.sun.xml.internal.txw2.output;

import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import com.sun.xml.internal.txw2.TxwException;
import org.xml.sax.Attributes;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Element;
import java.util.ArrayList;
import org.w3c.dom.Document;
import java.util.Stack;
import org.w3c.dom.Node;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.ContentHandler;

class Dom2SaxAdapter implements ContentHandler, LexicalHandler
{
    private final Node _node;
    private final Stack _nodeStk;
    private boolean inCDATA;
    private final Document _document;
    private ArrayList unprocessedNamespaces;
    
    public final Element getCurrentElement() {
        return this._nodeStk.peek();
    }
    
    public Dom2SaxAdapter(final Node node) {
        this._nodeStk = new Stack();
        this.unprocessedNamespaces = new ArrayList();
        this._node = node;
        this._nodeStk.push(this._node);
        if (node instanceof Document) {
            this._document = (Document)node;
        }
        else {
            this._document = node.getOwnerDocument();
        }
    }
    
    public Dom2SaxAdapter() throws ParserConfigurationException {
        this._nodeStk = new Stack();
        this.unprocessedNamespaces = new ArrayList();
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setValidating(false);
        this._document = factory.newDocumentBuilder().newDocument();
        this._node = this._document;
        this._nodeStk.push(this._document);
    }
    
    public Node getDOM() {
        return this._node;
    }
    
    @Override
    public void startDocument() {
    }
    
    @Override
    public void endDocument() {
    }
    
    @Override
    public void startElement(final String namespace, final String localName, final String qName, final Attributes attrs) {
        final Element element = this._document.createElementNS(namespace, qName);
        if (element == null) {
            throw new TxwException("Your DOM provider doesn't support the createElementNS method properly");
        }
        for (int i = 0; i < this.unprocessedNamespaces.size(); i += 2) {
            final String prefix = this.unprocessedNamespaces.get(i + 0);
            final String uri = this.unprocessedNamespaces.get(i + 1);
            String qname;
            if ("".equals(prefix) || prefix == null) {
                qname = "xmlns";
            }
            else {
                qname = "xmlns:" + prefix;
            }
            if (element.hasAttributeNS("http://www.w3.org/2000/xmlns/", qname)) {
                element.removeAttributeNS("http://www.w3.org/2000/xmlns/", qname);
            }
            element.setAttributeNS("http://www.w3.org/2000/xmlns/", qname, uri);
        }
        this.unprocessedNamespaces.clear();
        for (int length = attrs.getLength(), j = 0; j < length; ++j) {
            final String namespaceuri = attrs.getURI(j);
            final String value = attrs.getValue(j);
            final String qname2 = attrs.getQName(j);
            element.setAttributeNS(namespaceuri, qname2, value);
        }
        this.getParent().appendChild(element);
        this._nodeStk.push(element);
    }
    
    private final Node getParent() {
        return this._nodeStk.peek();
    }
    
    @Override
    public void endElement(final String namespace, final String localName, final String qName) {
        this._nodeStk.pop();
    }
    
    @Override
    public void characters(final char[] ch, final int start, final int length) {
        Node text;
        if (this.inCDATA) {
            text = this._document.createCDATASection(new String(ch, start, length));
        }
        else {
            text = this._document.createTextNode(new String(ch, start, length));
        }
        this.getParent().appendChild(text);
    }
    
    @Override
    public void comment(final char[] ch, final int start, final int length) throws SAXException {
        this.getParent().appendChild(this._document.createComment(new String(ch, start, length)));
    }
    
    @Override
    public void ignorableWhitespace(final char[] ch, final int start, final int length) {
    }
    
    @Override
    public void processingInstruction(final String target, final String data) throws SAXException {
        final Node node = this._document.createProcessingInstruction(target, data);
        this.getParent().appendChild(node);
    }
    
    @Override
    public void setDocumentLocator(final Locator locator) {
    }
    
    @Override
    public void skippedEntity(final String name) {
    }
    
    @Override
    public void startPrefixMapping(final String prefix, final String uri) {
        this.unprocessedNamespaces.add(prefix);
        this.unprocessedNamespaces.add(uri);
    }
    
    @Override
    public void endPrefixMapping(final String prefix) {
    }
    
    @Override
    public void startDTD(final String name, final String publicId, final String systemId) throws SAXException {
    }
    
    @Override
    public void endDTD() throws SAXException {
    }
    
    @Override
    public void startEntity(final String name) throws SAXException {
    }
    
    @Override
    public void endEntity(final String name) throws SAXException {
    }
    
    @Override
    public void startCDATA() throws SAXException {
        this.inCDATA = true;
    }
    
    @Override
    public void endCDATA() throws SAXException {
        this.inCDATA = false;
    }
}
