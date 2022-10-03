package com.sun.xml.internal.ws.wsdl.writer;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.Locator;
import com.sun.xml.internal.txw2.TypedXmlWriter;
import java.util.Stack;
import org.xml.sax.ContentHandler;

public class TXWContentHandler implements ContentHandler
{
    Stack<TypedXmlWriter> stack;
    
    public TXWContentHandler(final TypedXmlWriter txw) {
        (this.stack = new Stack<TypedXmlWriter>()).push(txw);
    }
    
    @Override
    public void setDocumentLocator(final Locator locator) {
    }
    
    @Override
    public void startDocument() throws SAXException {
    }
    
    @Override
    public void endDocument() throws SAXException {
    }
    
    @Override
    public void startPrefixMapping(final String prefix, final String uri) throws SAXException {
    }
    
    @Override
    public void endPrefixMapping(final String prefix) throws SAXException {
    }
    
    @Override
    public void startElement(final String uri, final String localName, final String qName, final Attributes atts) throws SAXException {
        final TypedXmlWriter txw = this.stack.peek()._element(uri, localName, TypedXmlWriter.class);
        this.stack.push(txw);
        if (atts != null) {
            for (int i = 0; i < atts.getLength(); ++i) {
                final String auri = atts.getURI(i);
                if ("http://www.w3.org/2000/xmlns/".equals(auri)) {
                    if ("xmlns".equals(atts.getLocalName(i))) {
                        txw._namespace(atts.getValue(i), "");
                    }
                    else {
                        txw._namespace(atts.getValue(i), atts.getLocalName(i));
                    }
                }
                else if (!"schemaLocation".equals(atts.getLocalName(i)) || !"".equals(atts.getValue(i))) {
                    txw._attribute(auri, atts.getLocalName(i), atts.getValue(i));
                }
            }
        }
    }
    
    @Override
    public void endElement(final String uri, final String localName, final String qName) throws SAXException {
        this.stack.pop();
    }
    
    @Override
    public void characters(final char[] ch, final int start, final int length) throws SAXException {
    }
    
    @Override
    public void ignorableWhitespace(final char[] ch, final int start, final int length) throws SAXException {
    }
    
    @Override
    public void processingInstruction(final String target, final String data) throws SAXException {
    }
    
    @Override
    public void skippedEntity(final String name) throws SAXException {
    }
}
