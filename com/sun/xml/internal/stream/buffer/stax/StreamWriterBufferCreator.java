package com.sun.xml.internal.stream.buffer.stax;

import java.io.OutputStream;
import javax.activation.DataHandler;
import com.sun.xml.internal.org.jvnet.staxex.Base64Data;
import javax.xml.namespace.NamespaceContext;
import com.sun.xml.internal.org.jvnet.staxex.NamespaceContextEx;
import javax.xml.stream.XMLStreamException;
import com.sun.xml.internal.stream.buffer.MutableXMLStreamBuffer;
import com.sun.xml.internal.org.jvnet.staxex.XMLStreamWriterEx;

public class StreamWriterBufferCreator extends StreamBufferCreator implements XMLStreamWriterEx
{
    private final NamespaceContexHelper namespaceContext;
    private int depth;
    
    public StreamWriterBufferCreator() {
        this.namespaceContext = new NamespaceContexHelper();
        this.depth = 0;
        this.setXMLStreamBuffer(new MutableXMLStreamBuffer());
    }
    
    public StreamWriterBufferCreator(final MutableXMLStreamBuffer buffer) {
        this.namespaceContext = new NamespaceContexHelper();
        this.depth = 0;
        this.setXMLStreamBuffer(buffer);
    }
    
    @Override
    public Object getProperty(final String str) throws IllegalArgumentException {
        return null;
    }
    
    @Override
    public void close() throws XMLStreamException {
    }
    
    @Override
    public void flush() throws XMLStreamException {
    }
    
    @Override
    public NamespaceContextEx getNamespaceContext() {
        return this.namespaceContext;
    }
    
    @Override
    public void setNamespaceContext(final NamespaceContext namespaceContext) throws XMLStreamException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void setDefaultNamespace(final String namespaceURI) throws XMLStreamException {
        this.setPrefix("", namespaceURI);
    }
    
    @Override
    public void setPrefix(final String prefix, final String namespaceURI) throws XMLStreamException {
        this.namespaceContext.declareNamespace(prefix, namespaceURI);
    }
    
    @Override
    public String getPrefix(final String namespaceURI) throws XMLStreamException {
        return this.namespaceContext.getPrefix(namespaceURI);
    }
    
    @Override
    public void writeStartDocument() throws XMLStreamException {
        this.writeStartDocument("", "");
    }
    
    @Override
    public void writeStartDocument(final String version) throws XMLStreamException {
        this.writeStartDocument("", "");
    }
    
    @Override
    public void writeStartDocument(final String encoding, final String version) throws XMLStreamException {
        this.namespaceContext.resetContexts();
        this.storeStructure(16);
    }
    
    @Override
    public void writeEndDocument() throws XMLStreamException {
        this.storeStructure(144);
    }
    
    @Override
    public void writeStartElement(final String localName) throws XMLStreamException {
        this.namespaceContext.pushContext();
        ++this.depth;
        final String defaultNamespaceURI = this.namespaceContext.getNamespaceURI("");
        if (defaultNamespaceURI == null) {
            this.storeQualifiedName(32, null, null, localName);
        }
        else {
            this.storeQualifiedName(32, null, defaultNamespaceURI, localName);
        }
    }
    
    @Override
    public void writeStartElement(final String namespaceURI, final String localName) throws XMLStreamException {
        this.namespaceContext.pushContext();
        ++this.depth;
        final String prefix = this.namespaceContext.getPrefix(namespaceURI);
        if (prefix == null) {
            throw new XMLStreamException();
        }
        this.namespaceContext.pushContext();
        this.storeQualifiedName(32, prefix, namespaceURI, localName);
    }
    
    @Override
    public void writeStartElement(final String prefix, final String localName, final String namespaceURI) throws XMLStreamException {
        this.namespaceContext.pushContext();
        ++this.depth;
        this.storeQualifiedName(32, prefix, namespaceURI, localName);
    }
    
    @Override
    public void writeEmptyElement(final String localName) throws XMLStreamException {
        this.writeStartElement(localName);
        this.writeEndElement();
    }
    
    @Override
    public void writeEmptyElement(final String namespaceURI, final String localName) throws XMLStreamException {
        this.writeStartElement(namespaceURI, localName);
        this.writeEndElement();
    }
    
    @Override
    public void writeEmptyElement(final String prefix, final String localName, final String namespaceURI) throws XMLStreamException {
        this.writeStartElement(prefix, localName, namespaceURI);
        this.writeEndElement();
    }
    
    @Override
    public void writeEndElement() throws XMLStreamException {
        this.namespaceContext.popContext();
        this.storeStructure(144);
        final int depth = this.depth - 1;
        this.depth = depth;
        if (depth == 0) {
            this.increaseTreeCount();
        }
    }
    
    @Override
    public void writeDefaultNamespace(final String namespaceURI) throws XMLStreamException {
        this.storeNamespaceAttribute(null, namespaceURI);
    }
    
    @Override
    public void writeNamespace(String prefix, final String namespaceURI) throws XMLStreamException {
        if ("xmlns".equals(prefix)) {
            prefix = null;
        }
        this.storeNamespaceAttribute(prefix, namespaceURI);
    }
    
    @Override
    public void writeAttribute(final String localName, final String value) throws XMLStreamException {
        this.storeAttribute(null, null, localName, "CDATA", value);
    }
    
    @Override
    public void writeAttribute(final String namespaceURI, final String localName, final String value) throws XMLStreamException {
        final String prefix = this.namespaceContext.getPrefix(namespaceURI);
        if (prefix == null) {
            throw new XMLStreamException();
        }
        this.writeAttribute(prefix, namespaceURI, localName, value);
    }
    
    @Override
    public void writeAttribute(final String prefix, final String namespaceURI, final String localName, final String value) throws XMLStreamException {
        this.storeAttribute(prefix, namespaceURI, localName, "CDATA", value);
    }
    
    @Override
    public void writeCData(final String data) throws XMLStreamException {
        this.storeStructure(88);
        this.storeContentString(data);
    }
    
    @Override
    public void writeCharacters(final String charData) throws XMLStreamException {
        this.storeStructure(88);
        this.storeContentString(charData);
    }
    
    @Override
    public void writeCharacters(final char[] buf, final int start, final int len) throws XMLStreamException {
        this.storeContentCharacters(80, buf, start, len);
    }
    
    @Override
    public void writeComment(final String str) throws XMLStreamException {
        this.storeStructure(104);
        this.storeContentString(str);
    }
    
    @Override
    public void writeDTD(final String str) throws XMLStreamException {
    }
    
    @Override
    public void writeEntityRef(final String str) throws XMLStreamException {
        this.storeStructure(128);
        this.storeContentString(str);
    }
    
    @Override
    public void writeProcessingInstruction(final String target) throws XMLStreamException {
        this.writeProcessingInstruction(target, "");
    }
    
    @Override
    public void writeProcessingInstruction(final String target, final String data) throws XMLStreamException {
        this.storeProcessingInstruction(target, data);
    }
    
    @Override
    public void writePCDATA(final CharSequence charSequence) throws XMLStreamException {
        if (charSequence instanceof Base64Data) {
            this.storeStructure(92);
            this.storeContentObject(((Base64Data)charSequence).clone());
        }
        else {
            this.writeCharacters(charSequence.toString());
        }
    }
    
    @Override
    public void writeBinary(final byte[] bytes, final int offset, final int length, final String endpointURL) throws XMLStreamException {
        final Base64Data d = new Base64Data();
        final byte[] b = new byte[length];
        System.arraycopy(bytes, offset, b, 0, length);
        d.set(b, length, null, true);
        this.storeStructure(92);
        this.storeContentObject(d);
    }
    
    @Override
    public void writeBinary(final DataHandler dataHandler) throws XMLStreamException {
        final Base64Data d = new Base64Data();
        d.set(dataHandler);
        this.storeStructure(92);
        this.storeContentObject(d);
    }
    
    @Override
    public OutputStream writeBinary(final String endpointURL) throws XMLStreamException {
        throw new UnsupportedOperationException();
    }
}
