package org.apache.axiom.om.impl.intf;

import java.io.Writer;
import org.apache.axiom.om.OMOutputFormat;
import javax.xml.stream.XMLStreamException;
import java.io.OutputStream;
import org.apache.axiom.om.OMException;

public interface AxiomLeafNode extends AxiomChildNode
{
    void discard() throws OMException;
    
    void serialize(final OutputStream p0) throws XMLStreamException;
    
    void serialize(final OutputStream p0, final OMOutputFormat p1) throws XMLStreamException;
    
    void serialize(final Writer p0) throws XMLStreamException;
    
    void serialize(final Writer p0, final OMOutputFormat p1) throws XMLStreamException;
    
    void serializeAndConsume(final OutputStream p0) throws XMLStreamException;
    
    void serializeAndConsume(final OutputStream p0, final OMOutputFormat p1) throws XMLStreamException;
    
    void serializeAndConsume(final Writer p0) throws XMLStreamException;
    
    void serializeAndConsume(final Writer p0, final OMOutputFormat p1) throws XMLStreamException;
    
    void setComplete(final boolean p0);
}
