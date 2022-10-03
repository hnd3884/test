package org.apache.axiom.util.stax;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamReader;

public abstract class AbstractXMLStreamReader implements XMLStreamReader
{
    public Location getLocation() {
        return DummyLocation.INSTANCE;
    }
    
    public boolean hasText() {
        final int event = this.getEventType();
        return event == 4 || event == 11 || event == 12 || event == 9 || event == 5 || event == 6;
    }
    
    public int nextTag() throws XMLStreamException {
        int eventType;
        for (eventType = this.next(); (eventType == 4 && this.isWhiteSpace()) || (eventType == 12 && this.isWhiteSpace()) || eventType == 6 || eventType == 3 || eventType == 5; eventType = this.next()) {}
        if (eventType != 1 && eventType != 2) {
            throw new XMLStreamException("expected start or end tag", this.getLocation());
        }
        return eventType;
    }
    
    public boolean isStartElement() {
        return this.getEventType() == 1;
    }
    
    public boolean isEndElement() {
        return this.getEventType() == 2;
    }
    
    public boolean isCharacters() {
        return this.getEventType() == 4;
    }
    
    public boolean isWhiteSpace() {
        switch (this.getEventType()) {
            case 6: {
                return true;
            }
            case 4: {
                final String text = this.getText();
                for (int i = 0; i < text.length(); ++i) {
                    final char c = text.charAt(i);
                    if (c != ' ' && c != '\t' && c != '\r' && c != '\n') {
                        return false;
                    }
                }
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    public boolean hasName() {
        final int event = this.getEventType();
        return event == 1 || event == 2;
    }
    
    public void require(final int type, final String uri, final String localName) throws XMLStreamException {
        final int actualType = this.getEventType();
        if (type != actualType) {
            throw new XMLStreamException("Required type " + XMLEventUtils.getEventTypeString(type) + ", actual type " + XMLEventUtils.getEventTypeString(actualType));
        }
        if (localName != null) {
            if (actualType != 1 && actualType != 2 && actualType != 9) {
                throw new XMLStreamException("Required a non-null local name, but current token not a START_ELEMENT, END_ELEMENT or ENTITY_REFERENCE (was " + XMLEventUtils.getEventTypeString(actualType) + ")");
            }
            final String actualLocalName = this.getLocalName();
            if (actualLocalName != localName && !actualLocalName.equals(localName)) {
                throw new XMLStreamException("Required local name '" + localName + "'; current local name '" + actualLocalName + "'.");
            }
        }
        if (uri != null) {
            if (actualType != 1 && actualType != 2) {
                throw new XMLStreamException("Required non-null namespace URI, but current token not a START_ELEMENT or END_ELEMENT (was " + XMLEventUtils.getEventTypeString(actualType) + ")");
            }
            final String actualUri = this.getNamespaceURI();
            if (uri.length() == 0) {
                if (actualUri != null && actualUri.length() > 0) {
                    throw new XMLStreamException("Required empty namespace, instead have '" + actualUri + "'.");
                }
            }
            else if (!uri.equals(actualUri)) {
                throw new XMLStreamException("Required namespace '" + uri + "'; have '" + actualUri + "'.");
            }
        }
    }
    
    public String getElementText() throws XMLStreamException {
        if (this.getEventType() != 1) {
            throw new XMLStreamException("parser must be on START_ELEMENT to read next text", this.getLocation());
        }
        int eventType = this.next();
        final StringBuffer content = new StringBuffer();
        while (eventType != 2) {
            if (eventType == 4 || eventType == 12 || eventType == 6 || eventType == 9) {
                content.append(this.getText());
            }
            else if (eventType != 3) {
                if (eventType != 5) {
                    if (eventType == 8) {
                        throw new XMLStreamException("unexpected end of document when reading element text content");
                    }
                    if (eventType == 1) {
                        throw new XMLStreamException("element text content may not contain START_ELEMENT");
                    }
                    throw new XMLStreamException("Unexpected event type " + eventType, this.getLocation());
                }
            }
            eventType = this.next();
        }
        return content.toString();
    }
}
