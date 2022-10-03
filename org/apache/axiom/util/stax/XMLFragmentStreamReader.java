package org.apache.axiom.util.stax;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import java.util.NoSuchElementException;
import javax.xml.stream.XMLStreamReader;

public class XMLFragmentStreamReader implements XMLStreamReader
{
    private static final int STATE_START_DOCUMENT = 0;
    private static final int STATE_IN_FRAGMENT = 1;
    private static final int STATE_FRAGMENT_END = 2;
    private static final int STATE_END_DOCUMENT = 3;
    private XMLStreamReader parent;
    private int state;
    private int depth;
    
    public XMLFragmentStreamReader(final XMLStreamReader parent) {
        this.parent = parent;
        if (parent.getEventType() != 1) {
            throw new IllegalStateException("Expected START_ELEMENT as current event");
        }
    }
    
    public int getEventType() {
        switch (this.state) {
            case 0: {
                return 7;
            }
            case 1: {
                return this.parent.getEventType();
            }
            case 2: {
                return 2;
            }
            case 3: {
                return 8;
            }
            default: {
                throw new IllegalStateException();
            }
        }
    }
    
    public int next() throws XMLStreamException {
        switch (this.state) {
            case 0: {
                return this.state = 1;
            }
            case 1: {
                final int type = this.parent.next();
                switch (type) {
                    case 1: {
                        ++this.depth;
                        break;
                    }
                    case 2: {
                        if (this.depth == 0) {
                            this.state = 2;
                            break;
                        }
                        --this.depth;
                        break;
                    }
                }
                return type;
            }
            case 2: {
                this.parent.next();
                this.state = 3;
                return 8;
            }
            default: {
                throw new NoSuchElementException("End of document reached");
            }
        }
    }
    
    public int nextTag() throws XMLStreamException {
        switch (this.state) {
            case 0: {
                return this.state = 1;
            }
            case 2:
            case 3: {
                throw new NoSuchElementException();
            }
            default: {
                final int result = this.parent.nextTag();
                switch (result) {
                    case 1: {
                        ++this.depth;
                        break;
                    }
                    case 2: {
                        if (this.depth == 0) {
                            this.state = 2;
                            break;
                        }
                        --this.depth;
                        break;
                    }
                }
                return result;
            }
        }
    }
    
    public void close() throws XMLStreamException {
        this.parent = null;
    }
    
    public Object getProperty(final String name) throws IllegalArgumentException {
        return this.parent.getProperty(name);
    }
    
    public String getCharacterEncodingScheme() {
        if (this.state == 0) {
            return null;
        }
        throw new IllegalStateException();
    }
    
    public String getEncoding() {
        if (this.state == 0) {
            return null;
        }
        throw new IllegalStateException();
    }
    
    public String getVersion() {
        return "1.0";
    }
    
    public boolean isStandalone() {
        return true;
    }
    
    public boolean standaloneSet() {
        return false;
    }
    
    public Location getLocation() {
        return this.parent.getLocation();
    }
    
    public int getAttributeCount() {
        if (this.state == 0 || this.state == 3) {
            throw new IllegalStateException();
        }
        return this.parent.getAttributeCount();
    }
    
    public String getAttributeLocalName(final int index) {
        if (this.state == 0 || this.state == 3) {
            throw new IllegalStateException();
        }
        return this.parent.getAttributeLocalName(index);
    }
    
    public QName getAttributeName(final int index) {
        if (this.state == 0 || this.state == 3) {
            throw new IllegalStateException();
        }
        return this.parent.getAttributeName(index);
    }
    
    public String getAttributeNamespace(final int index) {
        if (this.state == 0 || this.state == 3) {
            throw new IllegalStateException();
        }
        return this.parent.getAttributeNamespace(index);
    }
    
    public String getAttributePrefix(final int index) {
        if (this.state == 0 || this.state == 3) {
            throw new IllegalStateException();
        }
        return this.parent.getAttributePrefix(index);
    }
    
    public String getAttributeType(final int index) {
        if (this.state == 0 || this.state == 3) {
            throw new IllegalStateException();
        }
        return this.parent.getAttributeType(index);
    }
    
    public String getAttributeValue(final int index) {
        if (this.state == 0 || this.state == 3) {
            throw new IllegalStateException();
        }
        return this.parent.getAttributeValue(index);
    }
    
    public boolean isAttributeSpecified(final int index) {
        if (this.state == 0 || this.state == 3) {
            throw new IllegalStateException();
        }
        return this.parent.isAttributeSpecified(index);
    }
    
    public String getAttributeValue(final String namespaceURI, final String localName) {
        if (this.state == 0 || this.state == 3) {
            throw new IllegalStateException();
        }
        return this.parent.getAttributeValue(namespaceURI, localName);
    }
    
    public String getElementText() throws XMLStreamException {
        if (this.state == 0 || this.state == 3) {
            throw new IllegalStateException();
        }
        return this.parent.getElementText();
    }
    
    public String getLocalName() {
        if (this.state == 0 || this.state == 3) {
            throw new IllegalStateException();
        }
        return this.parent.getLocalName();
    }
    
    public QName getName() {
        if (this.state == 0 || this.state == 3) {
            throw new IllegalStateException();
        }
        return this.parent.getName();
    }
    
    public String getPrefix() {
        if (this.state == 0 || this.state == 3) {
            throw new IllegalStateException();
        }
        return this.parent.getPrefix();
    }
    
    public String getNamespaceURI() {
        if (this.state == 0 || this.state == 3) {
            throw new IllegalStateException();
        }
        return this.parent.getNamespaceURI();
    }
    
    public int getNamespaceCount() {
        if (this.state == 0 || this.state == 3) {
            throw new IllegalStateException();
        }
        return this.parent.getNamespaceCount();
    }
    
    public String getNamespacePrefix(final int index) {
        if (this.state == 0 || this.state == 3) {
            throw new IllegalStateException();
        }
        return this.parent.getNamespacePrefix(index);
    }
    
    public String getNamespaceURI(final int index) {
        if (this.state == 0 || this.state == 3) {
            throw new IllegalStateException();
        }
        return this.parent.getNamespaceURI(index);
    }
    
    public String getNamespaceURI(final String prefix) {
        if (this.state == 0 || this.state == 3) {
            throw new IllegalStateException();
        }
        return this.parent.getNamespaceURI(prefix);
    }
    
    public NamespaceContext getNamespaceContext() {
        return this.parent.getNamespaceContext();
    }
    
    public String getPIData() {
        if (this.state == 0 || this.state == 3) {
            throw new IllegalStateException();
        }
        return this.parent.getPIData();
    }
    
    public String getPITarget() {
        if (this.state == 0 || this.state == 3) {
            throw new IllegalStateException();
        }
        return this.parent.getPITarget();
    }
    
    public String getText() {
        if (this.state == 0 || this.state == 3) {
            throw new IllegalStateException();
        }
        return this.parent.getText();
    }
    
    public char[] getTextCharacters() {
        if (this.state == 0 || this.state == 3) {
            throw new IllegalStateException();
        }
        return this.parent.getTextCharacters();
    }
    
    public int getTextCharacters(final int sourceStart, final char[] target, final int targetStart, final int length) throws XMLStreamException {
        if (this.state == 0 || this.state == 3) {
            throw new IllegalStateException();
        }
        return this.getTextCharacters(sourceStart, target, targetStart, length);
    }
    
    public int getTextLength() {
        if (this.state == 0 || this.state == 3) {
            throw new IllegalStateException();
        }
        return this.parent.getTextLength();
    }
    
    public int getTextStart() {
        if (this.state == 0 || this.state == 3) {
            throw new IllegalStateException();
        }
        return this.parent.getTextStart();
    }
    
    public boolean hasName() {
        return this.state != 0 && this.state != 3 && this.parent.hasName();
    }
    
    public boolean hasNext() throws XMLStreamException {
        return this.state != 3;
    }
    
    public boolean hasText() {
        return this.state != 0 && this.state != 3 && this.parent.hasText();
    }
    
    public boolean isCharacters() {
        return this.state != 0 && this.state != 3 && this.parent.isCharacters();
    }
    
    public boolean isStartElement() {
        return this.state != 0 && this.state != 3 && this.parent.isStartElement();
    }
    
    public boolean isEndElement() {
        return this.state != 0 && this.state != 3 && this.parent.isEndElement();
    }
    
    public boolean isWhiteSpace() {
        return this.state != 0 && this.state != 3 && this.parent.isWhiteSpace();
    }
    
    public void require(final int type, final String namespaceURI, final String localName) throws XMLStreamException {
        switch (this.state) {
            case 0: {
                if (type != 7) {
                    throw new XMLStreamException("Expected START_DOCUMENT");
                }
                break;
            }
            case 3: {
                if (type != 8) {
                    throw new XMLStreamException("Expected END_DOCUMENT");
                }
                break;
            }
            default: {
                this.parent.require(type, namespaceURI, localName);
                break;
            }
        }
    }
}
