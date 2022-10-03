package com.sun.org.apache.xalan.internal.xsltc.trax;

import com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl;
import jdk.xml.internal.JdkXmlUtils;
import org.xml.sax.SAXException;
import org.w3c.dom.Comment;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.ext.Locator2;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.DocumentBuilderFactory;
import org.xml.sax.Locator;
import java.util.Vector;
import java.util.Stack;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import com.sun.org.apache.xalan.internal.xsltc.runtime.Constants;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.ContentHandler;

public class SAX2DOM implements ContentHandler, LexicalHandler, Constants
{
    private Node _root;
    private Document _document;
    private Node _nextSibling;
    private Stack _nodeStk;
    private Vector _namespaceDecls;
    private Node _lastSibling;
    private Locator locator;
    private boolean needToSetDocumentInfo;
    private StringBuilder _textBuffer;
    private Node _nextSiblingCache;
    private DocumentBuilderFactory _factory;
    private boolean _internal;
    
    public SAX2DOM(final boolean overrideDefaultParser) throws ParserConfigurationException {
        this._root = null;
        this._document = null;
        this._nextSibling = null;
        this._nodeStk = new Stack();
        this._namespaceDecls = null;
        this._lastSibling = null;
        this.locator = null;
        this.needToSetDocumentInfo = true;
        this._textBuffer = new StringBuilder();
        this._nextSiblingCache = null;
        this._internal = true;
        this._document = this.createDocument(overrideDefaultParser);
        this._root = this._document;
    }
    
    public SAX2DOM(final Node root, final Node nextSibling, final boolean overrideDefaultParser) throws ParserConfigurationException {
        this._root = null;
        this._document = null;
        this._nextSibling = null;
        this._nodeStk = new Stack();
        this._namespaceDecls = null;
        this._lastSibling = null;
        this.locator = null;
        this.needToSetDocumentInfo = true;
        this._textBuffer = new StringBuilder();
        this._nextSiblingCache = null;
        this._internal = true;
        this._root = root;
        if (root instanceof Document) {
            this._document = (Document)root;
        }
        else if (root != null) {
            this._document = root.getOwnerDocument();
        }
        else {
            this._document = this.createDocument(overrideDefaultParser);
            this._root = this._document;
        }
        this._nextSibling = nextSibling;
    }
    
    public SAX2DOM(final Node root, final boolean overrideDefaultParser) throws ParserConfigurationException {
        this(root, null, overrideDefaultParser);
    }
    
    public Node getDOM() {
        return this._root;
    }
    
    @Override
    public void characters(final char[] ch, final int start, final int length) {
        if (length == 0) {
            return;
        }
        final Node last = this._nodeStk.peek();
        if (last != this._document) {
            this._nextSiblingCache = this._nextSibling;
            this._textBuffer.append(ch, start, length);
        }
    }
    
    private void appendTextNode() {
        if (this._textBuffer.length() > 0) {
            final Node last = this._nodeStk.peek();
            if (last == this._root && this._nextSiblingCache != null) {
                this._lastSibling = last.insertBefore(this._document.createTextNode(this._textBuffer.toString()), this._nextSiblingCache);
            }
            else {
                this._lastSibling = last.appendChild(this._document.createTextNode(this._textBuffer.toString()));
            }
            this._textBuffer.setLength(0);
        }
    }
    
    @Override
    public void startDocument() {
        this._nodeStk.push(this._root);
    }
    
    @Override
    public void endDocument() {
        this._nodeStk.pop();
    }
    
    private void setDocumentInfo() {
        if (this.locator == null) {
            return;
        }
        try {
            this._document.setXmlVersion(((Locator2)this.locator).getXMLVersion());
        }
        catch (final ClassCastException ex) {}
    }
    
    @Override
    public void startElement(final String namespace, final String localName, final String qName, final Attributes attrs) {
        this.appendTextNode();
        if (this.needToSetDocumentInfo) {
            this.setDocumentInfo();
            this.needToSetDocumentInfo = false;
        }
        final Element tmp = this._document.createElementNS(namespace, qName);
        if (this._namespaceDecls != null) {
            for (int nDecls = this._namespaceDecls.size(), i = 0; i < nDecls; ++i) {
                final String prefix = this._namespaceDecls.elementAt(i++);
                if (prefix == null || prefix.equals("")) {
                    tmp.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns", this._namespaceDecls.elementAt(i));
                }
                else {
                    tmp.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:" + prefix, this._namespaceDecls.elementAt(i));
                }
            }
            this._namespaceDecls.clear();
        }
        for (int nattrs = attrs.getLength(), i = 0; i < nattrs; ++i) {
            final String attQName = attrs.getQName(i);
            final String attURI = attrs.getURI(i);
            if (attrs.getLocalName(i).equals("")) {
                tmp.setAttribute(attQName, attrs.getValue(i));
                if (attrs.getType(i).equals("ID")) {
                    tmp.setIdAttribute(attQName, true);
                }
            }
            else {
                tmp.setAttributeNS(attURI, attQName, attrs.getValue(i));
                if (attrs.getType(i).equals("ID")) {
                    tmp.setIdAttributeNS(attURI, attrs.getLocalName(i), true);
                }
            }
        }
        final Node last = this._nodeStk.peek();
        if (last == this._root && this._nextSibling != null) {
            last.insertBefore(tmp, this._nextSibling);
        }
        else {
            last.appendChild(tmp);
        }
        this._nodeStk.push(tmp);
        this._lastSibling = null;
    }
    
    @Override
    public void endElement(final String namespace, final String localName, final String qName) {
        this.appendTextNode();
        this._nodeStk.pop();
        this._lastSibling = null;
    }
    
    @Override
    public void startPrefixMapping(final String prefix, final String uri) {
        if (this._namespaceDecls == null) {
            this._namespaceDecls = new Vector(2);
        }
        this._namespaceDecls.addElement(prefix);
        this._namespaceDecls.addElement(uri);
    }
    
    @Override
    public void endPrefixMapping(final String prefix) {
    }
    
    @Override
    public void ignorableWhitespace(final char[] ch, final int start, final int length) {
    }
    
    @Override
    public void processingInstruction(final String target, final String data) {
        this.appendTextNode();
        final Node last = this._nodeStk.peek();
        final ProcessingInstruction pi = this._document.createProcessingInstruction(target, data);
        if (pi != null) {
            if (last == this._root && this._nextSibling != null) {
                last.insertBefore(pi, this._nextSibling);
            }
            else {
                last.appendChild(pi);
            }
            this._lastSibling = pi;
        }
    }
    
    @Override
    public void setDocumentLocator(final Locator locator) {
        this.locator = locator;
    }
    
    @Override
    public void skippedEntity(final String name) {
    }
    
    @Override
    public void comment(final char[] ch, final int start, final int length) {
        this.appendTextNode();
        final Node last = this._nodeStk.peek();
        final Comment comment = this._document.createComment(new String(ch, start, length));
        if (comment != null) {
            if (last == this._root && this._nextSibling != null) {
                last.insertBefore(comment, this._nextSibling);
            }
            else {
                last.appendChild(comment);
            }
            this._lastSibling = comment;
        }
    }
    
    @Override
    public void startCDATA() {
    }
    
    @Override
    public void endCDATA() {
    }
    
    @Override
    public void startEntity(final String name) {
    }
    
    @Override
    public void endDTD() {
    }
    
    @Override
    public void endEntity(final String name) {
    }
    
    @Override
    public void startDTD(final String name, final String publicId, final String systemId) throws SAXException {
    }
    
    private Document createDocument(final boolean overrideDefaultParser) throws ParserConfigurationException {
        if (this._factory == null) {
            this._factory = JdkXmlUtils.getDOMFactory(overrideDefaultParser);
            this._internal = true;
            if (!(this._factory instanceof DocumentBuilderFactoryImpl)) {
                this._internal = false;
            }
        }
        Document doc;
        if (this._internal) {
            doc = this._factory.newDocumentBuilder().newDocument();
        }
        else {
            synchronized (SAX2DOM.class) {
                doc = this._factory.newDocumentBuilder().newDocument();
            }
        }
        return doc;
    }
}
