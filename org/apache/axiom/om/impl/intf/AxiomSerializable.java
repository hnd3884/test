package org.apache.axiom.om.impl.intf;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.apache.axiom.om.impl.common.serializer.push.OutputException;
import org.apache.axiom.om.OMOutputFormat;
import org.apache.axiom.om.impl.common.serializer.push.Serializer;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.om.OMSerializable;

public interface AxiomSerializable extends OMSerializable, AxiomInformationItem
{
    OMXMLParserWrapper getBuilder();
    
    void setComplete(final boolean p0);
    
    void internalSerialize(final Serializer p0, final OMOutputFormat p1, final boolean p2) throws OutputException;
    
    void close(final boolean p0);
    
    void serialize(final XMLStreamWriter p0) throws XMLStreamException;
    
    void serialize(final XMLStreamWriter p0, final boolean p1) throws XMLStreamException;
    
    void serializeAndConsume(final XMLStreamWriter p0) throws XMLStreamException;
}
