package javax.xml.stream.util;

import javax.xml.stream.events.XMLEvent;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLEventReader;

public class EventReaderDelegate implements XMLEventReader
{
    private XMLEventReader reader;
    
    public EventReaderDelegate() {
    }
    
    public EventReaderDelegate(final XMLEventReader reader) {
        this.reader = reader;
    }
    
    public void setParent(final XMLEventReader reader) {
        this.reader = reader;
    }
    
    public XMLEventReader getParent() {
        return this.reader;
    }
    
    public void close() throws XMLStreamException {
        this.reader.close();
    }
    
    public String getElementText() throws XMLStreamException {
        return this.reader.getElementText();
    }
    
    public Object getProperty(final String s) throws IllegalArgumentException {
        return this.reader.getProperty(s);
    }
    
    public boolean hasNext() {
        return this.reader.hasNext();
    }
    
    public Object next() {
        return this.reader.next();
    }
    
    public XMLEvent nextEvent() throws XMLStreamException {
        return this.reader.nextEvent();
    }
    
    public XMLEvent nextTag() throws XMLStreamException {
        return this.reader.nextTag();
    }
    
    public XMLEvent peek() throws XMLStreamException {
        return this.reader.peek();
    }
    
    public void remove() {
        this.reader.remove();
    }
}
