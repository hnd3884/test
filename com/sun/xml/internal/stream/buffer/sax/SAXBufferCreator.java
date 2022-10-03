package com.sun.xml.internal.stream.buffer.sax;

import org.xml.sax.SAXParseException;
import org.xml.sax.Locator;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import java.io.IOException;
import java.io.InputStream;
import org.xml.sax.XMLReader;
import com.sun.xml.internal.stream.buffer.MutableXMLStreamBuffer;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import com.sun.xml.internal.stream.buffer.AbstractCreator;

public class SAXBufferCreator extends AbstractCreator implements EntityResolver, DTDHandler, ContentHandler, ErrorHandler, LexicalHandler
{
    protected String[] _namespaceAttributes;
    protected int _namespaceAttributesPtr;
    private int depth;
    
    public SAXBufferCreator() {
        this.depth = 0;
        this._namespaceAttributes = new String[32];
    }
    
    public SAXBufferCreator(final MutableXMLStreamBuffer buffer) {
        this();
        this.setBuffer(buffer);
    }
    
    public MutableXMLStreamBuffer create(final XMLReader reader, final InputStream in) throws IOException, SAXException {
        return this.create(reader, in, null);
    }
    
    public MutableXMLStreamBuffer create(final XMLReader reader, final InputStream in, final String systemId) throws IOException, SAXException {
        if (this._buffer == null) {
            this.createBuffer();
        }
        this._buffer.setSystemId(systemId);
        reader.setContentHandler(this);
        reader.setProperty("http://xml.org/sax/properties/lexical-handler", this);
        try {
            this.setHasInternedStrings(reader.getFeature("http://xml.org/sax/features/string-interning"));
        }
        catch (final SAXException ex) {}
        if (systemId != null) {
            final InputSource s = new InputSource(systemId);
            s.setByteStream(in);
            reader.parse(s);
        }
        else {
            reader.parse(new InputSource(in));
        }
        return this.getXMLStreamBuffer();
    }
    
    public void reset() {
        this._buffer = null;
        this._namespaceAttributesPtr = 0;
        this.depth = 0;
    }
    
    @Override
    public void startDocument() throws SAXException {
        this.storeStructure(16);
    }
    
    @Override
    public void endDocument() throws SAXException {
        this.storeStructure(144);
    }
    
    @Override
    public void startPrefixMapping(final String prefix, final String uri) throws SAXException {
        this.cacheNamespaceAttribute(prefix, uri);
    }
    
    @Override
    public void startElement(final String uri, final String localName, final String qName, final Attributes attributes) throws SAXException {
        this.storeQualifiedName(32, uri, localName, qName);
        if (this._namespaceAttributesPtr > 0) {
            this.storeNamespaceAttributes();
        }
        if (attributes.getLength() > 0) {
            this.storeAttributes(attributes);
        }
        ++this.depth;
    }
    
    @Override
    public void endElement(final String uri, final String localName, final String qName) throws SAXException {
        this.storeStructure(144);
        final int depth = this.depth - 1;
        this.depth = depth;
        if (depth == 0) {
            this.increaseTreeCount();
        }
    }
    
    @Override
    public void characters(final char[] ch, final int start, final int length) throws SAXException {
        this.storeContentCharacters(80, ch, start, length);
    }
    
    @Override
    public void ignorableWhitespace(final char[] ch, final int start, final int length) throws SAXException {
        this.characters(ch, start, length);
    }
    
    @Override
    public void processingInstruction(final String target, final String data) throws SAXException {
        this.storeStructure(112);
        this.storeStructureString(target);
        this.storeStructureString(data);
    }
    
    @Override
    public void comment(final char[] ch, final int start, final int length) throws SAXException {
        this.storeContentCharacters(96, ch, start, length);
    }
    
    private void cacheNamespaceAttribute(final String prefix, final String uri) {
        this._namespaceAttributes[this._namespaceAttributesPtr++] = prefix;
        this._namespaceAttributes[this._namespaceAttributesPtr++] = uri;
        if (this._namespaceAttributesPtr == this._namespaceAttributes.length) {
            final String[] namespaceAttributes = new String[this._namespaceAttributesPtr * 2];
            System.arraycopy(this._namespaceAttributes, 0, namespaceAttributes, 0, this._namespaceAttributesPtr);
            this._namespaceAttributes = namespaceAttributes;
        }
    }
    
    private void storeNamespaceAttributes() {
        for (int i = 0; i < this._namespaceAttributesPtr; i += 2) {
            int item = 64;
            if (this._namespaceAttributes[i].length() > 0) {
                item |= 0x1;
                this.storeStructureString(this._namespaceAttributes[i]);
            }
            if (this._namespaceAttributes[i + 1].length() > 0) {
                item |= 0x2;
                this.storeStructureString(this._namespaceAttributes[i + 1]);
            }
            this.storeStructure(item);
        }
        this._namespaceAttributesPtr = 0;
    }
    
    private void storeAttributes(final Attributes attributes) {
        for (int i = 0; i < attributes.getLength(); ++i) {
            if (!attributes.getQName(i).startsWith("xmlns")) {
                this.storeQualifiedName(48, attributes.getURI(i), attributes.getLocalName(i), attributes.getQName(i));
                this.storeStructureString(attributes.getType(i));
                this.storeContentString(attributes.getValue(i));
            }
        }
    }
    
    private void storeQualifiedName(int item, final String uri, final String localName, final String qName) {
        if (uri.length() > 0) {
            item |= 0x2;
            this.storeStructureString(uri);
        }
        this.storeStructureString(localName);
        if (qName.indexOf(58) >= 0) {
            item |= 0x4;
            this.storeStructureString(qName);
        }
        this.storeStructure(item);
    }
    
    @Override
    public InputSource resolveEntity(final String publicId, final String systemId) throws IOException, SAXException {
        return null;
    }
    
    @Override
    public void notationDecl(final String name, final String publicId, final String systemId) throws SAXException {
    }
    
    @Override
    public void unparsedEntityDecl(final String name, final String publicId, final String systemId, final String notationName) throws SAXException {
    }
    
    @Override
    public void setDocumentLocator(final Locator locator) {
    }
    
    @Override
    public void endPrefixMapping(final String prefix) throws SAXException {
    }
    
    @Override
    public void skippedEntity(final String name) throws SAXException {
    }
    
    @Override
    public void startDTD(final String name, final String publicId, final String systemId) throws SAXException {
    }
    
    @Override
    public void endDTD() throws SAXException {
    }
    
    @Override
    public void startEntity(final String name) throws SAXException {
    }
    
    @Override
    public void endEntity(final String name) throws SAXException {
    }
    
    @Override
    public void startCDATA() throws SAXException {
    }
    
    @Override
    public void endCDATA() throws SAXException {
    }
    
    @Override
    public void warning(final SAXParseException e) throws SAXException {
    }
    
    @Override
    public void error(final SAXParseException e) throws SAXException {
    }
    
    @Override
    public void fatalError(final SAXParseException e) throws SAXException {
        throw e;
    }
}
