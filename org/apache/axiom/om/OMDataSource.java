package org.apache.axiom.om;

import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import java.io.Writer;
import javax.xml.stream.XMLStreamException;
import java.io.OutputStream;

public interface OMDataSource
{
    void serialize(final OutputStream p0, final OMOutputFormat p1) throws XMLStreamException;
    
    void serialize(final Writer p0, final OMOutputFormat p1) throws XMLStreamException;
    
    void serialize(final XMLStreamWriter p0) throws XMLStreamException;
    
    XMLStreamReader getReader() throws XMLStreamException;
}
