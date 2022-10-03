package org.apache.xmlbeans.xml.stream;

public interface XMLInputStream
{
    XMLEvent next() throws XMLStreamException;
    
    boolean hasNext() throws XMLStreamException;
    
    void skip() throws XMLStreamException;
    
    void skipElement() throws XMLStreamException;
    
    XMLEvent peek() throws XMLStreamException;
    
    boolean skip(final int p0) throws XMLStreamException;
    
    boolean skip(final XMLName p0) throws XMLStreamException;
    
    boolean skip(final XMLName p0, final int p1) throws XMLStreamException;
    
    XMLInputStream getSubStream() throws XMLStreamException;
    
    void close() throws XMLStreamException;
    
    ReferenceResolver getReferenceResolver();
    
    void setReferenceResolver(final ReferenceResolver p0);
}
