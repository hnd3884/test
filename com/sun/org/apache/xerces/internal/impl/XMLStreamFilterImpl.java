package com.sun.org.apache.xerces.internal.impl;

import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.Location;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.StreamFilter;
import javax.xml.stream.XMLStreamReader;

public class XMLStreamFilterImpl implements XMLStreamReader
{
    private StreamFilter fStreamFilter;
    private XMLStreamReader fStreamReader;
    private int fCurrentEvent;
    private boolean fEventAccepted;
    private boolean fStreamAdvancedByHasNext;
    
    public XMLStreamFilterImpl(final XMLStreamReader reader, final StreamFilter filter) {
        this.fStreamFilter = null;
        this.fStreamReader = null;
        this.fEventAccepted = false;
        this.fStreamAdvancedByHasNext = false;
        this.fStreamReader = reader;
        this.fStreamFilter = filter;
        try {
            if (this.fStreamFilter.accept(this.fStreamReader)) {
                this.fEventAccepted = true;
            }
            else {
                this.findNextEvent();
            }
        }
        catch (final XMLStreamException xs) {
            System.err.println("Error while creating a stream Filter" + xs);
        }
    }
    
    protected void setStreamFilter(final StreamFilter sf) {
        this.fStreamFilter = sf;
    }
    
    @Override
    public int next() throws XMLStreamException {
        if (this.fStreamAdvancedByHasNext && this.fEventAccepted) {
            this.fStreamAdvancedByHasNext = false;
            return this.fCurrentEvent;
        }
        final int event = this.findNextEvent();
        if (event != -1) {
            return event;
        }
        throw new IllegalStateException("The stream reader has reached the end of the document, or there are no more  items to return");
    }
    
    @Override
    public int nextTag() throws XMLStreamException {
        if (this.fStreamAdvancedByHasNext && this.fEventAccepted && (this.fCurrentEvent == 1 || this.fCurrentEvent == 1)) {
            this.fStreamAdvancedByHasNext = false;
            return this.fCurrentEvent;
        }
        final int event = this.findNextTag();
        if (event != -1) {
            return event;
        }
        throw new IllegalStateException("The stream reader has reached the end of the document, or there are no more  items to return");
    }
    
    @Override
    public boolean hasNext() throws XMLStreamException {
        if (this.fStreamReader.hasNext()) {
            if (!this.fEventAccepted) {
                if ((this.fCurrentEvent = this.findNextEvent()) == -1) {
                    return false;
                }
                this.fStreamAdvancedByHasNext = true;
            }
            return true;
        }
        return false;
    }
    
    private int findNextEvent() throws XMLStreamException {
        this.fStreamAdvancedByHasNext = false;
        while (this.fStreamReader.hasNext()) {
            this.fCurrentEvent = this.fStreamReader.next();
            if (this.fStreamFilter.accept(this.fStreamReader)) {
                this.fEventAccepted = true;
                return this.fCurrentEvent;
            }
        }
        if (this.fCurrentEvent == 8) {
            return this.fCurrentEvent;
        }
        return -1;
    }
    
    private int findNextTag() throws XMLStreamException {
        this.fStreamAdvancedByHasNext = false;
        while (this.fStreamReader.hasNext()) {
            this.fCurrentEvent = this.fStreamReader.nextTag();
            if (this.fStreamFilter.accept(this.fStreamReader)) {
                this.fEventAccepted = true;
                return this.fCurrentEvent;
            }
        }
        if (this.fCurrentEvent == 8) {
            return this.fCurrentEvent;
        }
        return -1;
    }
    
    @Override
    public void close() throws XMLStreamException {
        this.fStreamReader.close();
    }
    
    @Override
    public int getAttributeCount() {
        return this.fStreamReader.getAttributeCount();
    }
    
    @Override
    public QName getAttributeName(final int index) {
        return this.fStreamReader.getAttributeName(index);
    }
    
    @Override
    public String getAttributeNamespace(final int index) {
        return this.fStreamReader.getAttributeNamespace(index);
    }
    
    @Override
    public String getAttributePrefix(final int index) {
        return this.fStreamReader.getAttributePrefix(index);
    }
    
    @Override
    public String getAttributeType(final int index) {
        return this.fStreamReader.getAttributeType(index);
    }
    
    @Override
    public String getAttributeValue(final int index) {
        return this.fStreamReader.getAttributeValue(index);
    }
    
    @Override
    public String getAttributeValue(final String namespaceURI, final String localName) {
        return this.fStreamReader.getAttributeValue(namespaceURI, localName);
    }
    
    @Override
    public String getCharacterEncodingScheme() {
        return this.fStreamReader.getCharacterEncodingScheme();
    }
    
    @Override
    public String getElementText() throws XMLStreamException {
        return this.fStreamReader.getElementText();
    }
    
    @Override
    public String getEncoding() {
        return this.fStreamReader.getEncoding();
    }
    
    @Override
    public int getEventType() {
        return this.fStreamReader.getEventType();
    }
    
    @Override
    public String getLocalName() {
        return this.fStreamReader.getLocalName();
    }
    
    @Override
    public Location getLocation() {
        return this.fStreamReader.getLocation();
    }
    
    @Override
    public QName getName() {
        return this.fStreamReader.getName();
    }
    
    @Override
    public NamespaceContext getNamespaceContext() {
        return this.fStreamReader.getNamespaceContext();
    }
    
    @Override
    public int getNamespaceCount() {
        return this.fStreamReader.getNamespaceCount();
    }
    
    @Override
    public String getNamespacePrefix(final int index) {
        return this.fStreamReader.getNamespacePrefix(index);
    }
    
    @Override
    public String getNamespaceURI() {
        return this.fStreamReader.getNamespaceURI();
    }
    
    @Override
    public String getNamespaceURI(final int index) {
        return this.fStreamReader.getNamespaceURI(index);
    }
    
    @Override
    public String getNamespaceURI(final String prefix) {
        return this.fStreamReader.getNamespaceURI(prefix);
    }
    
    @Override
    public String getPIData() {
        return this.fStreamReader.getPIData();
    }
    
    @Override
    public String getPITarget() {
        return this.fStreamReader.getPITarget();
    }
    
    @Override
    public String getPrefix() {
        return this.fStreamReader.getPrefix();
    }
    
    @Override
    public Object getProperty(final String name) throws IllegalArgumentException {
        return this.fStreamReader.getProperty(name);
    }
    
    @Override
    public String getText() {
        return this.fStreamReader.getText();
    }
    
    @Override
    public char[] getTextCharacters() {
        return this.fStreamReader.getTextCharacters();
    }
    
    @Override
    public int getTextCharacters(final int sourceStart, final char[] target, final int targetStart, final int length) throws XMLStreamException {
        return this.fStreamReader.getTextCharacters(sourceStart, target, targetStart, length);
    }
    
    @Override
    public int getTextLength() {
        return this.fStreamReader.getTextLength();
    }
    
    @Override
    public int getTextStart() {
        return this.fStreamReader.getTextStart();
    }
    
    @Override
    public String getVersion() {
        return this.fStreamReader.getVersion();
    }
    
    @Override
    public boolean hasName() {
        return this.fStreamReader.hasName();
    }
    
    @Override
    public boolean hasText() {
        return this.fStreamReader.hasText();
    }
    
    @Override
    public boolean isAttributeSpecified(final int index) {
        return this.fStreamReader.isAttributeSpecified(index);
    }
    
    @Override
    public boolean isCharacters() {
        return this.fStreamReader.isCharacters();
    }
    
    @Override
    public boolean isEndElement() {
        return this.fStreamReader.isEndElement();
    }
    
    @Override
    public boolean isStandalone() {
        return this.fStreamReader.isStandalone();
    }
    
    @Override
    public boolean isStartElement() {
        return this.fStreamReader.isStartElement();
    }
    
    @Override
    public boolean isWhiteSpace() {
        return this.fStreamReader.isWhiteSpace();
    }
    
    @Override
    public void require(final int type, final String namespaceURI, final String localName) throws XMLStreamException {
        this.fStreamReader.require(type, namespaceURI, localName);
    }
    
    @Override
    public boolean standaloneSet() {
        return this.fStreamReader.standaloneSet();
    }
    
    @Override
    public String getAttributeLocalName(final int index) {
        return this.fStreamReader.getAttributeLocalName(index);
    }
}
