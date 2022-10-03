package org.apache.axiom.util.stax;

import java.util.Map;
import org.apache.axiom.util.namespace.MapBasedNamespaceContext;
import java.util.Collections;
import javax.xml.stream.Location;
import java.io.IOException;
import javax.xml.stream.XMLStreamException;
import javax.xml.namespace.NamespaceContext;
import java.io.Reader;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;

public class WrappedTextNodeStreamReader implements XMLStreamReader
{
    private final QName wrapperElementName;
    private final Reader reader;
    private final int chunkSize;
    private int eventType;
    private char[] charData;
    private int charDataLength;
    private NamespaceContext namespaceContext;
    
    public WrappedTextNodeStreamReader(final QName wrapperElementName, final Reader reader, final int chunkSize) {
        this.eventType = 7;
        this.wrapperElementName = wrapperElementName;
        this.reader = reader;
        this.chunkSize = chunkSize;
    }
    
    public WrappedTextNodeStreamReader(final QName wrapperElementName, final Reader reader) {
        this(wrapperElementName, reader, 4096);
    }
    
    public Object getProperty(final String name) throws IllegalArgumentException {
        return null;
    }
    
    public boolean hasNext() throws XMLStreamException {
        return this.eventType != 8;
    }
    
    public int next() throws XMLStreamException {
        switch (this.eventType) {
            case 7: {
                this.eventType = 1;
                break;
            }
            case 1: {
                this.charData = new char[this.chunkSize];
            }
            case 4: {
                try {
                    this.charDataLength = this.reader.read(this.charData);
                }
                catch (final IOException ex) {
                    throw new XMLStreamException(ex);
                }
                if (this.charDataLength == -1) {
                    this.charData = null;
                    this.eventType = 2;
                    break;
                }
                this.eventType = 4;
                break;
            }
            case 2: {
                this.eventType = 8;
                break;
            }
            default: {
                throw new IllegalStateException();
            }
        }
        return this.eventType;
    }
    
    public int nextTag() throws XMLStreamException {
        throw new XMLStreamException("Current event is not white space");
    }
    
    public int getEventType() {
        return this.eventType;
    }
    
    public boolean isStartElement() {
        return this.eventType == 1;
    }
    
    public boolean isEndElement() {
        return this.eventType == 2;
    }
    
    public boolean isCharacters() {
        return this.eventType == 4;
    }
    
    public boolean isWhiteSpace() {
        return false;
    }
    
    public boolean hasText() {
        return this.eventType == 4;
    }
    
    public boolean hasName() {
        return this.eventType == 1 || this.eventType == 2;
    }
    
    public void require(final int type, final String namespaceURI, final String localName) throws XMLStreamException {
        if (type != this.eventType || (namespaceURI != null && !namespaceURI.equals(this.getNamespaceURI())) || (localName != null && !namespaceURI.equals(this.getLocalName()))) {
            throw new XMLStreamException("Unexpected event type");
        }
    }
    
    public Location getLocation() {
        return DummyLocation.INSTANCE;
    }
    
    public void close() throws XMLStreamException {
        try {
            this.reader.close();
        }
        catch (final IOException ex) {
            throw new XMLStreamException(ex);
        }
    }
    
    public String getEncoding() {
        return null;
    }
    
    public String getCharacterEncodingScheme() {
        return null;
    }
    
    public String getVersion() {
        return null;
    }
    
    public boolean standaloneSet() {
        return false;
    }
    
    public boolean isStandalone() {
        return true;
    }
    
    public NamespaceContext getNamespaceContext() {
        if (this.namespaceContext == null) {
            this.namespaceContext = new MapBasedNamespaceContext(Collections.singletonMap(this.wrapperElementName.getPrefix(), this.wrapperElementName.getNamespaceURI()));
        }
        return this.namespaceContext;
    }
    
    public String getNamespaceURI(final String prefix) {
        final String namespaceURI = this.getNamespaceContext().getNamespaceURI(prefix);
        return namespaceURI.equals("") ? null : prefix;
    }
    
    private void checkStartElement() {
        if (this.eventType != 1) {
            throw new IllegalStateException();
        }
    }
    
    public String getAttributeValue(final String namespaceURI, final String localName) {
        this.checkStartElement();
        return null;
    }
    
    public int getAttributeCount() {
        this.checkStartElement();
        return 0;
    }
    
    public QName getAttributeName(final int index) {
        this.checkStartElement();
        throw new ArrayIndexOutOfBoundsException();
    }
    
    public String getAttributeLocalName(final int index) {
        this.checkStartElement();
        throw new ArrayIndexOutOfBoundsException();
    }
    
    public String getAttributePrefix(final int index) {
        this.checkStartElement();
        throw new ArrayIndexOutOfBoundsException();
    }
    
    public String getAttributeNamespace(final int index) {
        this.checkStartElement();
        throw new ArrayIndexOutOfBoundsException();
    }
    
    public String getAttributeType(final int index) {
        this.checkStartElement();
        throw new ArrayIndexOutOfBoundsException();
    }
    
    public String getAttributeValue(final int index) {
        this.checkStartElement();
        throw new ArrayIndexOutOfBoundsException();
    }
    
    public boolean isAttributeSpecified(final int index) {
        this.checkStartElement();
        throw new ArrayIndexOutOfBoundsException();
    }
    
    private void checkElement() {
        if (this.eventType != 1 && this.eventType != 2) {
            throw new IllegalStateException();
        }
    }
    
    public QName getName() {
        return null;
    }
    
    public String getLocalName() {
        this.checkElement();
        return this.wrapperElementName.getLocalPart();
    }
    
    public String getPrefix() {
        return this.wrapperElementName.getPrefix();
    }
    
    public String getNamespaceURI() {
        this.checkElement();
        return this.wrapperElementName.getNamespaceURI();
    }
    
    public int getNamespaceCount() {
        this.checkElement();
        return 1;
    }
    
    public String getNamespacePrefix(final int index) {
        this.checkElement();
        if (index == 0) {
            return this.wrapperElementName.getPrefix();
        }
        throw new IndexOutOfBoundsException();
    }
    
    public String getNamespaceURI(final int index) {
        this.checkElement();
        if (index == 0) {
            return this.wrapperElementName.getNamespaceURI();
        }
        throw new IndexOutOfBoundsException();
    }
    
    public String getElementText() throws XMLStreamException {
        if (this.eventType == 1) {
            try {
                final StringBuffer buffer = new StringBuffer();
                final char[] cbuf = new char[4096];
                int c;
                while ((c = this.reader.read(cbuf)) != -1) {
                    buffer.append(cbuf, 0, c);
                }
                this.eventType = 2;
                return buffer.toString();
            }
            catch (final IOException ex) {
                throw new XMLStreamException(ex);
            }
        }
        throw new XMLStreamException("Current event is not a START_ELEMENT");
    }
    
    private void checkCharacters() {
        if (this.eventType != 4) {
            throw new IllegalStateException();
        }
    }
    
    public String getText() {
        this.checkCharacters();
        return new String(this.charData, 0, this.charDataLength);
    }
    
    public char[] getTextCharacters() {
        this.checkCharacters();
        return this.charData;
    }
    
    public int getTextStart() {
        this.checkCharacters();
        return 0;
    }
    
    public int getTextLength() {
        this.checkCharacters();
        return this.charDataLength;
    }
    
    public int getTextCharacters(final int sourceStart, final char[] target, final int targetStart, final int length) throws XMLStreamException {
        this.checkCharacters();
        final int c = Math.min(this.charDataLength - sourceStart, length);
        System.arraycopy(this.charData, sourceStart, target, targetStart, c);
        return c;
    }
    
    public String getPIData() {
        throw new IllegalStateException();
    }
    
    public String getPITarget() {
        throw new IllegalStateException();
    }
}
