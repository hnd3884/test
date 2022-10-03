package org.apache.axiom.util.stax;

import org.apache.commons.logging.LogFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.namespace.NamespaceContext;
import org.apache.axiom.util.namespace.ScopedNamespaceContext;
import org.apache.commons.logging.Log;
import javax.xml.stream.XMLStreamWriter;

public abstract class AbstractXMLStreamWriter implements XMLStreamWriter
{
    private static final Log log;
    private final ScopedNamespaceContext namespaceContext;
    private boolean inEmptyElement;
    
    public AbstractXMLStreamWriter() {
        this.namespaceContext = new ScopedNamespaceContext();
    }
    
    public final NamespaceContext getNamespaceContext() {
        return this.namespaceContext;
    }
    
    public final void setNamespaceContext(final NamespaceContext context) throws XMLStreamException {
        throw new UnsupportedOperationException();
    }
    
    public final String getPrefix(final String uri) throws XMLStreamException {
        return this.namespaceContext.getPrefix(uri);
    }
    
    private void internalSetPrefix(final String prefix, final String uri) {
        if (this.inEmptyElement) {
            AbstractXMLStreamWriter.log.warn((Object)"The behavior of XMLStreamWriter#setPrefix and XMLStreamWriter#setDefaultNamespace is undefined when invoked in the context of an empty element");
        }
        this.namespaceContext.setPrefix(prefix, uri);
    }
    
    public final void setDefaultNamespace(final String uri) throws XMLStreamException {
        this.internalSetPrefix("", uri);
    }
    
    public final void setPrefix(final String prefix, final String uri) throws XMLStreamException {
        this.internalSetPrefix(prefix, uri);
    }
    
    public final void writeStartDocument() throws XMLStreamException {
        this.doWriteStartDocument();
    }
    
    protected abstract void doWriteStartDocument() throws XMLStreamException;
    
    public final void writeStartDocument(final String encoding, final String version) throws XMLStreamException {
        this.doWriteStartDocument(encoding, version);
    }
    
    protected abstract void doWriteStartDocument(final String p0, final String p1) throws XMLStreamException;
    
    public final void writeStartDocument(final String version) throws XMLStreamException {
        this.doWriteStartDocument(version);
    }
    
    protected abstract void doWriteStartDocument(final String p0) throws XMLStreamException;
    
    public final void writeDTD(final String dtd) throws XMLStreamException {
        this.doWriteDTD(dtd);
    }
    
    protected abstract void doWriteDTD(final String p0) throws XMLStreamException;
    
    public final void writeEndDocument() throws XMLStreamException {
        this.doWriteEndDocument();
    }
    
    protected abstract void doWriteEndDocument() throws XMLStreamException;
    
    private String internalGetPrefix(final String namespaceURI) throws XMLStreamException {
        final String prefix = this.namespaceContext.getPrefix(namespaceURI);
        if (prefix == null) {
            throw new XMLStreamException("Unbound namespace URI '" + namespaceURI + "'");
        }
        return prefix;
    }
    
    public final void writeStartElement(final String prefix, final String localName, final String namespaceURI) throws XMLStreamException {
        this.doWriteStartElement(prefix, localName, namespaceURI);
        this.namespaceContext.startScope();
        this.inEmptyElement = false;
    }
    
    public final void writeStartElement(final String namespaceURI, final String localName) throws XMLStreamException {
        this.doWriteStartElement(this.internalGetPrefix(namespaceURI), localName, namespaceURI);
        this.namespaceContext.startScope();
        this.inEmptyElement = false;
    }
    
    protected abstract void doWriteStartElement(final String p0, final String p1, final String p2) throws XMLStreamException;
    
    public final void writeStartElement(final String localName) throws XMLStreamException {
        this.doWriteStartElement(localName);
        this.namespaceContext.startScope();
        this.inEmptyElement = false;
    }
    
    protected abstract void doWriteStartElement(final String p0) throws XMLStreamException;
    
    public final void writeEndElement() throws XMLStreamException {
        this.doWriteEndElement();
        this.namespaceContext.endScope();
        this.inEmptyElement = false;
    }
    
    protected abstract void doWriteEndElement() throws XMLStreamException;
    
    public final void writeEmptyElement(final String prefix, final String localName, final String namespaceURI) throws XMLStreamException {
        this.doWriteEmptyElement(prefix, localName, namespaceURI);
        this.inEmptyElement = true;
    }
    
    public final void writeEmptyElement(final String namespaceURI, final String localName) throws XMLStreamException {
        this.doWriteEmptyElement(this.internalGetPrefix(namespaceURI), localName, namespaceURI);
        this.inEmptyElement = true;
    }
    
    protected abstract void doWriteEmptyElement(final String p0, final String p1, final String p2) throws XMLStreamException;
    
    public final void writeEmptyElement(final String localName) throws XMLStreamException {
        this.doWriteEmptyElement(localName);
        this.inEmptyElement = true;
    }
    
    protected abstract void doWriteEmptyElement(final String p0) throws XMLStreamException;
    
    public final void writeAttribute(final String prefix, final String namespaceURI, final String localName, final String value) throws XMLStreamException {
        this.doWriteAttribute(prefix, namespaceURI, localName, value);
    }
    
    public final void writeAttribute(final String namespaceURI, final String localName, final String value) throws XMLStreamException {
        this.doWriteAttribute(this.internalGetPrefix(namespaceURI), namespaceURI, localName, value);
    }
    
    protected abstract void doWriteAttribute(final String p0, final String p1, final String p2, final String p3) throws XMLStreamException;
    
    public final void writeAttribute(final String localName, final String value) throws XMLStreamException {
        this.doWriteAttribute(localName, value);
    }
    
    protected abstract void doWriteAttribute(final String p0, final String p1) throws XMLStreamException;
    
    public final void writeNamespace(final String prefix, final String namespaceURI) throws XMLStreamException {
        this.doWriteNamespace(prefix, namespaceURI);
        this.namespaceContext.setPrefix(prefix, namespaceURI);
    }
    
    protected abstract void doWriteNamespace(final String p0, final String p1) throws XMLStreamException;
    
    public final void writeDefaultNamespace(final String namespaceURI) throws XMLStreamException {
        this.doWriteDefaultNamespace(namespaceURI);
        this.namespaceContext.setPrefix("", namespaceURI);
    }
    
    protected abstract void doWriteDefaultNamespace(final String p0) throws XMLStreamException;
    
    public final void writeCharacters(final char[] text, final int start, final int len) throws XMLStreamException {
        this.doWriteCharacters(text, start, len);
        this.inEmptyElement = false;
    }
    
    protected abstract void doWriteCharacters(final char[] p0, final int p1, final int p2) throws XMLStreamException;
    
    public final void writeCharacters(final String text) throws XMLStreamException {
        this.doWriteCharacters(text);
        this.inEmptyElement = false;
    }
    
    protected abstract void doWriteCharacters(final String p0) throws XMLStreamException;
    
    public final void writeCData(final String data) throws XMLStreamException {
        this.doWriteCData(data);
        this.inEmptyElement = false;
    }
    
    protected abstract void doWriteCData(final String p0) throws XMLStreamException;
    
    public final void writeComment(final String data) throws XMLStreamException {
        this.doWriteComment(data);
        this.inEmptyElement = false;
    }
    
    protected abstract void doWriteComment(final String p0) throws XMLStreamException;
    
    public final void writeEntityRef(final String name) throws XMLStreamException {
        this.doWriteEntityRef(name);
        this.inEmptyElement = false;
    }
    
    protected abstract void doWriteEntityRef(final String p0) throws XMLStreamException;
    
    public final void writeProcessingInstruction(final String target, final String data) throws XMLStreamException {
        this.doWriteProcessingInstruction(target, data);
        this.inEmptyElement = false;
    }
    
    protected abstract void doWriteProcessingInstruction(final String p0, final String p1) throws XMLStreamException;
    
    public final void writeProcessingInstruction(final String target) throws XMLStreamException {
        this.doWriteProcessingInstruction(target);
        this.inEmptyElement = false;
    }
    
    protected abstract void doWriteProcessingInstruction(final String p0) throws XMLStreamException;
    
    static {
        log = LogFactory.getLog((Class)AbstractXMLStreamWriter.class);
    }
}
