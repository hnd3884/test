package com.sun.xml.internal.fastinfoset.stax.util;

import javax.xml.stream.Location;
import javax.xml.namespace.QName;
import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public class StAXParserWrapper implements XMLStreamReader
{
    private XMLStreamReader _reader;
    
    public StAXParserWrapper() {
    }
    
    public StAXParserWrapper(final XMLStreamReader reader) {
        this._reader = reader;
    }
    
    public void setReader(final XMLStreamReader reader) {
        this._reader = reader;
    }
    
    public XMLStreamReader getReader() {
        return this._reader;
    }
    
    @Override
    public int next() throws XMLStreamException {
        return this._reader.next();
    }
    
    @Override
    public int nextTag() throws XMLStreamException {
        return this._reader.nextTag();
    }
    
    @Override
    public String getElementText() throws XMLStreamException {
        return this._reader.getElementText();
    }
    
    @Override
    public void require(final int type, final String namespaceURI, final String localName) throws XMLStreamException {
        this._reader.require(type, namespaceURI, localName);
    }
    
    @Override
    public boolean hasNext() throws XMLStreamException {
        return this._reader.hasNext();
    }
    
    @Override
    public void close() throws XMLStreamException {
        this._reader.close();
    }
    
    @Override
    public String getNamespaceURI(final String prefix) {
        return this._reader.getNamespaceURI(prefix);
    }
    
    @Override
    public NamespaceContext getNamespaceContext() {
        return this._reader.getNamespaceContext();
    }
    
    @Override
    public boolean isStartElement() {
        return this._reader.isStartElement();
    }
    
    @Override
    public boolean isEndElement() {
        return this._reader.isEndElement();
    }
    
    @Override
    public boolean isCharacters() {
        return this._reader.isCharacters();
    }
    
    @Override
    public boolean isWhiteSpace() {
        return this._reader.isWhiteSpace();
    }
    
    @Override
    public QName getAttributeName(final int index) {
        return this._reader.getAttributeName(index);
    }
    
    @Override
    public int getTextCharacters(final int sourceStart, final char[] target, final int targetStart, final int length) throws XMLStreamException {
        return this._reader.getTextCharacters(sourceStart, target, targetStart, length);
    }
    
    @Override
    public String getAttributeValue(final String namespaceUri, final String localName) {
        return this._reader.getAttributeValue(namespaceUri, localName);
    }
    
    @Override
    public int getAttributeCount() {
        return this._reader.getAttributeCount();
    }
    
    @Override
    public String getAttributePrefix(final int index) {
        return this._reader.getAttributePrefix(index);
    }
    
    @Override
    public String getAttributeNamespace(final int index) {
        return this._reader.getAttributeNamespace(index);
    }
    
    @Override
    public String getAttributeLocalName(final int index) {
        return this._reader.getAttributeLocalName(index);
    }
    
    @Override
    public String getAttributeType(final int index) {
        return this._reader.getAttributeType(index);
    }
    
    @Override
    public String getAttributeValue(final int index) {
        return this._reader.getAttributeValue(index);
    }
    
    @Override
    public boolean isAttributeSpecified(final int index) {
        return this._reader.isAttributeSpecified(index);
    }
    
    @Override
    public int getNamespaceCount() {
        return this._reader.getNamespaceCount();
    }
    
    @Override
    public String getNamespacePrefix(final int index) {
        return this._reader.getNamespacePrefix(index);
    }
    
    @Override
    public String getNamespaceURI(final int index) {
        return this._reader.getNamespaceURI(index);
    }
    
    @Override
    public int getEventType() {
        return this._reader.getEventType();
    }
    
    @Override
    public String getText() {
        return this._reader.getText();
    }
    
    @Override
    public char[] getTextCharacters() {
        return this._reader.getTextCharacters();
    }
    
    @Override
    public int getTextStart() {
        return this._reader.getTextStart();
    }
    
    @Override
    public int getTextLength() {
        return this._reader.getTextLength();
    }
    
    @Override
    public String getEncoding() {
        return this._reader.getEncoding();
    }
    
    @Override
    public boolean hasText() {
        return this._reader.hasText();
    }
    
    @Override
    public Location getLocation() {
        return this._reader.getLocation();
    }
    
    @Override
    public QName getName() {
        return this._reader.getName();
    }
    
    @Override
    public String getLocalName() {
        return this._reader.getLocalName();
    }
    
    @Override
    public boolean hasName() {
        return this._reader.hasName();
    }
    
    @Override
    public String getNamespaceURI() {
        return this._reader.getNamespaceURI();
    }
    
    @Override
    public String getPrefix() {
        return this._reader.getPrefix();
    }
    
    @Override
    public String getVersion() {
        return this._reader.getVersion();
    }
    
    @Override
    public boolean isStandalone() {
        return this._reader.isStandalone();
    }
    
    @Override
    public boolean standaloneSet() {
        return this._reader.standaloneSet();
    }
    
    @Override
    public String getCharacterEncodingScheme() {
        return this._reader.getCharacterEncodingScheme();
    }
    
    @Override
    public String getPITarget() {
        return this._reader.getPITarget();
    }
    
    @Override
    public String getPIData() {
        return this._reader.getPIData();
    }
    
    @Override
    public Object getProperty(final String name) {
        return this._reader.getProperty(name);
    }
}
