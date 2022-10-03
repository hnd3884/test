package org.apache.xerces.impl.xs;

import org.w3c.dom.Element;
import org.apache.xerces.parsers.DOMParser;
import org.apache.xerces.dom.CoreDocumentImpl;
import org.w3c.dom.Document;
import org.apache.xerces.parsers.SAXParser;
import java.io.IOException;
import org.xml.sax.SAXException;
import java.io.Reader;
import org.xml.sax.InputSource;
import java.io.StringReader;
import org.apache.xerces.xs.XSNamespaceItem;
import org.xml.sax.ContentHandler;
import org.w3c.dom.Node;
import org.apache.xerces.xs.XSAnnotation;

public class XSAnnotationImpl implements XSAnnotation
{
    private String fData;
    private SchemaGrammar fGrammar;
    
    public XSAnnotationImpl(final String fData, final SchemaGrammar fGrammar) {
        this.fData = null;
        this.fGrammar = null;
        this.fData = fData;
        this.fGrammar = fGrammar;
    }
    
    public boolean writeAnnotation(final Object o, final short n) {
        if (n == 1 || n == 3) {
            this.writeToDOM((Node)o, n);
            return true;
        }
        if (n == 2) {
            this.writeToSAX((ContentHandler)o);
            return true;
        }
        return false;
    }
    
    public String getAnnotationString() {
        return this.fData;
    }
    
    public short getType() {
        return 12;
    }
    
    public String getName() {
        return null;
    }
    
    public String getNamespace() {
        return null;
    }
    
    public XSNamespaceItem getNamespaceItem() {
        return null;
    }
    
    private synchronized void writeToSAX(final ContentHandler contentHandler) {
        final SAXParser saxParser = this.fGrammar.getSAXParser();
        final InputSource inputSource = new InputSource(new StringReader(this.fData));
        saxParser.setContentHandler(contentHandler);
        try {
            saxParser.parse(inputSource);
        }
        catch (final SAXException ex) {}
        catch (final IOException ex2) {}
        saxParser.setContentHandler(null);
    }
    
    private synchronized void writeToDOM(final Node node, final short n) {
        final Document document = (Document)((n == 1) ? node.getOwnerDocument() : node);
        final DOMParser domParser = this.fGrammar.getDOMParser();
        final InputSource inputSource = new InputSource(new StringReader(this.fData));
        try {
            domParser.parse(inputSource);
        }
        catch (final SAXException ex) {}
        catch (final IOException ex2) {}
        final Document document2 = domParser.getDocument();
        domParser.dropDocumentReferences();
        final Element documentElement = document2.getDocumentElement();
        Node node2;
        if (document instanceof CoreDocumentImpl) {
            node2 = document.adoptNode(documentElement);
            if (node2 == null) {
                node2 = document.importNode(documentElement, true);
            }
        }
        else {
            node2 = document.importNode(documentElement, true);
        }
        node.insertBefore(node2, node.getFirstChild());
    }
}
