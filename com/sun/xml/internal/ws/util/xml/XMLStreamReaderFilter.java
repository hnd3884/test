package com.sun.xml.internal.ws.util.xml;

import javax.xml.stream.Location;
import javax.xml.namespace.QName;
import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import com.sun.xml.internal.ws.api.streaming.XMLStreamReaderFactory;

public class XMLStreamReaderFilter implements XMLStreamReaderFactory.RecycleAware, XMLStreamReader
{
    protected XMLStreamReader reader;
    
    public XMLStreamReaderFilter(final XMLStreamReader core) {
        this.reader = core;
    }
    
    @Override
    public void onRecycled() {
        XMLStreamReaderFactory.recycle(this.reader);
        this.reader = null;
    }
    
    @Override
    public int getAttributeCount() {
        return this.reader.getAttributeCount();
    }
    
    @Override
    public int getEventType() {
        return this.reader.getEventType();
    }
    
    @Override
    public int getNamespaceCount() {
        return this.reader.getNamespaceCount();
    }
    
    @Override
    public int getTextLength() {
        return this.reader.getTextLength();
    }
    
    @Override
    public int getTextStart() {
        return this.reader.getTextStart();
    }
    
    @Override
    public int next() throws XMLStreamException {
        return this.reader.next();
    }
    
    @Override
    public int nextTag() throws XMLStreamException {
        return this.reader.nextTag();
    }
    
    @Override
    public void close() throws XMLStreamException {
        this.reader.close();
    }
    
    @Override
    public boolean hasName() {
        return this.reader.hasName();
    }
    
    @Override
    public boolean hasNext() throws XMLStreamException {
        return this.reader.hasNext();
    }
    
    @Override
    public boolean hasText() {
        return this.reader.hasText();
    }
    
    @Override
    public boolean isCharacters() {
        return this.reader.isCharacters();
    }
    
    @Override
    public boolean isEndElement() {
        return this.reader.isEndElement();
    }
    
    @Override
    public boolean isStandalone() {
        return this.reader.isStandalone();
    }
    
    @Override
    public boolean isStartElement() {
        return this.reader.isStartElement();
    }
    
    @Override
    public boolean isWhiteSpace() {
        return this.reader.isWhiteSpace();
    }
    
    @Override
    public boolean standaloneSet() {
        return this.reader.standaloneSet();
    }
    
    @Override
    public char[] getTextCharacters() {
        return this.reader.getTextCharacters();
    }
    
    @Override
    public boolean isAttributeSpecified(final int index) {
        return this.reader.isAttributeSpecified(index);
    }
    
    @Override
    public int getTextCharacters(final int sourceStart, final char[] target, final int targetStart, final int length) throws XMLStreamException {
        return this.reader.getTextCharacters(sourceStart, target, targetStart, length);
    }
    
    @Override
    public String getCharacterEncodingScheme() {
        return this.reader.getCharacterEncodingScheme();
    }
    
    @Override
    public String getElementText() throws XMLStreamException {
        return this.reader.getElementText();
    }
    
    @Override
    public String getEncoding() {
        return this.reader.getEncoding();
    }
    
    @Override
    public String getLocalName() {
        return this.reader.getLocalName();
    }
    
    @Override
    public String getNamespaceURI() {
        return this.reader.getNamespaceURI();
    }
    
    @Override
    public String getPIData() {
        return this.reader.getPIData();
    }
    
    @Override
    public String getPITarget() {
        return this.reader.getPITarget();
    }
    
    @Override
    public String getPrefix() {
        return this.reader.getPrefix();
    }
    
    @Override
    public String getText() {
        return this.reader.getText();
    }
    
    @Override
    public String getVersion() {
        return this.reader.getVersion();
    }
    
    @Override
    public String getAttributeLocalName(final int index) {
        return this.reader.getAttributeLocalName(index);
    }
    
    @Override
    public String getAttributeNamespace(final int index) {
        return this.reader.getAttributeNamespace(index);
    }
    
    @Override
    public String getAttributePrefix(final int index) {
        return this.reader.getAttributePrefix(index);
    }
    
    @Override
    public String getAttributeType(final int index) {
        return this.reader.getAttributeType(index);
    }
    
    @Override
    public String getAttributeValue(final int index) {
        return this.reader.getAttributeValue(index);
    }
    
    @Override
    public String getNamespacePrefix(final int index) {
        return this.reader.getNamespacePrefix(index);
    }
    
    @Override
    public String getNamespaceURI(final int index) {
        return this.reader.getNamespaceURI(index);
    }
    
    @Override
    public NamespaceContext getNamespaceContext() {
        return this.reader.getNamespaceContext();
    }
    
    @Override
    public QName getName() {
        return this.reader.getName();
    }
    
    @Override
    public QName getAttributeName(final int index) {
        return this.reader.getAttributeName(index);
    }
    
    @Override
    public Location getLocation() {
        return this.reader.getLocation();
    }
    
    @Override
    public Object getProperty(final String name) throws IllegalArgumentException {
        return this.reader.getProperty(name);
    }
    
    @Override
    public void require(final int type, final String namespaceURI, final String localName) throws XMLStreamException {
        this.reader.require(type, namespaceURI, localName);
    }
    
    @Override
    public String getNamespaceURI(final String prefix) {
        return this.reader.getNamespaceURI(prefix);
    }
    
    @Override
    public String getAttributeValue(final String namespaceURI, final String localName) {
        return this.reader.getAttributeValue(namespaceURI, localName);
    }
}
