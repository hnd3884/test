package org.apache.html.dom;

import org.xml.sax.Locator;
import org.w3c.dom.html.HTMLDocument;
import org.apache.xerces.dom.CoreDocumentImpl;
import org.apache.xerces.dom.ProcessingInstructionImpl;
import org.w3c.dom.Node;
import org.xml.sax.AttributeList;
import org.xml.sax.SAXException;
import java.util.Vector;
import org.apache.xerces.dom.ElementImpl;
import org.xml.sax.DocumentHandler;

public class HTMLBuilder implements DocumentHandler
{
    protected HTMLDocumentImpl _document;
    protected ElementImpl _current;
    private boolean _ignoreWhitespace;
    private boolean _done;
    protected Vector _preRootNodes;
    
    public HTMLBuilder() {
        this._ignoreWhitespace = true;
        this._done = true;
    }
    
    public void startDocument() throws SAXException {
        if (!this._done) {
            throw new SAXException("HTM001 State error: startDocument fired twice on one builder.");
        }
        this._document = null;
        this._done = false;
    }
    
    public void endDocument() throws SAXException {
        if (this._document == null) {
            throw new SAXException("HTM002 State error: document never started or missing document element.");
        }
        if (this._current != null) {
            throw new SAXException("HTM003 State error: document ended before end of document element.");
        }
        this._current = null;
        this._done = true;
    }
    
    public synchronized void startElement(final String s, final AttributeList list) throws SAXException {
        if (s == null) {
            throw new SAXException("HTM004 Argument 'tagName' is null.");
        }
        ElementImpl elementImpl;
        if (this._document == null) {
            this._document = new HTMLDocumentImpl();
            elementImpl = (ElementImpl)this._document.getDocumentElement();
            this._current = elementImpl;
            if (this._current == null) {
                throw new SAXException("HTM005 State error: Document.getDocumentElement returns null.");
            }
            if (this._preRootNodes != null) {
                int size = this._preRootNodes.size();
                while (size-- > 0) {
                    this._document.insertBefore((Node)this._preRootNodes.elementAt(size), elementImpl);
                }
                this._preRootNodes = null;
            }
        }
        else {
            if (this._current == null) {
                throw new SAXException("HTM006 State error: startElement called after end of document element.");
            }
            elementImpl = (ElementImpl)this._document.createElement(s);
            this._current.appendChild(elementImpl);
            this._current = elementImpl;
        }
        if (list != null) {
            for (int i = 0; i < list.getLength(); ++i) {
                elementImpl.setAttribute(list.getName(i), list.getValue(i));
            }
        }
    }
    
    public void endElement(final String s) throws SAXException {
        if (this._current == null) {
            throw new SAXException("HTM007 State error: endElement called with no current node.");
        }
        if (!this._current.getNodeName().equalsIgnoreCase(s)) {
            throw new SAXException("HTM008 State error: mismatch in closing tag name " + s + "\n" + s);
        }
        if (this._current.getParentNode() == this._current.getOwnerDocument()) {
            this._current = null;
        }
        else {
            this._current = (ElementImpl)this._current.getParentNode();
        }
    }
    
    public void characters(final String s) throws SAXException {
        if (this._current == null) {
            throw new SAXException("HTM009 State error: character data found outside of root element.");
        }
        this._current.appendChild(this._document.createTextNode(s));
    }
    
    public void characters(final char[] array, final int n, final int n2) throws SAXException {
        if (this._current == null) {
            throw new SAXException("HTM010 State error: character data found outside of root element.");
        }
        this._current.appendChild(this._document.createTextNode(new String(array, n, n2)));
    }
    
    public void ignorableWhitespace(final char[] array, final int n, final int n2) throws SAXException {
        if (!this._ignoreWhitespace) {
            this._current.appendChild(this._document.createTextNode(new String(array, n, n2)));
        }
    }
    
    public void processingInstruction(final String s, final String s2) throws SAXException {
        if (this._current == null && this._document == null) {
            if (this._preRootNodes == null) {
                this._preRootNodes = new Vector();
            }
            this._preRootNodes.addElement(new ProcessingInstructionImpl(null, s, s2));
        }
        else if (this._current == null && this._document != null) {
            this._document.appendChild(this._document.createProcessingInstruction(s, s2));
        }
        else {
            this._current.appendChild(this._document.createProcessingInstruction(s, s2));
        }
    }
    
    public HTMLDocument getHTMLDocument() {
        return this._document;
    }
    
    public void setDocumentLocator(final Locator locator) {
    }
}
