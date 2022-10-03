package com.sun.xml.internal.ws.util.xml;

import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import javax.xml.stream.XMLStreamException;
import org.xml.sax.SAXException;
import java.util.Stack;
import javax.xml.stream.XMLStreamWriter;
import org.xml.sax.helpers.DefaultHandler;

public class ContentHandlerToXMLStreamWriter extends DefaultHandler
{
    private final XMLStreamWriter staxWriter;
    private final Stack prefixBindings;
    
    public ContentHandlerToXMLStreamWriter(final XMLStreamWriter staxCore) {
        this.staxWriter = staxCore;
        this.prefixBindings = new Stack();
    }
    
    @Override
    public void endDocument() throws SAXException {
        try {
            this.staxWriter.writeEndDocument();
            this.staxWriter.flush();
        }
        catch (final XMLStreamException e) {
            throw new SAXException(e);
        }
    }
    
    @Override
    public void startDocument() throws SAXException {
        try {
            this.staxWriter.writeStartDocument();
        }
        catch (final XMLStreamException e) {
            throw new SAXException(e);
        }
    }
    
    @Override
    public void characters(final char[] ch, final int start, final int length) throws SAXException {
        try {
            this.staxWriter.writeCharacters(ch, start, length);
        }
        catch (final XMLStreamException e) {
            throw new SAXException(e);
        }
    }
    
    @Override
    public void ignorableWhitespace(final char[] ch, final int start, final int length) throws SAXException {
        this.characters(ch, start, length);
    }
    
    @Override
    public void endPrefixMapping(final String prefix) throws SAXException {
    }
    
    @Override
    public void skippedEntity(final String name) throws SAXException {
        try {
            this.staxWriter.writeEntityRef(name);
        }
        catch (final XMLStreamException e) {
            throw new SAXException(e);
        }
    }
    
    @Override
    public void setDocumentLocator(final Locator locator) {
    }
    
    @Override
    public void processingInstruction(final String target, final String data) throws SAXException {
        try {
            this.staxWriter.writeProcessingInstruction(target, data);
        }
        catch (final XMLStreamException e) {
            throw new SAXException(e);
        }
    }
    
    @Override
    public void startPrefixMapping(String prefix, final String uri) throws SAXException {
        if (prefix == null) {
            prefix = "";
        }
        if (prefix.equals("xml")) {
            return;
        }
        this.prefixBindings.add(prefix);
        this.prefixBindings.add(uri);
    }
    
    @Override
    public void endElement(final String namespaceURI, final String localName, final String qName) throws SAXException {
        try {
            this.staxWriter.writeEndElement();
        }
        catch (final XMLStreamException e) {
            throw new SAXException(e);
        }
    }
    
    @Override
    public void startElement(final String namespaceURI, final String localName, final String qName, final Attributes atts) throws SAXException {
        try {
            this.staxWriter.writeStartElement(this.getPrefix(qName), localName, namespaceURI);
            while (this.prefixBindings.size() != 0) {
                final String uri = this.prefixBindings.pop();
                final String prefix = this.prefixBindings.pop();
                if (prefix.length() == 0) {
                    this.staxWriter.setDefaultNamespace(uri);
                }
                else {
                    this.staxWriter.setPrefix(prefix, uri);
                }
                this.staxWriter.writeNamespace(prefix, uri);
            }
            this.writeAttributes(atts);
        }
        catch (final XMLStreamException e) {
            throw new SAXException(e);
        }
    }
    
    private void writeAttributes(final Attributes atts) throws XMLStreamException {
        for (int i = 0; i < atts.getLength(); ++i) {
            final String prefix = this.getPrefix(atts.getQName(i));
            if (!prefix.equals("xmlns")) {
                this.staxWriter.writeAttribute(prefix, atts.getURI(i), atts.getLocalName(i), atts.getValue(i));
            }
        }
    }
    
    private String getPrefix(final String qName) {
        final int idx = qName.indexOf(58);
        if (idx == -1) {
            return "";
        }
        return qName.substring(0, idx);
    }
}
