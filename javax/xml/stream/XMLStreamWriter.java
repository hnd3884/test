package javax.xml.stream;

import javax.xml.namespace.NamespaceContext;

public interface XMLStreamWriter
{
    void close() throws XMLStreamException;
    
    void flush() throws XMLStreamException;
    
    NamespaceContext getNamespaceContext();
    
    String getPrefix(final String p0) throws XMLStreamException;
    
    Object getProperty(final String p0) throws IllegalArgumentException;
    
    void setDefaultNamespace(final String p0) throws XMLStreamException;
    
    void setNamespaceContext(final NamespaceContext p0) throws XMLStreamException;
    
    void setPrefix(final String p0, final String p1) throws XMLStreamException;
    
    void writeAttribute(final String p0, final String p1) throws XMLStreamException;
    
    void writeAttribute(final String p0, final String p1, final String p2) throws XMLStreamException;
    
    void writeAttribute(final String p0, final String p1, final String p2, final String p3) throws XMLStreamException;
    
    void writeCData(final String p0) throws XMLStreamException;
    
    void writeCharacters(final char[] p0, final int p1, final int p2) throws XMLStreamException;
    
    void writeCharacters(final String p0) throws XMLStreamException;
    
    void writeComment(final String p0) throws XMLStreamException;
    
    void writeDefaultNamespace(final String p0) throws XMLStreamException;
    
    void writeDTD(final String p0) throws XMLStreamException;
    
    void writeEmptyElement(final String p0) throws XMLStreamException;
    
    void writeEmptyElement(final String p0, final String p1) throws XMLStreamException;
    
    void writeEmptyElement(final String p0, final String p1, final String p2) throws XMLStreamException;
    
    void writeEndDocument() throws XMLStreamException;
    
    void writeEndElement() throws XMLStreamException;
    
    void writeEntityRef(final String p0) throws XMLStreamException;
    
    void writeNamespace(final String p0, final String p1) throws XMLStreamException;
    
    void writeProcessingInstruction(final String p0) throws XMLStreamException;
    
    void writeProcessingInstruction(final String p0, final String p1) throws XMLStreamException;
    
    void writeStartDocument() throws XMLStreamException;
    
    void writeStartDocument(final String p0) throws XMLStreamException;
    
    void writeStartDocument(final String p0, final String p1) throws XMLStreamException;
    
    void writeStartElement(final String p0) throws XMLStreamException;
    
    void writeStartElement(final String p0, final String p1) throws XMLStreamException;
    
    void writeStartElement(final String p0, final String p1, final String p2) throws XMLStreamException;
}
