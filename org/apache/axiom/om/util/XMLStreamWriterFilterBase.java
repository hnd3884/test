package org.apache.axiom.om.util;

import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public abstract class XMLStreamWriterFilterBase implements XMLStreamWriterFilter
{
    XMLStreamWriter delegate;
    
    public XMLStreamWriterFilterBase() {
        this.delegate = null;
    }
    
    public void setDelegate(final XMLStreamWriter writer) {
        this.delegate = writer;
    }
    
    public XMLStreamWriter getDelegate() {
        return this.delegate;
    }
    
    public void close() throws XMLStreamException {
        this.delegate.close();
    }
    
    public void flush() throws XMLStreamException {
        this.delegate.flush();
    }
    
    public NamespaceContext getNamespaceContext() {
        return this.delegate.getNamespaceContext();
    }
    
    public String getPrefix(final String uri) throws XMLStreamException {
        return this.delegate.getPrefix(uri);
    }
    
    public Object getProperty(final String name) throws IllegalArgumentException {
        return this.delegate.getProperty(name);
    }
    
    public void setDefaultNamespace(final String uri) throws XMLStreamException {
        this.delegate.setDefaultNamespace(uri);
    }
    
    public void setNamespaceContext(final NamespaceContext context) throws XMLStreamException {
        this.delegate.setNamespaceContext(context);
    }
    
    public void setPrefix(final String prefix, final String uri) throws XMLStreamException {
        this.delegate.setPrefix(prefix, uri);
    }
    
    public void writeAttribute(final String prefix, final String namespaceURI, final String localName, final String value) throws XMLStreamException {
        this.delegate.writeAttribute(prefix, namespaceURI, localName, this.xmlData(value));
    }
    
    public void writeAttribute(final String namespaceURI, final String localName, final String value) throws XMLStreamException {
        this.delegate.writeAttribute(namespaceURI, localName, this.xmlData(value));
    }
    
    public void writeAttribute(final String localName, final String value) throws XMLStreamException {
        this.delegate.writeAttribute(localName, this.xmlData(value));
    }
    
    public void writeCData(final String data) throws XMLStreamException {
        this.delegate.writeCData(this.xmlData(data));
    }
    
    public void writeCharacters(final char[] text, final int start, final int len) throws XMLStreamException {
        final String value = new String(text, start, len);
        this.writeCharacters(value);
    }
    
    public void writeCharacters(final String text) throws XMLStreamException {
        this.delegate.writeCharacters(this.xmlData(text));
    }
    
    public void writeComment(final String data) throws XMLStreamException {
        this.delegate.writeComment(data);
    }
    
    public void writeDTD(final String dtd) throws XMLStreamException {
        this.delegate.writeDTD(dtd);
    }
    
    public void writeDefaultNamespace(final String namespaceURI) throws XMLStreamException {
        this.delegate.writeDefaultNamespace(namespaceURI);
    }
    
    public void writeEmptyElement(final String prefix, final String localName, final String namespaceURI) throws XMLStreamException {
        this.delegate.writeEmptyElement(prefix, localName, namespaceURI);
    }
    
    public void writeEmptyElement(final String namespaceURI, final String localName) throws XMLStreamException {
        this.delegate.writeEmptyElement(namespaceURI, localName);
    }
    
    public void writeEmptyElement(final String localName) throws XMLStreamException {
        this.delegate.writeEmptyElement(localName);
    }
    
    public void writeEndDocument() throws XMLStreamException {
        this.delegate.writeEndDocument();
    }
    
    public void writeEndElement() throws XMLStreamException {
        this.delegate.writeEndElement();
    }
    
    public void writeEntityRef(final String name) throws XMLStreamException {
        this.delegate.writeEntityRef(name);
    }
    
    public void writeNamespace(final String prefix, final String namespaceURI) throws XMLStreamException {
        this.delegate.writeNamespace(prefix, namespaceURI);
    }
    
    public void writeProcessingInstruction(final String target, final String data) throws XMLStreamException {
        this.delegate.writeProcessingInstruction(target, data);
    }
    
    public void writeProcessingInstruction(final String target) throws XMLStreamException {
        this.delegate.writeProcessingInstruction(target);
    }
    
    public void writeStartDocument() throws XMLStreamException {
        this.delegate.writeStartDocument();
    }
    
    public void writeStartDocument(final String encoding, final String version) throws XMLStreamException {
        this.delegate.writeStartDocument(encoding, version);
    }
    
    public void writeStartDocument(final String version) throws XMLStreamException {
        this.delegate.writeStartDocument(version);
    }
    
    public void writeStartElement(final String prefix, final String localName, final String namespaceURI) throws XMLStreamException {
        this.delegate.writeStartElement(prefix, localName, namespaceURI);
    }
    
    public void writeStartElement(final String namespaceURI, final String localName) throws XMLStreamException {
        this.delegate.writeStartElement(namespaceURI, localName);
    }
    
    public void writeStartElement(final String localName) throws XMLStreamException {
        this.delegate.writeStartElement(localName);
    }
    
    protected abstract String xmlData(final String p0);
}
