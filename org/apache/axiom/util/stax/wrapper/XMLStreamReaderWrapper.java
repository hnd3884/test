package org.apache.axiom.util.stax.wrapper;

import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.Location;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public class XMLStreamReaderWrapper implements XMLStreamReader
{
    private final XMLStreamReader parent;
    
    public XMLStreamReaderWrapper(final XMLStreamReader parent) {
        this.parent = parent;
    }
    
    protected XMLStreamReader getParent() {
        return this.parent;
    }
    
    public void close() throws XMLStreamException {
        this.parent.close();
    }
    
    public int getAttributeCount() {
        return this.parent.getAttributeCount();
    }
    
    public String getAttributeLocalName(final int index) {
        return this.parent.getAttributeLocalName(index);
    }
    
    public QName getAttributeName(final int index) {
        return this.parent.getAttributeName(index);
    }
    
    public String getAttributeNamespace(final int index) {
        return this.parent.getAttributeNamespace(index);
    }
    
    public String getAttributePrefix(final int index) {
        return this.parent.getAttributePrefix(index);
    }
    
    public String getAttributeType(final int index) {
        return this.parent.getAttributeType(index);
    }
    
    public String getAttributeValue(final int index) {
        return this.parent.getAttributeValue(index);
    }
    
    public String getAttributeValue(final String namespaceURI, final String localName) {
        return this.parent.getAttributeValue(namespaceURI, localName);
    }
    
    public String getCharacterEncodingScheme() {
        return this.parent.getCharacterEncodingScheme();
    }
    
    public String getElementText() throws XMLStreamException {
        return this.parent.getElementText();
    }
    
    public String getEncoding() {
        return this.parent.getEncoding();
    }
    
    public int getEventType() {
        return this.parent.getEventType();
    }
    
    public String getLocalName() {
        return this.parent.getLocalName();
    }
    
    public Location getLocation() {
        return this.parent.getLocation();
    }
    
    public QName getName() {
        return this.parent.getName();
    }
    
    public NamespaceContext getNamespaceContext() {
        return this.parent.getNamespaceContext();
    }
    
    public int getNamespaceCount() {
        return this.parent.getNamespaceCount();
    }
    
    public String getNamespacePrefix(final int index) {
        return this.parent.getNamespacePrefix(index);
    }
    
    public String getNamespaceURI() {
        return this.parent.getNamespaceURI();
    }
    
    public String getNamespaceURI(final int index) {
        return this.parent.getNamespaceURI(index);
    }
    
    public String getNamespaceURI(final String prefix) {
        return this.parent.getNamespaceURI(prefix);
    }
    
    public String getPIData() {
        return this.parent.getPIData();
    }
    
    public String getPITarget() {
        return this.parent.getPITarget();
    }
    
    public String getPrefix() {
        return this.parent.getPrefix();
    }
    
    public Object getProperty(final String name) throws IllegalArgumentException {
        return this.parent.getProperty(name);
    }
    
    public String getText() {
        return this.parent.getText();
    }
    
    public char[] getTextCharacters() {
        return this.parent.getTextCharacters();
    }
    
    public int getTextCharacters(final int sourceStart, final char[] target, final int targetStart, final int length) throws XMLStreamException {
        return this.parent.getTextCharacters(sourceStart, target, targetStart, length);
    }
    
    public int getTextLength() {
        return this.parent.getTextLength();
    }
    
    public int getTextStart() {
        return this.parent.getTextStart();
    }
    
    public String getVersion() {
        return this.parent.getVersion();
    }
    
    public boolean hasName() {
        return this.parent.hasName();
    }
    
    public boolean hasNext() throws XMLStreamException {
        return this.parent.hasNext();
    }
    
    public boolean hasText() {
        return this.parent.hasText();
    }
    
    public boolean isAttributeSpecified(final int index) {
        return this.parent.isAttributeSpecified(index);
    }
    
    public boolean isCharacters() {
        return this.parent.isCharacters();
    }
    
    public boolean isEndElement() {
        return this.parent.isEndElement();
    }
    
    public boolean isStandalone() {
        return this.parent.isStandalone();
    }
    
    public boolean isStartElement() {
        return this.parent.isStartElement();
    }
    
    public boolean isWhiteSpace() {
        return this.parent.isWhiteSpace();
    }
    
    public int next() throws XMLStreamException {
        return this.parent.next();
    }
    
    public int nextTag() throws XMLStreamException {
        return this.parent.nextTag();
    }
    
    public void require(final int type, final String namespaceURI, final String localName) throws XMLStreamException {
        this.parent.require(type, namespaceURI, localName);
    }
    
    public boolean standaloneSet() {
        return this.parent.standaloneSet();
    }
}
