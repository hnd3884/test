package com.sun.org.apache.xalan.internal.xsltc.trax;

import org.xml.sax.EntityResolver;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.ErrorHandler;
import org.xml.sax.DTDHandler;
import javax.xml.stream.events.Attribute;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.Attributes;
import javax.xml.stream.events.Namespace;
import javax.xml.stream.events.StartElement;
import java.util.Iterator;
import javax.xml.namespace.QName;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.Characters;
import org.xml.sax.ext.Locator2;
import javax.xml.stream.events.XMLEvent;
import javax.xml.stream.events.ProcessingInstruction;
import javax.xml.stream.events.StartDocument;
import java.io.IOException;
import javax.xml.stream.XMLStreamException;
import org.xml.sax.SAXException;
import org.xml.sax.InputSource;
import com.sun.org.apache.xalan.internal.xsltc.dom.SAXImpl;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.ContentHandler;
import javax.xml.stream.XMLEventReader;
import org.xml.sax.Locator;
import org.xml.sax.XMLReader;

public class StAXEvent2SAX implements XMLReader, Locator
{
    private final XMLEventReader staxEventReader;
    private ContentHandler _sax;
    private LexicalHandler _lex;
    private SAXImpl _saxImpl;
    private String version;
    private String encoding;
    
    public StAXEvent2SAX(final XMLEventReader staxCore) {
        this._sax = null;
        this._lex = null;
        this._saxImpl = null;
        this.version = null;
        this.encoding = null;
        this.staxEventReader = staxCore;
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
    
    private void bridge() throws XMLStreamException {
        try {
            int depth = 0;
            boolean startedAtDocument = false;
            XMLEvent event = this.staxEventReader.peek();
            if (!event.isStartDocument() && !event.isStartElement()) {
                throw new IllegalStateException();
            }
            if (event.getEventType() == 7) {
                startedAtDocument = true;
                this.version = ((StartDocument)event).getVersion();
                if (((StartDocument)event).encodingSet()) {
                    this.encoding = ((StartDocument)event).getCharacterEncodingScheme();
                }
                event = this.staxEventReader.nextEvent();
                event = this.staxEventReader.nextEvent();
            }
            this.handleStartDocument(event);
            while (event.getEventType() != 1) {
                switch (event.getEventType()) {
                    case 4: {
                        this.handleCharacters(event.asCharacters());
                        break;
                    }
                    case 3: {
                        this.handlePI((ProcessingInstruction)event);
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
                    case 6: {
                        this.handleSpace();
                        break;
                    }
                    default: {
                        throw new InternalError("processing prolog event: " + event);
                    }
                }
                event = this.staxEventReader.nextEvent();
            }
            do {
                switch (event.getEventType()) {
                    case 1: {
                        ++depth;
                        this.handleStartElement(event.asStartElement());
                        break;
                    }
                    case 2: {
                        this.handleEndElement(event.asEndElement());
                        --depth;
                        break;
                    }
                    case 4: {
                        this.handleCharacters(event.asCharacters());
                        break;
                    }
                    case 9: {
                        this.handleEntityReference();
                        break;
                    }
                    case 3: {
                        this.handlePI((ProcessingInstruction)event);
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
                event = this.staxEventReader.nextEvent();
            } while (depth != 0);
            if (startedAtDocument) {
                while (event.getEventType() != 8) {
                    switch (event.getEventType()) {
                        case 4: {
                            this.handleCharacters(event.asCharacters());
                            break;
                        }
                        case 3: {
                            this.handlePI((ProcessingInstruction)event);
                            break;
                        }
                        case 5: {
                            this.handleComment();
                            break;
                        }
                        case 6: {
                            this.handleSpace();
                            break;
                        }
                        default: {
                            throw new InternalError("processing misc event after document element: " + event);
                        }
                    }
                    event = this.staxEventReader.nextEvent();
                }
            }
            this.handleEndDocument();
        }
        catch (final SAXException e) {
            throw new XMLStreamException(e);
        }
    }
    
    private void handleEndDocument() throws SAXException {
        this._sax.endDocument();
    }
    
    private void handleStartDocument(final XMLEvent event) throws SAXException {
        this._sax.setDocumentLocator(new Locator2() {
            @Override
            public int getColumnNumber() {
                return event.getLocation().getColumnNumber();
            }
            
            @Override
            public int getLineNumber() {
                return event.getLocation().getLineNumber();
            }
            
            @Override
            public String getPublicId() {
                return event.getLocation().getPublicId();
            }
            
            @Override
            public String getSystemId() {
                return event.getLocation().getSystemId();
            }
            
            @Override
            public String getXMLVersion() {
                return StAXEvent2SAX.this.version;
            }
            
            @Override
            public String getEncoding() {
                return StAXEvent2SAX.this.encoding;
            }
        });
        this._sax.startDocument();
    }
    
    private void handlePI(final ProcessingInstruction event) throws XMLStreamException {
        try {
            this._sax.processingInstruction(event.getTarget(), event.getData());
        }
        catch (final SAXException e) {
            throw new XMLStreamException(e);
        }
    }
    
    private void handleCharacters(final Characters event) throws XMLStreamException {
        try {
            this._sax.characters(event.getData().toCharArray(), 0, event.getData().length());
        }
        catch (final SAXException e) {
            throw new XMLStreamException(e);
        }
    }
    
    private void handleEndElement(final EndElement event) throws XMLStreamException {
        final QName qName = event.getName();
        String qname = "";
        if (qName.getPrefix() != null && qName.getPrefix().trim().length() != 0) {
            qname = qName.getPrefix() + ":";
        }
        qname += qName.getLocalPart();
        try {
            this._sax.endElement(qName.getNamespaceURI(), qName.getLocalPart(), qname);
            final Iterator i = event.getNamespaces();
            while (i.hasNext()) {
                String prefix = i.next();
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
    
    private void handleStartElement(final StartElement event) throws XMLStreamException {
        try {
            final Iterator i = event.getNamespaces();
            while (i.hasNext()) {
                String prefix = i.next().getPrefix();
                if (prefix == null) {
                    prefix = "";
                }
                this._sax.startPrefixMapping(prefix, event.getNamespaceURI(prefix));
            }
            final QName qName = event.getName();
            String prefix = qName.getPrefix();
            String rawname;
            if (prefix == null || prefix.length() == 0) {
                rawname = qName.getLocalPart();
            }
            else {
                rawname = prefix + ':' + qName.getLocalPart();
            }
            final Attributes saxAttrs = this.getAttributes(event);
            this._sax.startElement(qName.getNamespaceURI(), qName.getLocalPart(), rawname, saxAttrs);
        }
        catch (final SAXException e) {
            throw new XMLStreamException(e);
        }
    }
    
    private Attributes getAttributes(final StartElement event) {
        final AttributesImpl attrs = new AttributesImpl();
        if (!event.isStartElement()) {
            throw new InternalError("getAttributes() attempting to process: " + event);
        }
        final Iterator i = event.getAttributes();
        while (i.hasNext()) {
            final Attribute staxAttr = i.next();
            String uri = staxAttr.getName().getNamespaceURI();
            if (uri == null) {
                uri = "";
            }
            final String localName = staxAttr.getName().getLocalPart();
            final String prefix = staxAttr.getName().getPrefix();
            String qName;
            if (prefix == null || prefix.length() == 0) {
                qName = localName;
            }
            else {
                qName = prefix + ':' + localName;
            }
            final String type = staxAttr.getDTDType();
            final String value = staxAttr.getValue();
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
    public void parse(final String sysId) throws IOException, SAXException {
        throw new IOException("This method is not yet implemented.");
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
