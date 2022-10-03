package org.apache.axiom.util.stax.xop;

import javax.xml.namespace.NamespaceContext;
import org.apache.axiom.ext.stax.datahandler.DataHandlerProvider;
import java.io.IOException;
import org.apache.axiom.util.stax.XMLStreamWriterUtils;
import javax.activation.DataHandler;
import javax.xml.stream.XMLStreamException;
import org.apache.axiom.ext.stax.datahandler.DataHandlerWriter;
import javax.xml.stream.XMLStreamWriter;

public class XOPEncodingStreamWriter extends XOPEncodingStreamWrapper implements XMLStreamWriter, DataHandlerWriter
{
    private final XMLStreamWriter parent;
    
    public XOPEncodingStreamWriter(final XMLStreamWriter parent, final ContentIDGenerator contentIDGenerator, final OptimizationPolicy optimizationPolicy) {
        super(contentIDGenerator, optimizationPolicy);
        this.parent = parent;
    }
    
    public Object getProperty(final String name) throws IllegalArgumentException {
        if (DataHandlerWriter.PROPERTY.equals(name)) {
            return this;
        }
        return this.parent.getProperty(name);
    }
    
    private void writeXOPInclude(final String contentID) throws XMLStreamException {
        final String writerPrefix = this.parent.getPrefix("http://www.w3.org/2004/08/xop/include");
        if (writerPrefix != null) {
            this.parent.writeStartElement("http://www.w3.org/2004/08/xop/include", "Include");
        }
        else {
            this.parent.writeStartElement("xop", "Include", "http://www.w3.org/2004/08/xop/include");
            this.parent.setPrefix("xop", "http://www.w3.org/2004/08/xop/include");
            this.parent.writeNamespace("xop", "http://www.w3.org/2004/08/xop/include");
        }
        this.parent.writeAttribute("href", XOPUtils.getURLForContentID(contentID));
        this.parent.writeEndElement();
    }
    
    public void writeDataHandler(final DataHandler dataHandler, String contentID, final boolean optimize) throws IOException, XMLStreamException {
        contentID = this.processDataHandler(dataHandler, contentID, optimize);
        if (contentID != null) {
            this.writeXOPInclude(contentID);
        }
        else {
            XMLStreamWriterUtils.writeBase64(this.parent, dataHandler);
        }
    }
    
    public void writeDataHandler(final DataHandlerProvider dataHandlerProvider, String contentID, final boolean optimize) throws IOException, XMLStreamException {
        contentID = this.processDataHandler(dataHandlerProvider, contentID, optimize);
        if (contentID != null) {
            this.writeXOPInclude(contentID);
        }
        else {
            XMLStreamWriterUtils.writeBase64(this.parent, dataHandlerProvider.getDataHandler());
        }
    }
    
    public void close() throws XMLStreamException {
        this.parent.close();
    }
    
    public void flush() throws XMLStreamException {
        this.parent.flush();
    }
    
    public NamespaceContext getNamespaceContext() {
        return this.parent.getNamespaceContext();
    }
    
    public String getPrefix(final String uri) throws XMLStreamException {
        return this.parent.getPrefix(uri);
    }
    
    public void setDefaultNamespace(final String uri) throws XMLStreamException {
        this.parent.setDefaultNamespace(uri);
    }
    
    public void setNamespaceContext(final NamespaceContext context) throws XMLStreamException {
        this.parent.setNamespaceContext(context);
    }
    
    public void setPrefix(final String prefix, final String uri) throws XMLStreamException {
        this.parent.setPrefix(prefix, uri);
    }
    
    public void writeAttribute(final String prefix, final String namespaceURI, final String localName, final String value) throws XMLStreamException {
        this.parent.writeAttribute(prefix, namespaceURI, localName, value);
    }
    
    public void writeAttribute(final String namespaceURI, final String localName, final String value) throws XMLStreamException {
        this.parent.writeAttribute(namespaceURI, localName, value);
    }
    
    public void writeAttribute(final String localName, final String value) throws XMLStreamException {
        this.parent.writeAttribute(localName, value);
    }
    
    public void writeCData(final String data) throws XMLStreamException {
        this.parent.writeCData(data);
    }
    
    public void writeCharacters(final char[] text, final int start, final int len) throws XMLStreamException {
        this.parent.writeCharacters(text, start, len);
    }
    
    public void writeCharacters(final String text) throws XMLStreamException {
        this.parent.writeCharacters(text);
    }
    
    public void writeComment(final String data) throws XMLStreamException {
        this.parent.writeComment(data);
    }
    
    public void writeDefaultNamespace(final String namespaceURI) throws XMLStreamException {
        this.parent.writeDefaultNamespace(namespaceURI);
    }
    
    public void writeDTD(final String dtd) throws XMLStreamException {
        this.parent.writeDTD(dtd);
    }
    
    public void writeEmptyElement(final String prefix, final String localName, final String namespaceURI) throws XMLStreamException {
        this.parent.writeEmptyElement(prefix, localName, namespaceURI);
    }
    
    public void writeEmptyElement(final String namespaceURI, final String localName) throws XMLStreamException {
        this.parent.writeEmptyElement(namespaceURI, localName);
    }
    
    public void writeEmptyElement(final String localName) throws XMLStreamException {
        this.parent.writeEmptyElement(localName);
    }
    
    public void writeEndDocument() throws XMLStreamException {
        this.parent.writeEndDocument();
    }
    
    public void writeEndElement() throws XMLStreamException {
        this.parent.writeEndElement();
    }
    
    public void writeEntityRef(final String name) throws XMLStreamException {
        this.parent.writeEntityRef(name);
    }
    
    public void writeNamespace(final String prefix, final String namespaceURI) throws XMLStreamException {
        this.parent.writeNamespace(prefix, namespaceURI);
    }
    
    public void writeProcessingInstruction(final String target, final String data) throws XMLStreamException {
        this.parent.writeProcessingInstruction(target, data);
    }
    
    public void writeProcessingInstruction(final String target) throws XMLStreamException {
        this.parent.writeProcessingInstruction(target);
    }
    
    public void writeStartDocument() throws XMLStreamException {
        this.parent.writeStartDocument();
    }
    
    public void writeStartDocument(final String encoding, final String version) throws XMLStreamException {
        this.parent.writeStartDocument(encoding, version);
    }
    
    public void writeStartDocument(final String version) throws XMLStreamException {
        this.parent.writeStartDocument(version);
    }
    
    public void writeStartElement(final String prefix, final String localName, final String namespaceURI) throws XMLStreamException {
        this.parent.writeStartElement(prefix, localName, namespaceURI);
    }
    
    public void writeStartElement(final String namespaceURI, final String localName) throws XMLStreamException {
        this.parent.writeStartElement(namespaceURI, localName);
    }
    
    public void writeStartElement(final String localName) throws XMLStreamException {
        this.parent.writeStartElement(localName);
    }
}
