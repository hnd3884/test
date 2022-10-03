package javax.xml.stream.util;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

public interface XMLEventConsumer
{
    void add(final XMLEvent p0) throws XMLStreamException;
}
