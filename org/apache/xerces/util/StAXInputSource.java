package org.apache.xerces.util;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamReader;
import org.apache.xerces.xni.parser.XMLInputSource;

public final class StAXInputSource extends XMLInputSource
{
    private final XMLStreamReader fStreamReader;
    private final XMLEventReader fEventReader;
    private final boolean fConsumeRemainingContent;
    
    public StAXInputSource(final XMLStreamReader xmlStreamReader) {
        this(xmlStreamReader, false);
    }
    
    public StAXInputSource(final XMLStreamReader fStreamReader, final boolean fConsumeRemainingContent) {
        super(null, getStreamReaderSystemId(fStreamReader), null);
        if (fStreamReader == null) {
            throw new IllegalArgumentException("XMLStreamReader parameter cannot be null.");
        }
        this.fStreamReader = fStreamReader;
        this.fEventReader = null;
        this.fConsumeRemainingContent = fConsumeRemainingContent;
    }
    
    public StAXInputSource(final XMLEventReader xmlEventReader) {
        this(xmlEventReader, false);
    }
    
    public StAXInputSource(final XMLEventReader fEventReader, final boolean fConsumeRemainingContent) {
        super(null, getEventReaderSystemId(fEventReader), null);
        if (fEventReader == null) {
            throw new IllegalArgumentException("XMLEventReader parameter cannot be null.");
        }
        this.fStreamReader = null;
        this.fEventReader = fEventReader;
        this.fConsumeRemainingContent = fConsumeRemainingContent;
    }
    
    public XMLStreamReader getXMLStreamReader() {
        return this.fStreamReader;
    }
    
    public XMLEventReader getXMLEventReader() {
        return this.fEventReader;
    }
    
    public boolean shouldConsumeRemainingContent() {
        return this.fConsumeRemainingContent;
    }
    
    public void setSystemId(final String s) {
        throw new UnsupportedOperationException("Cannot set the system ID on a StAXInputSource");
    }
    
    private static String getStreamReaderSystemId(final XMLStreamReader xmlStreamReader) {
        if (xmlStreamReader != null) {
            return xmlStreamReader.getLocation().getSystemId();
        }
        return null;
    }
    
    private static String getEventReaderSystemId(final XMLEventReader xmlEventReader) {
        try {
            if (xmlEventReader != null) {
                return xmlEventReader.peek().getLocation().getSystemId();
            }
        }
        catch (final XMLStreamException ex) {}
        return null;
    }
}
