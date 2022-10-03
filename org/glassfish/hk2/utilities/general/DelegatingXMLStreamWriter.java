package org.glassfish.hk2.utilities.general;

import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

abstract class DelegatingXMLStreamWriter implements XMLStreamWriter
{
    private final XMLStreamWriter writer;
    
    public DelegatingXMLStreamWriter(final XMLStreamWriter writer) {
        this.writer = writer;
    }
    
    @Override
    public void writeStartElement(final String localName) throws XMLStreamException {
        this.writer.writeStartElement(localName);
    }
    
    @Override
    public void writeStartElement(final String namespaceURI, final String localName) throws XMLStreamException {
        this.writer.writeStartElement(namespaceURI, localName);
    }
    
    @Override
    public void writeStartElement(final String prefix, final String localName, final String namespaceURI) throws XMLStreamException {
        this.writer.writeStartElement(prefix, localName, namespaceURI);
    }
    
    @Override
    public void writeEmptyElement(final String namespaceURI, final String localName) throws XMLStreamException {
        this.writer.writeEmptyElement(namespaceURI, localName);
    }
    
    @Override
    public void writeEmptyElement(final String prefix, final String localName, final String namespaceURI) throws XMLStreamException {
        this.writer.writeEmptyElement(prefix, localName, namespaceURI);
    }
    
    @Override
    public void writeEmptyElement(final String localName) throws XMLStreamException {
        this.writer.writeEmptyElement(localName);
    }
    
    @Override
    public void writeEndElement() throws XMLStreamException {
        this.writer.writeEndElement();
    }
    
    @Override
    public void writeEndDocument() throws XMLStreamException {
        this.writer.writeEndDocument();
    }
    
    @Override
    public void close() throws XMLStreamException {
        this.writer.close();
    }
    
    @Override
    public void flush() throws XMLStreamException {
        this.writer.flush();
    }
    
    @Override
    public void writeAttribute(final String localName, final String value) throws XMLStreamException {
        this.writer.writeAttribute(localName, value);
    }
    
    @Override
    public void writeAttribute(final String prefix, final String namespaceURI, final String localName, final String value) throws XMLStreamException {
        this.writer.writeAttribute(prefix, namespaceURI, localName, value);
    }
    
    @Override
    public void writeAttribute(final String namespaceURI, final String localName, final String value) throws XMLStreamException {
        this.writer.writeAttribute(namespaceURI, localName, value);
    }
    
    @Override
    public void writeNamespace(final String prefix, final String namespaceURI) throws XMLStreamException {
        this.writer.writeNamespace(prefix, namespaceURI);
    }
    
    @Override
    public void writeDefaultNamespace(final String namespaceURI) throws XMLStreamException {
        this.writer.writeDefaultNamespace(namespaceURI);
    }
    
    @Override
    public void writeComment(final String data) throws XMLStreamException {
        this.writer.writeComment(data);
    }
    
    @Override
    public void writeProcessingInstruction(final String target) throws XMLStreamException {
        this.writer.writeProcessingInstruction(target);
    }
    
    @Override
    public void writeProcessingInstruction(final String target, final String data) throws XMLStreamException {
        this.writer.writeProcessingInstruction(target, data);
    }
    
    @Override
    public void writeCData(final String data) throws XMLStreamException {
        this.writer.writeCData(data);
    }
    
    @Override
    public void writeDTD(final String dtd) throws XMLStreamException {
        this.writer.writeDTD(dtd);
    }
    
    @Override
    public void writeEntityRef(final String name) throws XMLStreamException {
        this.writer.writeEntityRef(name);
    }
    
    @Override
    public void writeStartDocument() throws XMLStreamException {
        this.writer.writeStartDocument();
    }
    
    @Override
    public void writeStartDocument(final String version) throws XMLStreamException {
        this.writer.writeStartDocument(version);
    }
    
    @Override
    public void writeStartDocument(final String encoding, final String version) throws XMLStreamException {
        this.writer.writeStartDocument(encoding, version);
    }
    
    @Override
    public void writeCharacters(final String text) throws XMLStreamException {
        this.writer.writeCharacters(text);
    }
    
    @Override
    public void writeCharacters(final char[] text, final int start, final int len) throws XMLStreamException {
        this.writer.writeCharacters(text, start, len);
    }
    
    @Override
    public String getPrefix(final String uri) throws XMLStreamException {
        return this.writer.getPrefix(uri);
    }
    
    @Override
    public void setPrefix(final String prefix, final String uri) throws XMLStreamException {
        this.writer.setPrefix(prefix, uri);
    }
    
    @Override
    public void setDefaultNamespace(final String uri) throws XMLStreamException {
        this.writer.setDefaultNamespace(uri);
    }
    
    @Override
    public void setNamespaceContext(final NamespaceContext context) throws XMLStreamException {
        this.writer.setNamespaceContext(context);
    }
    
    @Override
    public NamespaceContext getNamespaceContext() {
        return this.writer.getNamespaceContext();
    }
    
    @Override
    public Object getProperty(final String name) throws IllegalArgumentException {
        return this.writer.getProperty(name);
    }
}
