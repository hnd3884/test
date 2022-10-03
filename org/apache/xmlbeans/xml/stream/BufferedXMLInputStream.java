package org.apache.xmlbeans.xml.stream;

public interface BufferedXMLInputStream extends XMLInputStream
{
    void mark() throws XMLStreamException;
    
    void reset() throws XMLStreamException;
}
