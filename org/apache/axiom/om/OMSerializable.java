package org.apache.axiom.om;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public interface OMSerializable extends OMInformationItem
{
    boolean isComplete();
    
    void build();
    
    void close(final boolean p0);
    
    void serialize(final XMLStreamWriter p0) throws XMLStreamException;
    
    void serializeAndConsume(final XMLStreamWriter p0) throws XMLStreamException;
    
    void serialize(final XMLStreamWriter p0, final boolean p1) throws XMLStreamException;
}
