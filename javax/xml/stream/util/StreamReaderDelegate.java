package javax.xml.stream.util;

import javax.xml.stream.Location;
import javax.xml.namespace.QName;
import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public class StreamReaderDelegate implements XMLStreamReader
{
    private XMLStreamReader reader;
    
    public StreamReaderDelegate() {
    }
    
    public StreamReaderDelegate(final XMLStreamReader reader) {
        this.reader = reader;
    }
    
    public void setParent(final XMLStreamReader reader) {
        this.reader = reader;
    }
    
    public XMLStreamReader getParent() {
        return this.reader;
    }
    
    public int next() throws XMLStreamException {
        return this.reader.next();
    }
    
    public int nextTag() throws XMLStreamException {
        return this.reader.nextTag();
    }
    
    public String getElementText() throws XMLStreamException {
        return this.reader.getElementText();
    }
    
    public void require(final int n, final String s, final String s2) throws XMLStreamException {
        this.reader.require(n, s, s2);
    }
    
    public boolean hasNext() throws XMLStreamException {
        return this.reader.hasNext();
    }
    
    public void close() throws XMLStreamException {
        this.reader.close();
    }
    
    public String getNamespaceURI(final String s) {
        return this.reader.getNamespaceURI(s);
    }
    
    public NamespaceContext getNamespaceContext() {
        return this.reader.getNamespaceContext();
    }
    
    public boolean isStartElement() {
        return this.reader.isStartElement();
    }
    
    public boolean isEndElement() {
        return this.reader.isEndElement();
    }
    
    public boolean isCharacters() {
        return this.reader.isCharacters();
    }
    
    public boolean isWhiteSpace() {
        return this.reader.isWhiteSpace();
    }
    
    public String getAttributeValue(final String s, final String s2) {
        return this.reader.getAttributeValue(s, s2);
    }
    
    public int getAttributeCount() {
        return this.reader.getAttributeCount();
    }
    
    public QName getAttributeName(final int n) {
        return this.reader.getAttributeName(n);
    }
    
    public String getAttributePrefix(final int n) {
        return this.reader.getAttributePrefix(n);
    }
    
    public String getAttributeNamespace(final int n) {
        return this.reader.getAttributeNamespace(n);
    }
    
    public String getAttributeLocalName(final int n) {
        return this.reader.getAttributeLocalName(n);
    }
    
    public String getAttributeType(final int n) {
        return this.reader.getAttributeType(n);
    }
    
    public String getAttributeValue(final int n) {
        return this.reader.getAttributeValue(n);
    }
    
    public boolean isAttributeSpecified(final int n) {
        return this.reader.isAttributeSpecified(n);
    }
    
    public int getNamespaceCount() {
        return this.reader.getNamespaceCount();
    }
    
    public String getNamespacePrefix(final int n) {
        return this.reader.getNamespacePrefix(n);
    }
    
    public String getNamespaceURI(final int n) {
        return this.reader.getNamespaceURI(n);
    }
    
    public int getEventType() {
        return this.reader.getEventType();
    }
    
    public String getText() {
        return this.reader.getText();
    }
    
    public int getTextCharacters(final int n, final char[] array, final int n2, final int n3) throws XMLStreamException {
        return this.reader.getTextCharacters(n, array, n2, n3);
    }
    
    public char[] getTextCharacters() {
        return this.reader.getTextCharacters();
    }
    
    public int getTextStart() {
        return this.reader.getTextStart();
    }
    
    public int getTextLength() {
        return this.reader.getTextLength();
    }
    
    public String getEncoding() {
        return this.reader.getEncoding();
    }
    
    public boolean hasText() {
        return this.reader.hasText();
    }
    
    public Location getLocation() {
        return this.reader.getLocation();
    }
    
    public QName getName() {
        return this.reader.getName();
    }
    
    public String getLocalName() {
        return this.reader.getLocalName();
    }
    
    public boolean hasName() {
        return this.reader.hasName();
    }
    
    public String getNamespaceURI() {
        return this.reader.getNamespaceURI();
    }
    
    public String getPrefix() {
        return this.reader.getPrefix();
    }
    
    public String getVersion() {
        return this.reader.getVersion();
    }
    
    public boolean isStandalone() {
        return this.reader.isStandalone();
    }
    
    public boolean standaloneSet() {
        return this.reader.standaloneSet();
    }
    
    public String getCharacterEncodingScheme() {
        return this.reader.getCharacterEncodingScheme();
    }
    
    public String getPITarget() {
        return this.reader.getPITarget();
    }
    
    public String getPIData() {
        return this.reader.getPIData();
    }
    
    public Object getProperty(final String s) throws IllegalArgumentException {
        return this.reader.getProperty(s);
    }
}
