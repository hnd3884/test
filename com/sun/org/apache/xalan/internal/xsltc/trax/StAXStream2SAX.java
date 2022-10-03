package com.sun.org.apache.xalan.internal.xsltc.trax;

import org.xml.sax.EntityResolver;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.ErrorHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.Attributes;
import javax.xml.namespace.QName;
import org.xml.sax.ext.Locator2;
import java.io.IOException;
import javax.xml.stream.XMLStreamException;
import org.xml.sax.SAXException;
import org.xml.sax.InputSource;
import com.sun.org.apache.xalan.internal.xsltc.dom.SAXImpl;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.ContentHandler;
import javax.xml.stream.XMLStreamReader;
import org.xml.sax.Locator;
import org.xml.sax.XMLReader;

public class StAXStream2SAX implements XMLReader, Locator
{
    private final XMLStreamReader staxStreamReader;
    private ContentHandler _sax;
    private LexicalHandler _lex;
    private SAXImpl _saxImpl;
    
    public StAXStream2SAX(final XMLStreamReader staxSrc) {
        this._sax = null;
        this._lex = null;
        this._saxImpl = null;
        this.staxStreamReader = staxSrc;
    }
    
    @Override
    public ContentHandler getContentHandler() {
        return this._sax;
    }
    
    @Override
    public void setContentHandler(final ContentHandler handler) throws NullPointerException {
        this._sax = handler;
        if (handler instanceof LexicalHandler) {
            this._lex = (LexicalHandler)handler;
        }
        if (handler instanceof SAXImpl) {
            this._saxImpl = (SAXImpl)handler;
        }
    }
    
    @Override
    public void parse(final InputSource unused) throws IOException, SAXException {
        try {
            this.bridge();
        }
        catch (final XMLStreamException e) {
            throw new SAXException(e);
        }
    }
    
    public void parse() throws IOException, SAXException, XMLStreamException {
        this.bridge();
    }
    
    @Override
    public void parse(final String sysId) throws IOException, SAXException {
        throw new IOException("This method is not yet implemented.");
    }
    
    public void bridge() throws XMLStreamException {
        try {
            int depth = 0;
            int event = this.staxStreamReader.getEventType();
            if (event == 7) {
                event = this.staxStreamReader.next();
            }
            if (event != 1) {
                event = this.staxStreamReader.nextTag();
                if (event != 1) {
                    throw new IllegalStateException("The current event is not START_ELEMENT\n but" + event);
                }
            }
            this.handleStartDocument();
            do {
                switch (event) {
                    case 1: {
                        ++depth;
                        this.handleStartElement();
                        break;
                    }
                    case 2: {
                        this.handleEndElement();
                        --depth;
                        break;
                    }
                    case 4: {
                        this.handleCharacters();
                        break;
                    }
                    case 9: {
                        this.handleEntityReference();
                        break;
                    }
                    case 3: {
                        this.handlePI();
                        break;
                    }
                    case 5: {
                        this.handleComment();
                        break;
                    }
                    case 11: {
                        this.handleDTD();
                        break;
                    }
                    case 10: {
                        this.handleAttribute();
                        break;
                    }
                    case 13: {
                        this.handleNamespace();
                        break;
                    }
                    case 12: {
                        this.handleCDATA();
                        break;
                    }
                    case 15: {
                        this.handleEntityDecl();
                        break;
                    }
                    case 14: {
                        this.handleNotationDecl();
                        break;
                    }
                    case 6: {
                        this.handleSpace();
                        break;
                    }
                    default: {
                        throw new InternalError("processing event: " + event);
                    }
                }
                event = this.staxStreamReader.next();
            } while (depth != 0);
            this.handleEndDocument();
        }
        catch (final SAXException e) {
            throw new XMLStreamException(e);
        }
    }
    
    private void handleEndDocument() throws SAXException {
        this._sax.endDocument();
    }
    
    private void handleStartDocument() throws SAXException {
        this._sax.setDocumentLocator(new Locator2() {
            @Override
            public int getColumnNumber() {
                return StAXStream2SAX.this.staxStreamReader.getLocation().getColumnNumber();
            }
            
            @Override
            public int getLineNumber() {
                return StAXStream2SAX.this.staxStreamReader.getLocation().getLineNumber();
            }
            
            @Override
            public String getPublicId() {
                return StAXStream2SAX.this.staxStreamReader.getLocation().getPublicId();
            }
            
            @Override
            public String getSystemId() {
                return StAXStream2SAX.this.staxStreamReader.getLocation().getSystemId();
            }
            
            @Override
            public String getXMLVersion() {
                return StAXStream2SAX.this.staxStreamReader.getVersion();
            }
            
            @Override
            public String getEncoding() {
                return StAXStream2SAX.this.staxStreamReader.getEncoding();
            }
        });
        this._sax.startDocument();
    }
    
    private void handlePI() throws XMLStreamException {
        try {
            this._sax.processingInstruction(this.staxStreamReader.getPITarget(), this.staxStreamReader.getPIData());
        }
        catch (final SAXException e) {
            throw new XMLStreamException(e);
        }
    }
    
    private void handleCharacters() throws XMLStreamException {
        final int textLength = this.staxStreamReader.getTextLength();
        final char[] chars = new char[textLength];
        this.staxStreamReader.getTextCharacters(0, chars, 0, textLength);
        try {
            this._sax.characters(chars, 0, chars.length);
        }
        catch (final SAXException e) {
            throw new XMLStreamException(e);
        }
    }
    
    private void handleEndElement() throws XMLStreamException {
        final QName qName = this.staxStreamReader.getName();
        try {
            String qname = "";
            if (qName.getPrefix() != null && qName.getPrefix().trim().length() != 0) {
                qname = qName.getPrefix() + ":";
            }
            qname += qName.getLocalPart();
            this._sax.endElement(qName.getNamespaceURI(), qName.getLocalPart(), qname);
            final int nsCount = this.staxStreamReader.getNamespaceCount();
            for (int i = nsCount - 1; i >= 0; --i) {
                String prefix = this.staxStreamReader.getNamespacePrefix(i);
                if (prefix == null) {
                    prefix = "";
                }
                this._sax.endPrefixMapping(prefix);
            }
        }
        catch (final SAXException e) {
            throw new XMLStreamException(e);
        }
    }
    
    private void handleStartElement() throws XMLStreamException {
        try {
            for (int nsCount = this.staxStreamReader.getNamespaceCount(), i = 0; i < nsCount; ++i) {
                String prefix = this.staxStreamReader.getNamespacePrefix(i);
                if (prefix == null) {
                    prefix = "";
                }
                this._sax.startPrefixMapping(prefix, this.staxStreamReader.getNamespaceURI(i));
            }
            final QName qName = this.staxStreamReader.getName();
            String prefix = qName.getPrefix();
            String rawname;
            if (prefix == null || prefix.length() == 0) {
                rawname = qName.getLocalPart();
            }
            else {
                rawname = prefix + ':' + qName.getLocalPart();
            }
            final Attributes attrs = this.getAttributes();
            this._sax.startElement(qName.getNamespaceURI(), qName.getLocalPart(), rawname, attrs);
        }
        catch (final SAXException e) {
            throw new XMLStreamException(e);
        }
    }
    
    private Attributes getAttributes() {
        final AttributesImpl attrs = new AttributesImpl();
        final int eventType = this.staxStreamReader.getEventType();
        if (eventType != 10 && eventType != 1) {
            throw new InternalError("getAttributes() attempting to process: " + eventType);
        }
        for (int i = 0; i < this.staxStreamReader.getAttributeCount(); ++i) {
            String uri = this.staxStreamReader.getAttributeNamespace(i);
            if (uri == null) {
                uri = "";
            }
            final String localName = this.staxStreamReader.getAttributeLocalName(i);
            final String prefix = this.staxStreamReader.getAttributePrefix(i);
            String qName;
            if (prefix == null || prefix.length() == 0) {
                qName = localName;
            }
            else {
                qName = prefix + ':' + localName;
            }
            final String type = this.staxStreamReader.getAttributeType(i);
            final String value = this.staxStreamReader.getAttributeValue(i);
            attrs.addAttribute(uri, localName, qName, type, value);
        }
        return attrs;
    }
    
    private void handleNamespace() {
    }
    
    private void handleAttribute() {
    }
    
    private void handleDTD() {
    }
    
    private void handleComment() {
    }
    
    private void handleEntityReference() {
    }
    
    private void handleSpace() {
    }
    
    private void handleNotationDecl() {
    }
    
    private void handleEntityDecl() {
    }
    
    private void handleCDATA() {
    }
    
    @Override
    public DTDHandler getDTDHandler() {
        return null;
    }
    
    @Override
    public ErrorHandler getErrorHandler() {
        return null;
    }
    
    @Override
    public boolean getFeature(final String name) throws SAXNotRecognizedException, SAXNotSupportedException {
        return false;
    }
    
    @Override
    public void setFeature(final String name, final boolean value) throws SAXNotRecognizedException, SAXNotSupportedException {
    }
    
    @Override
    public void setDTDHandler(final DTDHandler handler) throws NullPointerException {
    }
    
    @Override
    public void setEntityResolver(final EntityResolver resolver) throws NullPointerException {
    }
    
    @Override
    public EntityResolver getEntityResolver() {
        return null;
    }
    
    @Override
    public void setErrorHandler(final ErrorHandler handler) throws NullPointerException {
    }
    
    @Override
    public void setProperty(final String name, final Object value) throws SAXNotRecognizedException, SAXNotSupportedException {
    }
    
    @Override
    public Object getProperty(final String name) throws SAXNotRecognizedException, SAXNotSupportedException {
        return null;
    }
    
    @Override
    public int getColumnNumber() {
        return 0;
    }
    
    @Override
    public int getLineNumber() {
        return 0;
    }
    
    @Override
    public String getPublicId() {
        return null;
    }
    
    @Override
    public String getSystemId() {
        return null;
    }
}
