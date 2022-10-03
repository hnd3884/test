package com.sun.xml.internal.stream.events;

import java.io.IOException;
import javax.xml.stream.XMLStreamException;
import java.io.Writer;
import javax.xml.namespace.QName;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.Characters;
import javax.xml.stream.Location;
import javax.xml.stream.events.XMLEvent;

public abstract class DummyEvent implements XMLEvent
{
    private static DummyLocation nowhere;
    private int fEventType;
    protected Location fLocation;
    
    public DummyEvent() {
        this.fLocation = DummyEvent.nowhere;
    }
    
    public DummyEvent(final int i) {
        this.fLocation = DummyEvent.nowhere;
        this.fEventType = i;
    }
    
    @Override
    public int getEventType() {
        return this.fEventType;
    }
    
    protected void setEventType(final int eventType) {
        this.fEventType = eventType;
    }
    
    @Override
    public boolean isStartElement() {
        return this.fEventType == 1;
    }
    
    @Override
    public boolean isEndElement() {
        return this.fEventType == 2;
    }
    
    @Override
    public boolean isEntityReference() {
        return this.fEventType == 9;
    }
    
    @Override
    public boolean isProcessingInstruction() {
        return this.fEventType == 3;
    }
    
    public boolean isCharacterData() {
        return this.fEventType == 4;
    }
    
    @Override
    public boolean isStartDocument() {
        return this.fEventType == 7;
    }
    
    @Override
    public boolean isEndDocument() {
        return this.fEventType == 8;
    }
    
    @Override
    public Location getLocation() {
        return this.fLocation;
    }
    
    void setLocation(final Location loc) {
        if (loc == null) {
            this.fLocation = DummyEvent.nowhere;
        }
        else {
            this.fLocation = loc;
        }
    }
    
    @Override
    public Characters asCharacters() {
        return (Characters)this;
    }
    
    @Override
    public EndElement asEndElement() {
        return (EndElement)this;
    }
    
    @Override
    public StartElement asStartElement() {
        return (StartElement)this;
    }
    
    @Override
    public QName getSchemaType() {
        return null;
    }
    
    @Override
    public boolean isAttribute() {
        return this.fEventType == 10;
    }
    
    @Override
    public boolean isCharacters() {
        return this.fEventType == 4;
    }
    
    @Override
    public boolean isNamespace() {
        return this.fEventType == 13;
    }
    
    @Override
    public void writeAsEncodedUnicode(final Writer writer) throws XMLStreamException {
        try {
            this.writeAsEncodedUnicodeEx(writer);
        }
        catch (final IOException e) {
            throw new XMLStreamException(e);
        }
    }
    
    protected abstract void writeAsEncodedUnicodeEx(final Writer p0) throws IOException, XMLStreamException;
    
    protected void charEncode(final Writer writer, final String data) throws IOException {
        if (data == null || data == "") {
            return;
        }
        int i = 0;
        int start = 0;
        int len;
        for (len = data.length(); i < len; ++i) {
            switch (data.charAt(i)) {
                case '<': {
                    writer.write(data, start, i - start);
                    writer.write("&lt;");
                    start = i + 1;
                    break;
                }
                case '&': {
                    writer.write(data, start, i - start);
                    writer.write("&amp;");
                    start = i + 1;
                    break;
                }
                case '>': {
                    writer.write(data, start, i - start);
                    writer.write("&gt;");
                    start = i + 1;
                    break;
                }
                case '\"': {
                    writer.write(data, start, i - start);
                    writer.write("&quot;");
                    start = i + 1;
                    break;
                }
            }
        }
        writer.write(data, start, len - start);
    }
    
    static {
        DummyEvent.nowhere = new DummyLocation();
    }
    
    static class DummyLocation implements Location
    {
        public DummyLocation() {
        }
        
        @Override
        public int getCharacterOffset() {
            return -1;
        }
        
        @Override
        public int getColumnNumber() {
            return -1;
        }
        
        @Override
        public int getLineNumber() {
            return -1;
        }
        
        @Override
        public String getPublicId() {
            return null;
        }
        
        @Override
        public String getSystemId() {
            return null;
        }
    }
}
