package jdk.internal.util.xml;

public interface XMLStreamWriter
{
    public static final String DEFAULT_XML_VERSION = "1.0";
    public static final String DEFAULT_ENCODING = "UTF-8";
    
    void writeStartElement(final String p0) throws XMLStreamException;
    
    void writeEmptyElement(final String p0) throws XMLStreamException;
    
    void writeEndElement() throws XMLStreamException;
    
    void writeEndDocument() throws XMLStreamException;
    
    void close() throws XMLStreamException;
    
    void flush() throws XMLStreamException;
    
    void writeAttribute(final String p0, final String p1) throws XMLStreamException;
    
    void writeCData(final String p0) throws XMLStreamException;
    
    void writeDTD(final String p0) throws XMLStreamException;
    
    void writeStartDocument() throws XMLStreamException;
    
    void writeStartDocument(final String p0) throws XMLStreamException;
    
    void writeStartDocument(final String p0, final String p1) throws XMLStreamException;
    
    void writeCharacters(final String p0) throws XMLStreamException;
    
    void writeCharacters(final char[] p0, final int p1, final int p2) throws XMLStreamException;
}
