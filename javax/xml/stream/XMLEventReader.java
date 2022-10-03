package javax.xml.stream;

import javax.xml.stream.events.XMLEvent;
import java.util.Iterator;

public interface XMLEventReader extends Iterator
{
    void close() throws XMLStreamException;
    
    String getElementText() throws XMLStreamException;
    
    Object getProperty(final String p0) throws IllegalArgumentException;
    
    boolean hasNext();
    
    XMLEvent nextEvent() throws XMLStreamException;
    
    XMLEvent nextTag() throws XMLStreamException;
    
    XMLEvent peek() throws XMLStreamException;
}
