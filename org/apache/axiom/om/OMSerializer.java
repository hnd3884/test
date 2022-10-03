package org.apache.axiom.om;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.XMLStreamReader;

public interface OMSerializer
{
    void serialize(final XMLStreamReader p0, final XMLStreamWriter p1) throws XMLStreamException;
}
