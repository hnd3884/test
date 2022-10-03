package org.apache.axiom.om.impl.common.serializer.push.sax;

import org.apache.axiom.ext.stax.datahandler.DataHandlerProvider;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import org.apache.axiom.util.base64.Base64EncodingWriterOutputStream;
import javax.activation.DataHandler;
import org.xml.sax.SAXException;
import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamException;
import org.apache.axiom.util.namespace.ScopedNamespaceContext;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.ContentHandler;
import org.apache.axiom.ext.stax.datahandler.DataHandlerWriter;
import javax.xml.stream.XMLStreamWriter;

final class ContentHandlerXMLStreamWriter implements XMLStreamWriter, DataHandlerWriter
{
    private final SAXHelper helper;
    private final ContentHandler contentHandler;
    private final LexicalHandler lexicalHandler;
    private final ScopedNamespaceContext writerNsContext;
    private final ScopedNamespaceContext outputNsContext;
    
    ContentHandlerXMLStreamWriter(final SAXHelper helper, final ContentHandler contentHandler, final LexicalHandler lexicalHandler, final ScopedNamespaceContext nsContext) {
        this.outputNsContext = new ScopedNamespaceContext();
        this.helper = helper;
        this.contentHandler = contentHandler;
        this.lexicalHandler = lexicalHandler;
        this.writerNsContext = nsContext;
    }
    
    private static String normalize(final String s) {
        return (s == null) ? "" : s;
    }
    
    private String internalGetPrefix(final String namespaceURI) throws XMLStreamException {
        final String prefix = this.writerNsContext.getPrefix(namespaceURI);
        if (prefix == null) {
            throw new XMLStreamException("Unbound namespace URI '" + namespaceURI + "'");
        }
        return prefix;
    }
    
    public Object getProperty(final String name) throws IllegalArgumentException {
        if (name.equals(DataHandlerWriter.PROPERTY)) {
            return this;
        }
        return null;
    }
    
    public NamespaceContext getNamespaceContext() {
        return (NamespaceContext)this.writerNsContext;
    }
    
    public void setPrefix(final String prefix, final String uri) throws XMLStreamException {
        this.writerNsContext.setPrefix(normalize(prefix), normalize(uri));
    }
    
    public void setDefaultNamespace(final String uri) throws XMLStreamException {
        this.writerNsContext.setPrefix("", normalize(uri));
    }
    
    public String getPrefix(final String uri) throws XMLStreamException {
        return this.writerNsContext.getPrefix(uri);
    }
    
    public void writeStartElement(final String prefix, final String localName, final String namespaceURI) throws XMLStreamException {
        this.finishStartElementIfNecessary();
        this.helper.beginStartElement(normalize(prefix), normalize(namespaceURI), localName);
        this.writerNsContext.startScope();
        this.outputNsContext.startScope();
    }
    
    public void writeStartElement(final String namespaceURI, final String localName) throws XMLStreamException {
        this.writeStartElement(this.internalGetPrefix(namespaceURI), localName, namespaceURI);
    }
    
    public void writeEmptyElement(final String prefix, final String localName, final String namespaceURI) throws XMLStreamException {
        this.finishStartElementIfNecessary();
        this.helper.beginStartElement(normalize(prefix), normalize(namespaceURI), localName);
        try {
            this.helper.finishStartElement(this.contentHandler);
            this.helper.writeEndElement(this.contentHandler, null);
        }
        catch (final SAXException ex) {
            throw new SAXExceptionWrapper(ex);
        }
    }
    
    public void writeEmptyElement(final String namespaceURI, final String localName) throws XMLStreamException {
        this.writeEmptyElement(this.internalGetPrefix(namespaceURI), localName, namespaceURI);
    }
    
    public void writeNamespace(String prefix, String namespaceURI) throws XMLStreamException {
        prefix = normalize(prefix);
        namespaceURI = normalize(namespaceURI);
        this.outputNsContext.setPrefix(prefix, namespaceURI);
        try {
            this.contentHandler.startPrefixMapping(prefix, namespaceURI);
        }
        catch (final SAXException ex) {
            throw new SAXExceptionWrapper(ex);
        }
    }
    
    public void writeDefaultNamespace(String namespaceURI) throws XMLStreamException {
        namespaceURI = normalize(namespaceURI);
        this.outputNsContext.setPrefix("", namespaceURI);
        try {
            this.contentHandler.startPrefixMapping("", namespaceURI);
        }
        catch (final SAXException ex) {
            throw new SAXExceptionWrapper(ex);
        }
    }
    
    public void writeAttribute(final String prefix, final String namespaceURI, final String localName, final String value) throws XMLStreamException {
        this.helper.addAttribute(normalize(prefix), normalize(namespaceURI), localName, "CDATA", value);
    }
    
    public void writeAttribute(final String namespaceURI, final String localName, final String value) throws XMLStreamException {
        this.helper.addAttribute(this.internalGetPrefix(namespaceURI), normalize(namespaceURI), localName, "CDATA", value);
    }
    
    public void writeAttribute(final String localName, final String value) throws XMLStreamException {
        this.helper.addAttribute("", "", localName, "CDATA", value);
    }
    
    private void finishStartElementIfNecessary() throws XMLStreamException {
        if (this.helper.isInStartElement()) {
            try {
                this.helper.finishStartElement(this.contentHandler);
            }
            catch (final SAXException ex) {
                throw new SAXExceptionWrapper(ex);
            }
        }
    }
    
    public void writeEndElement() throws XMLStreamException {
        this.finishStartElementIfNecessary();
        try {
            this.helper.writeEndElement(this.contentHandler, this.outputNsContext);
            this.writerNsContext.endScope();
        }
        catch (final SAXException ex) {
            throw new SAXExceptionWrapper(ex);
        }
    }
    
    public void writeCharacters(final char[] text, final int start, final int len) throws XMLStreamException {
        this.finishStartElementIfNecessary();
        try {
            this.contentHandler.characters(text, start, len);
        }
        catch (final SAXException ex) {
            throw new SAXExceptionWrapper(ex);
        }
    }
    
    public void writeCharacters(final String text) throws XMLStreamException {
        final char[] ch = text.toCharArray();
        this.writeCharacters(ch, 0, ch.length);
    }
    
    public void writeCData(final String data) throws XMLStreamException {
        this.finishStartElementIfNecessary();
        try {
            if (this.lexicalHandler != null) {
                this.lexicalHandler.startCDATA();
            }
            final char[] ch = data.toCharArray();
            this.contentHandler.characters(ch, 0, ch.length);
            if (this.lexicalHandler != null) {
                this.lexicalHandler.endCDATA();
            }
        }
        catch (final SAXException ex) {
            throw new SAXExceptionWrapper(ex);
        }
    }
    
    public void writeDataHandler(final DataHandler dataHandler, final String contentID, final boolean optimize) throws IOException, XMLStreamException {
        this.finishStartElementIfNecessary();
        final Base64EncodingWriterOutputStream out = new Base64EncodingWriterOutputStream((Writer)new ContentHandlerWriter(this.contentHandler), 4096, true);
        dataHandler.writeTo((OutputStream)out);
        out.complete();
    }
    
    public void writeDataHandler(final DataHandlerProvider dataHandlerProvider, final String contentID, final boolean optimize) throws IOException, XMLStreamException {
        this.writeDataHandler(dataHandlerProvider.getDataHandler(), contentID, optimize);
    }
    
    public void writeComment(final String data) throws XMLStreamException {
        this.finishStartElementIfNecessary();
        if (this.lexicalHandler != null) {
            try {
                final char[] ch = data.toCharArray();
                this.lexicalHandler.comment(ch, 0, ch.length);
            }
            catch (final SAXException ex) {
                throw new SAXExceptionWrapper(ex);
            }
        }
    }
    
    public void writeProcessingInstruction(final String target) throws XMLStreamException {
        this.finishStartElementIfNecessary();
        try {
            this.contentHandler.processingInstruction(target, "");
        }
        catch (final SAXException ex) {
            throw new SAXExceptionWrapper(ex);
        }
    }
    
    public void writeProcessingInstruction(final String target, final String data) throws XMLStreamException {
        this.finishStartElementIfNecessary();
        try {
            this.contentHandler.processingInstruction(target, data);
        }
        catch (final SAXException ex) {
            throw new SAXExceptionWrapper(ex);
        }
    }
    
    public void writeEntityRef(final String name) throws XMLStreamException {
        this.finishStartElementIfNecessary();
        try {
            this.contentHandler.skippedEntity(name);
        }
        catch (final SAXException ex) {
            throw new SAXExceptionWrapper(ex);
        }
    }
    
    public void flush() throws XMLStreamException {
    }
    
    public void close() throws XMLStreamException {
        throw new UnsupportedOperationException("OMDataSource#serialize(XMLStreamWriter) MUST NOT call XMLStreamWriter#close()");
    }
    
    public void writeStartDocument() throws XMLStreamException {
        throw new UnsupportedOperationException("OMDataSource#serialize(XMLStreamWriter) MUST NOT use XMLStreamWriter#writeStartDocument()");
    }
    
    public void writeStartDocument(final String version) throws XMLStreamException {
        throw new UnsupportedOperationException("OMDataSource#serialize(XMLStreamWriter) MUST NOT use XMLStreamWriter#writeStartDocument(String)");
    }
    
    public void writeStartDocument(final String encoding, final String version) throws XMLStreamException {
        throw new UnsupportedOperationException("OMDataSource#serialize(XMLStreamWriter) MUST NOT use XMLStreamWriter#writeStartDocument(String, String)");
    }
    
    public void writeEndDocument() throws XMLStreamException {
        throw new UnsupportedOperationException("OMDataSource#serialize(XMLStreamWriter) MUST NOT use XMLStreamWriter#writeEndDocument()");
    }
    
    public void writeStartElement(final String localName) throws XMLStreamException {
        throw new UnsupportedOperationException("OMDataSource#serialize(XMLStreamWriter) MUST NOT use XMLStreamWriter#writeStartElement(String)");
    }
    
    public void writeEmptyElement(final String localName) throws XMLStreamException {
        throw new UnsupportedOperationException("OMDataSource#serialize(XMLStreamWriter) MUST NOT use XMLStreamWriter#writeEmptyElement(String)");
    }
    
    public void setNamespaceContext(final NamespaceContext context) throws XMLStreamException {
        throw new UnsupportedOperationException();
    }
    
    public void writeDTD(final String dtd) throws XMLStreamException {
        throw new UnsupportedOperationException();
    }
}
