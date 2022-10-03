package com.sun.xml.internal.stream;

import javax.xml.stream.events.XMLEvent;
import javax.xml.stream.XMLStreamException;
import java.util.NoSuchElementException;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.EventFilter;
import javax.xml.stream.util.EventReaderDelegate;

public class EventFilterSupport extends EventReaderDelegate
{
    EventFilter fEventFilter;
    
    public EventFilterSupport(final XMLEventReader eventReader, final EventFilter eventFilter) {
        this.setParent(eventReader);
        this.fEventFilter = eventFilter;
    }
    
    @Override
    public Object next() {
        try {
            return this.nextEvent();
        }
        catch (final XMLStreamException ex) {
            throw new NoSuchElementException();
        }
    }
    
    @Override
    public boolean hasNext() {
        try {
            return this.peek() != null;
        }
        catch (final XMLStreamException ex) {
            return false;
        }
    }
    
    @Override
    public XMLEvent nextEvent() throws XMLStreamException {
        if (!super.hasNext()) {
            throw new NoSuchElementException();
        }
        final XMLEvent event = super.nextEvent();
        if (this.fEventFilter.accept(event)) {
            return event;
        }
        return this.nextEvent();
    }
    
    @Override
    public XMLEvent nextTag() throws XMLStreamException {
        if (!super.hasNext()) {
            throw new NoSuchElementException();
        }
        final XMLEvent event = super.nextTag();
        if (this.fEventFilter.accept(event)) {
            return event;
        }
        return this.nextTag();
    }
    
    @Override
    public XMLEvent peek() throws XMLStreamException {
        while (true) {
            final XMLEvent event = super.peek();
            if (event == null) {
                return null;
            }
            if (this.fEventFilter.accept(event)) {
                return event;
            }
            super.next();
        }
    }
}
