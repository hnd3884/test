package com.sun.istack.internal;

import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.Attributes;
import javax.xml.namespace.QName;
import org.xml.sax.Locator;
import javax.xml.stream.XMLStreamException;
import org.xml.sax.SAXException;
import org.xml.sax.ContentHandler;
import javax.xml.stream.XMLStreamReader;

public class XMLStreamReaderToContentHandler
{
    private final XMLStreamReader staxStreamReader;
    private final ContentHandler saxHandler;
    private final boolean eagerQuit;
    private final boolean fragment;
    private final String[] inscopeNamespaces;
    
    public XMLStreamReaderToContentHandler(final XMLStreamReader staxCore, final ContentHandler saxCore, final boolean eagerQuit, final boolean fragment) {
        this(staxCore, saxCore, eagerQuit, fragment, new String[0]);
    }
    
    public XMLStreamReaderToContentHandler(final XMLStreamReader staxCore, final ContentHandler saxCore, final boolean eagerQuit, final boolean fragment, final String[] inscopeNamespaces) {
        this.staxStreamReader = staxCore;
        this.saxHandler = saxCore;
        this.eagerQuit = eagerQuit;
        this.fragment = fragment;
        this.inscopeNamespaces = inscopeNamespaces;
        assert inscopeNamespaces.length % 2 == 0;
    }
    
    public void bridge() throws XMLStreamException {
        try {
            int depth = 0;
            int event = this.staxStreamReader.getEventType();
            if (event == 7) {
                while (!this.staxStreamReader.isStartElement()) {
                    event = this.staxStreamReader.next();
                }
            }
            if (event != 1) {
                throw new IllegalStateException("The current event is not START_ELEMENT\n but " + event);
            }
            this.handleStartDocument();
            for (int i = 0; i < this.inscopeNamespaces.length; i += 2) {
                this.saxHandler.startPrefixMapping(this.inscopeNamespaces[i], this.inscopeNamespaces[i + 1]);
            }
        Label_0345:
            do {
                switch (event) {
                    case 1: {
                        ++depth;
                        this.handleStartElement();
                        break;
                    }
                    case 2: {
                        this.handleEndElement();
                        if (--depth == 0 && this.eagerQuit) {
                            break Label_0345;
                        }
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
            for (int i = 0; i < this.inscopeNamespaces.length; i += 2) {
                this.saxHandler.endPrefixMapping(this.inscopeNamespaces[i]);
            }
            this.handleEndDocument();
        }
        catch (final SAXException e) {
            throw new XMLStreamException2(e);
        }
    }
    
    private void handleEndDocument() throws SAXException {
        if (this.fragment) {
            return;
        }
        this.saxHandler.endDocument();
    }
    
    private void handleStartDocument() throws SAXException {
        if (this.fragment) {
            return;
        }
        this.saxHandler.setDocumentLocator(new Locator() {
            @Override
            public int getColumnNumber() {
                return XMLStreamReaderToContentHandler.this.staxStreamReader.getLocation().getColumnNumber();
            }
            
            @Override
            public int getLineNumber() {
                return XMLStreamReaderToContentHandler.this.staxStreamReader.getLocation().getLineNumber();
            }
            
            @Override
            public String getPublicId() {
                return XMLStreamReaderToContentHandler.this.staxStreamReader.getLocation().getPublicId();
            }
            
            @Override
            public String getSystemId() {
                return XMLStreamReaderToContentHandler.this.staxStreamReader.getLocation().getSystemId();
            }
        });
        this.saxHandler.startDocument();
    }
    
    private void handlePI() throws XMLStreamException {
        try {
            this.saxHandler.processingInstruction(this.staxStreamReader.getPITarget(), this.staxStreamReader.getPIData());
        }
        catch (final SAXException e) {
            throw new XMLStreamException2(e);
        }
    }
    
    private void handleCharacters() throws XMLStreamException {
        try {
            this.saxHandler.characters(this.staxStreamReader.getTextCharacters(), this.staxStreamReader.getTextStart(), this.staxStreamReader.getTextLength());
        }
        catch (final SAXException e) {
            throw new XMLStreamException2(e);
        }
    }
    
    private void handleEndElement() throws XMLStreamException {
        final QName qName = this.staxStreamReader.getName();
        try {
            final String pfix = qName.getPrefix();
            final String rawname = (pfix == null || pfix.length() == 0) ? qName.getLocalPart() : (pfix + ':' + qName.getLocalPart());
            this.saxHandler.endElement(qName.getNamespaceURI(), qName.getLocalPart(), rawname);
            final int nsCount = this.staxStreamReader.getNamespaceCount();
            for (int i = nsCount - 1; i >= 0; --i) {
                String prefix = this.staxStreamReader.getNamespacePrefix(i);
                if (prefix == null) {
                    prefix = "";
                }
                this.saxHandler.endPrefixMapping(prefix);
            }
        }
        catch (final SAXException e) {
            throw new XMLStreamException2(e);
        }
    }
    
    private void handleStartElement() throws XMLStreamException {
        try {
            for (int nsCount = this.staxStreamReader.getNamespaceCount(), i = 0; i < nsCount; ++i) {
                this.saxHandler.startPrefixMapping(fixNull(this.staxStreamReader.getNamespacePrefix(i)), fixNull(this.staxStreamReader.getNamespaceURI(i)));
            }
            final QName qName = this.staxStreamReader.getName();
            final String prefix = qName.getPrefix();
            String rawname;
            if (prefix == null || prefix.length() == 0) {
                rawname = qName.getLocalPart();
            }
            else {
                rawname = prefix + ':' + qName.getLocalPart();
            }
            final Attributes attrs = this.getAttributes();
            this.saxHandler.startElement(qName.getNamespaceURI(), qName.getLocalPart(), rawname, attrs);
        }
        catch (final SAXException e) {
            throw new XMLStreamException2(e);
        }
    }
    
    private static String fixNull(final String s) {
        if (s == null) {
            return "";
        }
        return s;
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
}
