package com.sun.org.apache.xerces.internal.util;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamReader;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;

public final class StAXInputSource extends XMLInputSource
{
    private final XMLStreamReader fStreamReader;
    private final XMLEventReader fEventReader;
    private final boolean fConsumeRemainingContent;
    
    public StAXInputSource(final XMLStreamReader source) {
        this(source, false);
    }
    
    public StAXInputSource(final XMLStreamReader source, final boolean consumeRemainingContent) {
        super(null, source.getLocation().getSystemId(), null);
        if (source == null) {
            throw new IllegalArgumentException("XMLStreamReader parameter cannot be null.");
        }
        this.fStreamReader = source;
        this.fEventReader = null;
        this.fConsumeRemainingContent = consumeRemainingContent;
    }
    
    public StAXInputSource(final XMLEventReader source) {
        this(source, false);
    }
    
    public StAXInputSource(final XMLEventReader source, final boolean consumeRemainingContent) {
        super(null, getEventReaderSystemId(source), null);
        if (source == null) {
            throw new IllegalArgumentException("XMLEventReader parameter cannot be null.");
        }
        this.fStreamReader = null;
        this.fEventReader = source;
        this.fConsumeRemainingContent = consumeRemainingContent;
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
    
    @Override
    public void setSystemId(final String systemId) {
        throw new UnsupportedOperationException("Cannot set the system ID on a StAXInputSource");
    }
    
    private static String getEventReaderSystemId(final XMLEventReader reader) {
        try {
            if (reader != null) {
                return reader.peek().getLocation().getSystemId();
            }
        }
        catch (final XMLStreamException ex) {}
        return null;
    }
}
