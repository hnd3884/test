package com.sun.xml.internal.stream;

import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamReader;

public class StaxXMLInputSource
{
    XMLStreamReader fStreamReader;
    XMLEventReader fEventReader;
    XMLInputSource fInputSource;
    boolean fHasResolver;
    
    public StaxXMLInputSource(final XMLStreamReader streamReader) {
        this.fHasResolver = false;
        this.fStreamReader = streamReader;
    }
    
    public StaxXMLInputSource(final XMLEventReader eventReader) {
        this.fHasResolver = false;
        this.fEventReader = eventReader;
    }
    
    public StaxXMLInputSource(final XMLInputSource inputSource) {
        this.fHasResolver = false;
        this.fInputSource = inputSource;
    }
    
    public StaxXMLInputSource(final XMLInputSource inputSource, final boolean hasResolver) {
        this.fHasResolver = false;
        this.fInputSource = inputSource;
        this.fHasResolver = hasResolver;
    }
    
    public XMLStreamReader getXMLStreamReader() {
        return this.fStreamReader;
    }
    
    public XMLEventReader getXMLEventReader() {
        return this.fEventReader;
    }
    
    public XMLInputSource getXMLInputSource() {
        return this.fInputSource;
    }
    
    public boolean hasXMLStreamOrXMLEventReader() {
        return this.fStreamReader != null || this.fEventReader != null;
    }
    
    public boolean hasResolver() {
        return this.fHasResolver;
    }
}
