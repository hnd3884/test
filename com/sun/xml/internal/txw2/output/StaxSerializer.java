package com.sun.xml.internal.txw2.output;

import javax.xml.stream.XMLStreamException;
import com.sun.xml.internal.txw2.TxwException;
import javax.xml.stream.XMLStreamWriter;

public class StaxSerializer implements XmlSerializer
{
    private final XMLStreamWriter out;
    
    public StaxSerializer(final XMLStreamWriter writer) {
        this(writer, true);
    }
    
    public StaxSerializer(XMLStreamWriter writer, final boolean indenting) {
        if (indenting) {
            writer = new IndentingXMLStreamWriter(writer);
        }
        this.out = writer;
    }
    
    @Override
    public void startDocument() {
        try {
            this.out.writeStartDocument();
        }
        catch (final XMLStreamException e) {
            throw new TxwException(e);
        }
    }
    
    @Override
    public void beginStartTag(final String uri, final String localName, final String prefix) {
        try {
            this.out.writeStartElement(prefix, localName, uri);
        }
        catch (final XMLStreamException e) {
            throw new TxwException(e);
        }
    }
    
    @Override
    public void writeAttribute(final String uri, final String localName, final String prefix, final StringBuilder value) {
        try {
            this.out.writeAttribute(prefix, uri, localName, value.toString());
        }
        catch (final XMLStreamException e) {
            throw new TxwException(e);
        }
    }
    
    @Override
    public void writeXmlns(final String prefix, final String uri) {
        try {
            if (prefix.length() == 0) {
                this.out.setDefaultNamespace(uri);
            }
            else {
                this.out.setPrefix(prefix, uri);
            }
            this.out.writeNamespace(prefix, uri);
        }
        catch (final XMLStreamException e) {
            throw new TxwException(e);
        }
    }
    
    @Override
    public void endStartTag(final String uri, final String localName, final String prefix) {
    }
    
    @Override
    public void endTag() {
        try {
            this.out.writeEndElement();
        }
        catch (final XMLStreamException e) {
            throw new TxwException(e);
        }
    }
    
    @Override
    public void text(final StringBuilder text) {
        try {
            this.out.writeCharacters(text.toString());
        }
        catch (final XMLStreamException e) {
            throw new TxwException(e);
        }
    }
    
    @Override
    public void cdata(final StringBuilder text) {
        try {
            this.out.writeCData(text.toString());
        }
        catch (final XMLStreamException e) {
            throw new TxwException(e);
        }
    }
    
    @Override
    public void comment(final StringBuilder comment) {
        try {
            this.out.writeComment(comment.toString());
        }
        catch (final XMLStreamException e) {
            throw new TxwException(e);
        }
    }
    
    @Override
    public void endDocument() {
        try {
            this.out.writeEndDocument();
            this.out.flush();
        }
        catch (final XMLStreamException e) {
            throw new TxwException(e);
        }
    }
    
    @Override
    public void flush() {
        try {
            this.out.flush();
        }
        catch (final XMLStreamException e) {
            throw new TxwException(e);
        }
    }
}
