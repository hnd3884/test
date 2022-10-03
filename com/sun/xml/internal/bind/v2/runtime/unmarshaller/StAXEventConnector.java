package com.sun.xml.internal.bind.v2.runtime.unmarshaller;

import javax.xml.stream.events.Attribute;
import org.xml.sax.Attributes;
import javax.xml.stream.events.StartElement;
import java.util.Iterator;
import javax.xml.stream.events.Namespace;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.Characters;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import org.xml.sax.SAXException;
import javax.xml.stream.XMLStreamException;
import org.xml.sax.helpers.AttributesImpl;
import javax.xml.stream.events.XMLEvent;
import javax.xml.stream.XMLEventReader;

final class StAXEventConnector extends StAXConnector
{
    private final XMLEventReader staxEventReader;
    private XMLEvent event;
    private final AttributesImpl attrs;
    private final StringBuilder buffer;
    private boolean seenText;
    
    public StAXEventConnector(final XMLEventReader staxCore, final XmlVisitor visitor) {
        super(visitor);
        this.attrs = new AttributesImpl();
        this.buffer = new StringBuilder();
        this.staxEventReader = staxCore;
    }
    
    @Override
    public void bridge() throws XMLStreamException {
        try {
            int depth = 0;
            this.event = this.staxEventReader.peek();
            if (!this.event.isStartDocument() && !this.event.isStartElement()) {
                throw new IllegalStateException();
            }
            do {
                this.event = this.staxEventReader.nextEvent();
            } while (!this.event.isStartElement());
            this.handleStartDocument(this.event.asStartElement().getNamespaceContext());
        Block_5:
            while (true) {
                switch (this.event.getEventType()) {
                    case 1: {
                        this.handleStartElement(this.event.asStartElement());
                        ++depth;
                        break;
                    }
                    case 2: {
                        --depth;
                        this.handleEndElement(this.event.asEndElement());
                        if (depth == 0) {
                            break Block_5;
                        }
                        break;
                    }
                    case 4:
                    case 6:
                    case 12: {
                        this.handleCharacters(this.event.asCharacters());
                        break;
                    }
                }
                this.event = this.staxEventReader.nextEvent();
            }
            this.handleEndDocument();
            this.event = null;
        }
        catch (final SAXException e) {
            throw new XMLStreamException(e);
        }
    }
    
    @Override
    protected Location getCurrentLocation() {
        return this.event.getLocation();
    }
    
    @Override
    protected String getCurrentQName() {
        QName qName;
        if (this.event.isEndElement()) {
            qName = this.event.asEndElement().getName();
        }
        else {
            qName = this.event.asStartElement().getName();
        }
        return this.getQName(qName.getPrefix(), qName.getLocalPart());
    }
    
    private void handleCharacters(final Characters event) throws SAXException, XMLStreamException {
        if (!this.predictor.expectText()) {
            return;
        }
        this.seenText = true;
        XMLEvent next;
        while (true) {
            next = this.staxEventReader.peek();
            if (!this.isIgnorable(next)) {
                break;
            }
            this.staxEventReader.nextEvent();
        }
        if (this.isTag(next)) {
            this.visitor.text(event.getData());
            return;
        }
        this.buffer.append(event.getData());
        while (true) {
            next = this.staxEventReader.peek();
            if (!this.isIgnorable(next)) {
                if (this.isTag(next)) {
                    break;
                }
                this.buffer.append(next.asCharacters().getData());
                this.staxEventReader.nextEvent();
            }
            else {
                this.staxEventReader.nextEvent();
            }
        }
        this.visitor.text(this.buffer);
        this.buffer.setLength(0);
    }
    
    private boolean isTag(final XMLEvent event) {
        final int eventType = event.getEventType();
        return eventType == 1 || eventType == 2;
    }
    
    private boolean isIgnorable(final XMLEvent event) {
        final int eventType = event.getEventType();
        return eventType == 5 || eventType == 3;
    }
    
    private void handleEndElement(final EndElement event) throws SAXException {
        if (!this.seenText && this.predictor.expectText()) {
            this.visitor.text("");
        }
        final QName qName = event.getName();
        this.tagName.uri = StAXConnector.fixNull(qName.getNamespaceURI());
        this.tagName.local = qName.getLocalPart();
        this.visitor.endElement(this.tagName);
        final Iterator<Namespace> i = event.getNamespaces();
        while (i.hasNext()) {
            final String prefix = StAXConnector.fixNull(i.next().getPrefix());
            this.visitor.endPrefixMapping(prefix);
        }
        this.seenText = false;
    }
    
    private void handleStartElement(final StartElement event) throws SAXException {
        final Iterator i = event.getNamespaces();
        while (i.hasNext()) {
            final Namespace ns = i.next();
            this.visitor.startPrefixMapping(StAXConnector.fixNull(ns.getPrefix()), StAXConnector.fixNull(ns.getNamespaceURI()));
        }
        final QName qName = event.getName();
        this.tagName.uri = StAXConnector.fixNull(qName.getNamespaceURI());
        final String localName = qName.getLocalPart();
        this.tagName.uri = StAXConnector.fixNull(qName.getNamespaceURI());
        this.tagName.local = localName;
        this.tagName.atts = this.getAttributes(event);
        this.visitor.startElement(this.tagName);
        this.seenText = false;
    }
    
    private Attributes getAttributes(final StartElement event) {
        this.attrs.clear();
        final Iterator i = event.getAttributes();
        while (i.hasNext()) {
            final Attribute staxAttr = i.next();
            final QName name = staxAttr.getName();
            final String uri = StAXConnector.fixNull(name.getNamespaceURI());
            final String localName = name.getLocalPart();
            final String prefix = name.getPrefix();
            String qName;
            if (prefix == null || prefix.length() == 0) {
                qName = localName;
            }
            else {
                qName = prefix + ':' + localName;
            }
            final String type = staxAttr.getDTDType();
            final String value = staxAttr.getValue();
            this.attrs.addAttribute(uri, localName, qName, type, value);
        }
        return this.attrs;
    }
}
