package org.apache.xmlbeans.xml.stream;

public interface ReferenceResolver
{
    @Deprecated
    XMLInputStream resolve(final String p0) throws XMLStreamException;
    
    String getId(final String p0);
}
