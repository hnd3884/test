package org.apache.xerces.stax.events;

import javax.xml.stream.XMLStreamException;
import java.io.Writer;
import java.io.StringWriter;
import javax.xml.namespace.QName;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import org.apache.xerces.stax.EmptyLocation;
import org.apache.xerces.stax.ImmutableLocation;
import javax.xml.stream.Location;
import javax.xml.stream.events.XMLEvent;

abstract class XMLEventImpl implements XMLEvent
{
    private int fEventType;
    private Location fLocation;
    
    XMLEventImpl(final int fEventType, final Location location) {
        this.fEventType = fEventType;
        if (location != null) {
            this.fLocation = new ImmutableLocation(location);
        }
        else {
            this.fLocation = EmptyLocation.getInstance();
        }
    }
    
    public final int getEventType() {
        return this.fEventType;
    }
    
    public final Location getLocation() {
        return this.fLocation;
    }
    
    public final boolean isStartElement() {
        return 1 == this.fEventType;
    }
    
    public final boolean isAttribute() {
        return 10 == this.fEventType;
    }
    
    public final boolean isNamespace() {
        return 13 == this.fEventType;
    }
    
    public final boolean isEndElement() {
        return 2 == this.fEventType;
    }
    
    public final boolean isEntityReference() {
        return 9 == this.fEventType;
    }
    
    public final boolean isProcessingInstruction() {
        return 3 == this.fEventType;
    }
    
    public final boolean isCharacters() {
        return 4 == this.fEventType || 12 == this.fEventType || 6 == this.fEventType;
    }
    
    public final boolean isStartDocument() {
        return 7 == this.fEventType;
    }
    
    public final boolean isEndDocument() {
        return 8 == this.fEventType;
    }
    
    public final StartElement asStartElement() {
        return (StartElement)this;
    }
    
    public final EndElement asEndElement() {
        return (EndElement)this;
    }
    
    public final Characters asCharacters() {
        return (Characters)this;
    }
    
    public final QName getSchemaType() {
        return null;
    }
    
    public final String toString() {
        final StringWriter stringWriter = new StringWriter();
        try {
            this.writeAsEncodedUnicode(stringWriter);
        }
        catch (final XMLStreamException ex) {}
        return stringWriter.toString();
    }
}
