package com.sun.xml.internal.fastinfoset.tools;

import org.xml.sax.Locator;
import java.util.logging.Level;
import com.sun.xml.internal.fastinfoset.QualifiedName;
import org.xml.sax.Attributes;
import javax.xml.stream.XMLStreamException;
import org.xml.sax.SAXException;
import java.util.ArrayList;
import javax.xml.stream.XMLStreamWriter;
import java.util.logging.Logger;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.DefaultHandler;

public class SAX2StAXWriter extends DefaultHandler implements LexicalHandler
{
    private static final Logger logger;
    XMLStreamWriter _writer;
    ArrayList _namespaces;
    
    public SAX2StAXWriter(final XMLStreamWriter writer) {
        this._namespaces = new ArrayList();
        this._writer = writer;
    }
    
    public XMLStreamWriter getWriter() {
        return this._writer;
    }
    
    @Override
    public void startDocument() throws SAXException {
        try {
            this._writer.writeStartDocument();
        }
        catch (final XMLStreamException e) {
            throw new SAXException(e);
        }
    }
    
    @Override
    public void endDocument() throws SAXException {
        try {
            this._writer.writeEndDocument();
            this._writer.flush();
        }
        catch (final XMLStreamException e) {
            throw new SAXException(e);
        }
    }
    
    @Override
    public void characters(final char[] ch, final int start, final int length) throws SAXException {
        try {
            this._writer.writeCharacters(ch, start, length);
        }
        catch (final XMLStreamException e) {
            throw new SAXException(e);
        }
    }
    
    @Override
    public void startElement(final String namespaceURI, final String localName, final String qName, final Attributes atts) throws SAXException {
        try {
            final int k = qName.indexOf(58);
            final String prefix = (k > 0) ? qName.substring(0, k) : "";
            this._writer.writeStartElement(prefix, localName, namespaceURI);
            for (int length = this._namespaces.size(), i = 0; i < length; ++i) {
                final QualifiedName nsh = this._namespaces.get(i);
                this._writer.writeNamespace(nsh.prefix, nsh.namespaceName);
            }
            this._namespaces.clear();
            for (int length = atts.getLength(), i = 0; i < length; ++i) {
                this._writer.writeAttribute(atts.getURI(i), atts.getLocalName(i), atts.getValue(i));
            }
        }
        catch (final XMLStreamException e) {
            throw new SAXException(e);
        }
    }
    
    @Override
    public void endElement(final String namespaceURI, final String localName, final String qName) throws SAXException {
        try {
            this._writer.writeEndElement();
        }
        catch (final XMLStreamException e) {
            SAX2StAXWriter.logger.log(Level.FINE, "Exception on endElement", e);
            throw new SAXException(e);
        }
    }
    
    @Override
    public void startPrefixMapping(final String prefix, final String uri) throws SAXException {
        this._namespaces.add(new QualifiedName(prefix, uri));
    }
    
    @Override
    public void endPrefixMapping(final String prefix) throws SAXException {
    }
    
    @Override
    public void ignorableWhitespace(final char[] ch, final int start, final int length) throws SAXException {
        this.characters(ch, start, length);
    }
    
    @Override
    public void processingInstruction(final String target, final String data) throws SAXException {
        try {
            this._writer.writeProcessingInstruction(target, data);
        }
        catch (final XMLStreamException e) {
            throw new SAXException(e);
        }
    }
    
    @Override
    public void setDocumentLocator(final Locator locator) {
    }
    
    @Override
    public void skippedEntity(final String name) throws SAXException {
    }
    
    @Override
    public void comment(final char[] ch, final int start, final int length) throws SAXException {
        try {
            this._writer.writeComment(new String(ch, start, length));
        }
        catch (final XMLStreamException e) {
            throw new SAXException(e);
        }
    }
    
    @Override
    public void endCDATA() throws SAXException {
    }
    
    @Override
    public void endDTD() throws SAXException {
    }
    
    @Override
    public void endEntity(final String name) throws SAXException {
    }
    
    @Override
    public void startCDATA() throws SAXException {
    }
    
    @Override
    public void startDTD(final String name, final String publicId, final String systemId) throws SAXException {
    }
    
    @Override
    public void startEntity(final String name) throws SAXException {
    }
    
    static {
        logger = Logger.getLogger(SAX2StAXWriter.class.getName());
    }
}
