package com.sun.org.apache.xalan.internal.xsltc.trax;

import org.xml.sax.ext.Locator2;
import org.xml.sax.Attributes;
import javax.xml.stream.XMLStreamException;
import org.xml.sax.SAXException;
import javax.xml.stream.XMLStreamWriter;

public class SAX2StAXStreamWriter extends SAX2StAXBaseWriter
{
    private XMLStreamWriter writer;
    private boolean needToCallStartDocument;
    
    public SAX2StAXStreamWriter() {
        this.needToCallStartDocument = false;
    }
    
    public SAX2StAXStreamWriter(final XMLStreamWriter writer) {
        this.needToCallStartDocument = false;
        this.writer = writer;
    }
    
    public XMLStreamWriter getStreamWriter() {
        return this.writer;
    }
    
    public void setStreamWriter(final XMLStreamWriter writer) {
        this.writer = writer;
    }
    
    @Override
    public void startDocument() throws SAXException {
        super.startDocument();
        this.needToCallStartDocument = true;
    }
    
    @Override
    public void endDocument() throws SAXException {
        try {
            this.writer.writeEndDocument();
        }
        catch (final XMLStreamException e) {
            throw new SAXException(e);
        }
        super.endDocument();
    }
    
    @Override
    public void startElement(final String uri, final String localName, final String qName, final Attributes attributes) throws SAXException {
        if (this.needToCallStartDocument) {
            try {
                if (this.docLocator == null) {
                    this.writer.writeStartDocument();
                }
                else {
                    try {
                        this.writer.writeStartDocument(((Locator2)this.docLocator).getXMLVersion());
                    }
                    catch (final ClassCastException e) {
                        this.writer.writeStartDocument();
                    }
                }
            }
            catch (final XMLStreamException e2) {
                throw new SAXException(e2);
            }
            this.needToCallStartDocument = false;
        }
        try {
            final String[] qname = { null, null };
            SAX2StAXBaseWriter.parseQName(qName, qname);
            this.writer.writeStartElement(qName);
            for (int i = 0, s = attributes.getLength(); i < s; ++i) {
                SAX2StAXBaseWriter.parseQName(attributes.getQName(i), qname);
                final String attrPrefix = qname[0];
                final String attrLocal = qname[1];
                final String attrQName = attributes.getQName(i);
                final String attrValue = attributes.getValue(i);
                final String attrURI = attributes.getURI(i);
                if ("xmlns".equals(attrPrefix) || "xmlns".equals(attrQName)) {
                    if (attrLocal.length() == 0) {
                        this.writer.setDefaultNamespace(attrValue);
                    }
                    else {
                        this.writer.setPrefix(attrLocal, attrValue);
                    }
                    this.writer.writeNamespace(attrLocal, attrValue);
                }
                else if (attrPrefix.length() > 0) {
                    this.writer.writeAttribute(attrPrefix, attrURI, attrLocal, attrValue);
                }
                else {
                    this.writer.writeAttribute(attrQName, attrValue);
                }
            }
        }
        catch (final XMLStreamException e2) {
            throw new SAXException(e2);
        }
        finally {
            super.startElement(uri, localName, qName, attributes);
        }
    }
    
    @Override
    public void endElement(final String uri, final String localName, final String qName) throws SAXException {
        try {
            this.writer.writeEndElement();
        }
        catch (final XMLStreamException e) {
            throw new SAXException(e);
        }
        finally {
            super.endElement(uri, localName, qName);
        }
    }
    
    @Override
    public void comment(final char[] ch, final int start, final int length) throws SAXException {
        super.comment(ch, start, length);
        try {
            this.writer.writeComment(new String(ch, start, length));
        }
        catch (final XMLStreamException e) {
            throw new SAXException(e);
        }
    }
    
    @Override
    public void characters(final char[] ch, final int start, final int length) throws SAXException {
        super.characters(ch, start, length);
        try {
            if (!this.isCDATA) {
                this.writer.writeCharacters(ch, start, length);
            }
        }
        catch (final XMLStreamException e) {
            throw new SAXException(e);
        }
    }
    
    @Override
    public void endCDATA() throws SAXException {
        try {
            this.writer.writeCData(this.CDATABuffer.toString());
        }
        catch (final XMLStreamException e) {
            throw new SAXException(e);
        }
        super.endCDATA();
    }
    
    @Override
    public void ignorableWhitespace(final char[] ch, final int start, final int length) throws SAXException {
        super.ignorableWhitespace(ch, start, length);
        try {
            this.writer.writeCharacters(ch, start, length);
        }
        catch (final XMLStreamException e) {
            throw new SAXException(e);
        }
    }
    
    @Override
    public void processingInstruction(final String target, final String data) throws SAXException {
        super.processingInstruction(target, data);
        try {
            this.writer.writeProcessingInstruction(target, data);
        }
        catch (final XMLStreamException e) {
            throw new SAXException(e);
        }
    }
}
