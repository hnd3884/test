package javax.xml.transform.stax;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Source;

public class StAXSource implements Source
{
    public static final String FEATURE = "http://javax.xml.transform.stax.StAXSource/feature";
    private final XMLStreamReader xmlStreamReader;
    private final XMLEventReader xmlEventReader;
    private final String systemId;
    
    public StAXSource(final XMLStreamReader xmlStreamReader) {
        if (xmlStreamReader == null) {
            throw new IllegalArgumentException("XMLStreamReader cannot be null.");
        }
        final int eventType = xmlStreamReader.getEventType();
        if (eventType != 7 && eventType != 1) {
            throw new IllegalStateException("The state of the XMLStreamReader must be START_DOCUMENT or START_ELEMENT");
        }
        this.xmlStreamReader = xmlStreamReader;
        this.xmlEventReader = null;
        this.systemId = xmlStreamReader.getLocation().getSystemId();
    }
    
    public StAXSource(final XMLEventReader xmlEventReader) throws XMLStreamException {
        if (xmlEventReader == null) {
            throw new IllegalArgumentException("XMLEventReader cannot be null.");
        }
        final XMLEvent peek = xmlEventReader.peek();
        if (!peek.isStartDocument() && !peek.isStartElement()) {
            throw new IllegalStateException("The state of the XMLEventReader must be START_DOCUMENT or START_ELEMENT");
        }
        this.xmlStreamReader = null;
        this.xmlEventReader = xmlEventReader;
        this.systemId = peek.getLocation().getSystemId();
    }
    
    public XMLStreamReader getXMLStreamReader() {
        return this.xmlStreamReader;
    }
    
    public XMLEventReader getXMLEventReader() {
        return this.xmlEventReader;
    }
    
    public String getSystemId() {
        return this.systemId;
    }
    
    public void setSystemId(final String s) {
        throw new UnsupportedOperationException("Setting systemId is not supported.");
    }
}
