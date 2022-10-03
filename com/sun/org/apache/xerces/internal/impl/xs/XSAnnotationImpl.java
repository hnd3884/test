package com.sun.org.apache.xerces.internal.impl.xs;

import org.w3c.dom.Element;
import com.sun.org.apache.xerces.internal.parsers.DOMParser;
import com.sun.org.apache.xerces.internal.dom.CoreDocumentImpl;
import org.w3c.dom.Document;
import com.sun.org.apache.xerces.internal.parsers.SAXParser;
import java.io.IOException;
import org.xml.sax.SAXException;
import java.io.Reader;
import org.xml.sax.InputSource;
import java.io.StringReader;
import com.sun.org.apache.xerces.internal.xs.XSNamespaceItem;
import org.xml.sax.ContentHandler;
import org.w3c.dom.Node;
import com.sun.org.apache.xerces.internal.xs.XSAnnotation;

public class XSAnnotationImpl implements XSAnnotation
{
    private String fData;
    private SchemaGrammar fGrammar;
    
    public XSAnnotationImpl(final String contents, final SchemaGrammar grammar) {
        this.fData = null;
        this.fGrammar = null;
        this.fData = contents;
        this.fGrammar = grammar;
    }
    
    @Override
    public boolean writeAnnotation(final Object target, final short targetType) {
        if (targetType == 1 || targetType == 3) {
            this.writeToDOM((Node)target, targetType);
            return true;
        }
        if (targetType == 2) {
            this.writeToSAX((ContentHandler)target);
            return true;
        }
        return false;
    }
    
    @Override
    public String getAnnotationString() {
        return this.fData;
    }
    
    @Override
    public short getType() {
        return 12;
    }
    
    @Override
    public String getName() {
        return null;
    }
    
    @Override
    public String getNamespace() {
        return null;
    }
    
    @Override
    public XSNamespaceItem getNamespaceItem() {
        return null;
    }
    
    private synchronized void writeToSAX(final ContentHandler handler) {
        final SAXParser parser = this.fGrammar.getSAXParser();
        final StringReader aReader = new StringReader(this.fData);
        final InputSource aSource = new InputSource(aReader);
        parser.setContentHandler(handler);
        try {
            parser.parse(aSource);
        }
        catch (final SAXException ex) {}
        catch (final IOException ex2) {}
        parser.setContentHandler(null);
    }
    
    private synchronized void writeToDOM(final Node target, final short type) {
        final Document futureOwner = (Document)((type == 1) ? target.getOwnerDocument() : target);
        final DOMParser parser = this.fGrammar.getDOMParser();
        final StringReader aReader = new StringReader(this.fData);
        final InputSource aSource = new InputSource(aReader);
        try {
            parser.parse(aSource);
        }
        catch (final SAXException ex) {}
        catch (final IOException ex2) {}
        final Document aDocument = parser.getDocument();
        parser.dropDocumentReferences();
        final Element annotation = aDocument.getDocumentElement();
        Node newElem = null;
        if (futureOwner instanceof CoreDocumentImpl) {
            newElem = futureOwner.adoptNode(annotation);
            if (newElem == null) {
                newElem = futureOwner.importNode(annotation, true);
            }
        }
        else {
            newElem = futureOwner.importNode(annotation, true);
        }
        target.insertBefore(newElem, target.getFirstChild());
    }
}
