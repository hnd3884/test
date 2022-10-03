package com.sun.xml.internal.stream;

import javax.xml.stream.events.EntityReference;
import java.util.NoSuchElementException;
import javax.xml.stream.XMLStreamException;
import com.sun.xml.internal.stream.events.XMLEventAllocatorImpl;
import javax.xml.stream.events.XMLEvent;
import javax.xml.stream.util.XMLEventAllocator;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLEventReader;

public class XMLEventReaderImpl implements XMLEventReader
{
    protected XMLStreamReader fXMLReader;
    protected XMLEventAllocator fXMLEventAllocator;
    private XMLEvent fPeekedEvent;
    private XMLEvent fLastEvent;
    
    public XMLEventReaderImpl(final XMLStreamReader reader) throws XMLStreamException {
        this.fXMLReader = reader;
        this.fXMLEventAllocator = (XMLEventAllocator)reader.getProperty("javax.xml.stream.allocator");
        if (this.fXMLEventAllocator == null) {
            this.fXMLEventAllocator = new XMLEventAllocatorImpl();
        }
        this.fPeekedEvent = this.fXMLEventAllocator.allocate(this.fXMLReader);
    }
    
    @Override
    public boolean hasNext() {
        if (this.fPeekedEvent != null) {
            return true;
        }
        boolean next = false;
        try {
            next = this.fXMLReader.hasNext();
        }
        catch (final XMLStreamException ex) {
            return false;
        }
        return next;
    }
    
    @Override
    public XMLEvent nextEvent() throws XMLStreamException {
        if (this.fPeekedEvent != null) {
            this.fLastEvent = this.fPeekedEvent;
            this.fPeekedEvent = null;
            return this.fLastEvent;
        }
        if (this.fXMLReader.hasNext()) {
            this.fXMLReader.next();
            return this.fLastEvent = this.fXMLEventAllocator.allocate(this.fXMLReader);
        }
        this.fLastEvent = null;
        throw new NoSuchElementException();
    }
    
    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void close() throws XMLStreamException {
        this.fXMLReader.close();
    }
    
    @Override
    public String getElementText() throws XMLStreamException {
        if (this.fLastEvent.getEventType() != 1) {
            throw new XMLStreamException("parser must be on START_ELEMENT to read next text", this.fLastEvent.getLocation());
        }
        String data = null;
        if (this.fPeekedEvent != null) {
            XMLEvent event = this.fPeekedEvent;
            this.fPeekedEvent = null;
            final int type = event.getEventType();
            if (type == 4 || type == 6 || type == 12) {
                data = event.asCharacters().getData();
            }
            else if (type == 9) {
                data = ((EntityReference)event).getDeclaration().getReplacementText();
            }
            else if (type != 5) {
                if (type != 3) {
                    if (type == 1) {
                        throw new XMLStreamException("elementGetText() function expects text only elment but START_ELEMENT was encountered.", event.getLocation());
                    }
                    if (type == 2) {
                        return "";
                    }
                }
            }
            final StringBuffer buffer = new StringBuffer();
            if (data != null && data.length() > 0) {
                buffer.append(data);
            }
            for (event = this.nextEvent(); event.getEventType() != 2; event = this.nextEvent()) {
                if (type == 4 || type == 6 || type == 12) {
                    data = event.asCharacters().getData();
                }
                else if (type == 9) {
                    data = ((EntityReference)event).getDeclaration().getReplacementText();
                }
                else if (type != 5) {
                    if (type != 3) {
                        if (type == 8) {
                            throw new XMLStreamException("unexpected end of document when reading element text content");
                        }
                        if (type == 1) {
                            throw new XMLStreamException("elementGetText() function expects text only elment but START_ELEMENT was encountered.", event.getLocation());
                        }
                        throw new XMLStreamException("Unexpected event type " + type, event.getLocation());
                    }
                }
                if (data != null && data.length() > 0) {
                    buffer.append(data);
                }
            }
            return buffer.toString();
        }
        data = this.fXMLReader.getElementText();
        this.fLastEvent = this.fXMLEventAllocator.allocate(this.fXMLReader);
        return data;
    }
    
    @Override
    public Object getProperty(final String name) throws IllegalArgumentException {
        return this.fXMLReader.getProperty(name);
    }
    
    @Override
    public XMLEvent nextTag() throws XMLStreamException {
        if (this.fPeekedEvent == null) {
            this.fXMLReader.nextTag();
            return this.fLastEvent = this.fXMLEventAllocator.allocate(this.fXMLReader);
        }
        XMLEvent event = this.fPeekedEvent;
        this.fPeekedEvent = null;
        int eventType = event.getEventType();
        if ((event.isCharacters() && event.asCharacters().isWhiteSpace()) || eventType == 3 || eventType == 5 || eventType == 7) {
            event = this.nextEvent();
            eventType = event.getEventType();
        }
        while ((event.isCharacters() && event.asCharacters().isWhiteSpace()) || eventType == 3 || eventType == 5) {
            event = this.nextEvent();
            eventType = event.getEventType();
        }
        if (eventType != 1 && eventType != 2) {
            throw new XMLStreamException("expected start or end tag", event.getLocation());
        }
        return event;
    }
    
    @Override
    public Object next() {
        Object object = null;
        try {
            object = this.nextEvent();
        }
        catch (final XMLStreamException streamException) {
            this.fLastEvent = null;
            final NoSuchElementException e = new NoSuchElementException(streamException.getMessage());
            e.initCause(streamException.getCause());
            throw e;
        }
        return object;
    }
    
    @Override
    public XMLEvent peek() throws XMLStreamException {
        if (this.fPeekedEvent != null) {
            return this.fPeekedEvent;
        }
        if (this.hasNext()) {
            this.fXMLReader.next();
            return this.fPeekedEvent = this.fXMLEventAllocator.allocate(this.fXMLReader);
        }
        return null;
    }
}
