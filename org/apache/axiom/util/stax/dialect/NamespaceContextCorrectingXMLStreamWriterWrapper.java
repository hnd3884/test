package org.apache.axiom.util.stax.dialect;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.apache.axiom.util.stax.AbstractXMLStreamWriter;

class NamespaceContextCorrectingXMLStreamWriterWrapper extends AbstractXMLStreamWriter
{
    private final XMLStreamWriter parent;
    
    public NamespaceContextCorrectingXMLStreamWriterWrapper(final XMLStreamWriter parent) {
        this.parent = parent;
    }
    
    @Override
    protected void doWriteAttribute(final String prefix, final String namespaceURI, final String localName, final String value) throws XMLStreamException {
        this.parent.writeAttribute(prefix, namespaceURI, localName, value);
    }
    
    @Override
    protected void doWriteAttribute(final String localName, final String value) throws XMLStreamException {
        this.parent.writeAttribute(localName, value);
    }
    
    @Override
    protected void doWriteCData(final String data) throws XMLStreamException {
        this.parent.writeCData(data);
    }
    
    @Override
    protected void doWriteCharacters(final char[] text, final int start, final int len) throws XMLStreamException {
        this.parent.writeCharacters(text, start, len);
    }
    
    @Override
    protected void doWriteCharacters(final String text) throws XMLStreamException {
        this.parent.writeCharacters(text);
    }
    
    @Override
    protected void doWriteComment(final String data) throws XMLStreamException {
        this.parent.writeComment(data);
    }
    
    @Override
    protected void doWriteDefaultNamespace(final String namespaceURI) throws XMLStreamException {
        this.parent.writeDefaultNamespace(namespaceURI);
    }
    
    @Override
    protected void doWriteDTD(final String dtd) throws XMLStreamException {
        this.parent.writeDTD(dtd);
    }
    
    @Override
    protected void doWriteEmptyElement(final String prefix, final String localName, final String namespaceURI) throws XMLStreamException {
        this.parent.writeEmptyElement(prefix, localName, namespaceURI);
    }
    
    @Override
    protected void doWriteEmptyElement(final String localName) throws XMLStreamException {
        this.parent.writeEmptyElement(localName);
    }
    
    @Override
    protected void doWriteEndDocument() throws XMLStreamException {
        this.parent.writeEndDocument();
    }
    
    @Override
    protected void doWriteEndElement() throws XMLStreamException {
        this.parent.writeEndElement();
    }
    
    @Override
    protected void doWriteEntityRef(final String name) throws XMLStreamException {
        this.parent.writeEntityRef(name);
    }
    
    @Override
    protected void doWriteNamespace(final String prefix, final String namespaceURI) throws XMLStreamException {
        this.parent.writeNamespace(prefix, namespaceURI);
    }
    
    @Override
    protected void doWriteProcessingInstruction(final String target, final String data) throws XMLStreamException {
        this.parent.writeProcessingInstruction(target, data);
    }
    
    @Override
    protected void doWriteProcessingInstruction(final String target) throws XMLStreamException {
        this.parent.writeProcessingInstruction(target);
    }
    
    @Override
    protected void doWriteStartDocument() throws XMLStreamException {
        this.parent.writeStartDocument();
    }
    
    @Override
    protected void doWriteStartDocument(final String encoding, final String version) throws XMLStreamException {
        this.parent.writeStartDocument(encoding, version);
    }
    
    @Override
    protected void doWriteStartDocument(final String version) throws XMLStreamException {
        this.parent.writeStartDocument(version);
    }
    
    @Override
    protected void doWriteStartElement(final String prefix, final String localName, final String namespaceURI) throws XMLStreamException {
        this.parent.writeStartElement(prefix, localName, namespaceURI);
    }
    
    @Override
    protected void doWriteStartElement(final String localName) throws XMLStreamException {
        this.parent.writeStartElement(localName);
    }
    
    public void close() throws XMLStreamException {
        this.parent.close();
    }
    
    public void flush() throws XMLStreamException {
        this.parent.flush();
    }
    
    public Object getProperty(final String name) throws IllegalArgumentException {
        return this.parent.getProperty(name);
    }
}
