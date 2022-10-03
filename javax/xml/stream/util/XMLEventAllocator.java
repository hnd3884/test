package javax.xml.stream.util;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import javax.xml.stream.XMLStreamReader;

public interface XMLEventAllocator
{
    XMLEvent allocate(final XMLStreamReader p0) throws XMLStreamException;
    
    void allocate(final XMLStreamReader p0, final XMLEventConsumer p1) throws XMLStreamException;
    
    XMLEventAllocator newInstance();
}
