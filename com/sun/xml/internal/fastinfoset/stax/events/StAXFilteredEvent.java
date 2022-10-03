package com.sun.xml.internal.fastinfoset.stax.events;

import javax.xml.stream.events.Characters;
import com.sun.xml.internal.fastinfoset.CommonResourceBundle;
import javax.xml.stream.events.XMLEvent;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.EventFilter;
import javax.xml.stream.XMLEventReader;

public class StAXFilteredEvent implements XMLEventReader
{
    private XMLEventReader eventReader;
    private EventFilter _filter;
    
    public StAXFilteredEvent() {
    }
    
    public StAXFilteredEvent(final XMLEventReader reader, final EventFilter filter) throws XMLStreamException {
        this.eventReader = reader;
        this._filter = filter;
    }
    
    public void setEventReader(final XMLEventReader reader) {
        this.eventReader = reader;
    }
    
    public void setFilter(final EventFilter filter) {
        this._filter = filter;
    }
    
    @Override
    public Object next() {
        try {
            return this.nextEvent();
        }
        catch (final XMLStreamException e) {
            return null;
        }
    }
    
    @Override
    public XMLEvent nextEvent() throws XMLStreamException {
        if (this.hasNext()) {
            return this.eventReader.nextEvent();
        }
        return null;
    }
    
    @Override
    public String getElementText() throws XMLStreamException {
        final StringBuffer buffer = new StringBuffer();
        XMLEvent e = this.nextEvent();
        if (!e.isStartElement()) {
            throw new XMLStreamException(CommonResourceBundle.getInstance().getString("message.mustBeOnSTART_ELEMENT"));
        }
        while (this.hasNext()) {
            e = this.nextEvent();
            if (e.isStartElement()) {
                throw new XMLStreamException(CommonResourceBundle.getInstance().getString("message.getElementTextExpectTextOnly"));
            }
            if (e.isCharacters()) {
                buffer.append(((Characters)e).getData());
            }
            if (e.isEndElement()) {
                return buffer.toString();
            }
        }
        throw new XMLStreamException(CommonResourceBundle.getInstance().getString("message.END_ELEMENTnotFound"));
    }
    
    @Override
    public XMLEvent nextTag() throws XMLStreamException {
        while (this.hasNext()) {
            final XMLEvent e = this.nextEvent();
            if (e.isStartElement() || e.isEndElement()) {
                return e;
            }
        }
        throw new XMLStreamException(CommonResourceBundle.getInstance().getString("message.startOrEndNotFound"));
    }
    
    @Override
    public boolean hasNext() {
        try {
            while (this.eventReader.hasNext()) {
                if (this._filter.accept(this.eventReader.peek())) {
                    return true;
                }
                this.eventReader.nextEvent();
            }
            return false;
        }
        catch (final XMLStreamException e) {
            return false;
        }
    }
    
    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public XMLEvent peek() throws XMLStreamException {
        if (this.hasNext()) {
            return this.eventReader.peek();
        }
        return null;
    }
    
    @Override
    public void close() throws XMLStreamException {
        this.eventReader.close();
    }
    
    @Override
    public Object getProperty(final String name) {
        return this.eventReader.getProperty(name);
    }
}
