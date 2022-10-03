package com.sun.xml.internal.stream.buffer;

import java.io.IOException;
import java.io.InputStream;
import org.xml.sax.XMLReader;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMResult;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import org.xml.sax.ErrorHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.ContentHandler;
import com.sun.xml.internal.stream.buffer.sax.SAXBufferProcessor;
import com.sun.xml.internal.stream.buffer.stax.StreamWriterBufferProcessor;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.XMLStreamException;
import com.sun.xml.internal.stream.buffer.stax.StreamReaderBufferProcessor;
import java.util.Collections;
import javax.xml.transform.TransformerFactory;
import java.util.Map;

public abstract class XMLStreamBuffer
{
    protected Map<String, String> _inscopeNamespaces;
    protected boolean _hasInternedStrings;
    protected FragmentedArray<byte[]> _structure;
    protected int _structurePtr;
    protected FragmentedArray<String[]> _structureStrings;
    protected int _structureStringsPtr;
    protected FragmentedArray<char[]> _contentCharactersBuffer;
    protected int _contentCharactersBufferPtr;
    protected FragmentedArray<Object[]> _contentObjects;
    protected int _contentObjectsPtr;
    protected int treeCount;
    protected String systemId;
    private static final ContextClassloaderLocal<TransformerFactory> trnsformerFactory;
    
    public XMLStreamBuffer() {
        this._inscopeNamespaces = Collections.emptyMap();
    }
    
    public final boolean isCreated() {
        return this._structure.getArray()[0] != 144;
    }
    
    public final boolean isFragment() {
        return this.isCreated() && (this._structure.getArray()[this._structurePtr] & 0xF0) != 0x10;
    }
    
    public final boolean isElementFragment() {
        return this.isCreated() && (this._structure.getArray()[this._structurePtr] & 0xF0) == 0x20;
    }
    
    public final boolean isForest() {
        return this.isCreated() && this.treeCount > 1;
    }
    
    public final String getSystemId() {
        return this.systemId;
    }
    
    public final Map<String, String> getInscopeNamespaces() {
        return this._inscopeNamespaces;
    }
    
    public final boolean hasInternedStrings() {
        return this._hasInternedStrings;
    }
    
    public final StreamReaderBufferProcessor readAsXMLStreamReader() throws XMLStreamException {
        return new StreamReaderBufferProcessor(this);
    }
    
    public final void writeToXMLStreamWriter(final XMLStreamWriter writer, final boolean writeAsFragment) throws XMLStreamException {
        final StreamWriterBufferProcessor p = new StreamWriterBufferProcessor(this, writeAsFragment);
        p.process(writer);
    }
    
    @Deprecated
    public final void writeToXMLStreamWriter(final XMLStreamWriter writer) throws XMLStreamException {
        this.writeToXMLStreamWriter(writer, this.isFragment());
    }
    
    @Deprecated
    public final SAXBufferProcessor readAsXMLReader() {
        return new SAXBufferProcessor(this, this.isFragment());
    }
    
    public final SAXBufferProcessor readAsXMLReader(final boolean produceFragmentEvent) {
        return new SAXBufferProcessor(this, produceFragmentEvent);
    }
    
    public final void writeTo(final ContentHandler handler, final boolean produceFragmentEvent) throws SAXException {
        final SAXBufferProcessor p = this.readAsXMLReader(produceFragmentEvent);
        p.setContentHandler(handler);
        if (p instanceof LexicalHandler) {
            p.setLexicalHandler((LexicalHandler)handler);
        }
        if (p instanceof DTDHandler) {
            p.setDTDHandler((DTDHandler)handler);
        }
        if (p instanceof ErrorHandler) {
            p.setErrorHandler((ErrorHandler)handler);
        }
        p.process();
    }
    
    @Deprecated
    public final void writeTo(final ContentHandler handler) throws SAXException {
        this.writeTo(handler, this.isFragment());
    }
    
    public final void writeTo(final ContentHandler handler, final ErrorHandler errorHandler, final boolean produceFragmentEvent) throws SAXException {
        final SAXBufferProcessor p = this.readAsXMLReader(produceFragmentEvent);
        p.setContentHandler(handler);
        if (p instanceof LexicalHandler) {
            p.setLexicalHandler((LexicalHandler)handler);
        }
        if (p instanceof DTDHandler) {
            p.setDTDHandler((DTDHandler)handler);
        }
        p.setErrorHandler(errorHandler);
        p.process();
    }
    
    public final void writeTo(final ContentHandler handler, final ErrorHandler errorHandler) throws SAXException {
        this.writeTo(handler, errorHandler, this.isFragment());
    }
    
    public final Node writeTo(final Node n) throws XMLStreamBufferException {
        try {
            final Transformer t = XMLStreamBuffer.trnsformerFactory.get().newTransformer();
            t.transform(new XMLStreamBufferSource(this), new DOMResult(n));
            return n.getLastChild();
        }
        catch (final TransformerException e) {
            throw new XMLStreamBufferException(e);
        }
    }
    
    public static XMLStreamBuffer createNewBufferFromXMLStreamReader(final XMLStreamReader reader) throws XMLStreamException {
        final MutableXMLStreamBuffer b = new MutableXMLStreamBuffer();
        b.createFromXMLStreamReader(reader);
        return b;
    }
    
    public static XMLStreamBuffer createNewBufferFromXMLReader(final XMLReader reader, final InputStream in) throws SAXException, IOException {
        final MutableXMLStreamBuffer b = new MutableXMLStreamBuffer();
        b.createFromXMLReader(reader, in);
        return b;
    }
    
    public static XMLStreamBuffer createNewBufferFromXMLReader(final XMLReader reader, final InputStream in, final String systemId) throws SAXException, IOException {
        final MutableXMLStreamBuffer b = new MutableXMLStreamBuffer();
        b.createFromXMLReader(reader, in, systemId);
        return b;
    }
    
    protected final FragmentedArray<byte[]> getStructure() {
        return this._structure;
    }
    
    protected final int getStructurePtr() {
        return this._structurePtr;
    }
    
    protected final FragmentedArray<String[]> getStructureStrings() {
        return this._structureStrings;
    }
    
    protected final int getStructureStringsPtr() {
        return this._structureStringsPtr;
    }
    
    protected final FragmentedArray<char[]> getContentCharactersBuffer() {
        return this._contentCharactersBuffer;
    }
    
    protected final int getContentCharactersBufferPtr() {
        return this._contentCharactersBufferPtr;
    }
    
    protected final FragmentedArray<Object[]> getContentObjects() {
        return this._contentObjects;
    }
    
    protected final int getContentObjectsPtr() {
        return this._contentObjectsPtr;
    }
    
    static {
        trnsformerFactory = new ContextClassloaderLocal<TransformerFactory>() {
            @Override
            protected TransformerFactory initialValue() throws Exception {
                return TransformerFactory.newInstance();
            }
        };
    }
}
