package org.apache.axiom.om;

import java.io.Writer;
import javax.xml.stream.XMLStreamException;
import java.io.OutputStream;

public interface OMNode extends OMSerializable
{
    public static final short ELEMENT_NODE = 1;
    public static final short TEXT_NODE = 4;
    public static final short CDATA_SECTION_NODE = 12;
    public static final short COMMENT_NODE = 5;
    public static final short DTD_NODE = 11;
    public static final short PI_NODE = 3;
    public static final short ENTITY_REFERENCE_NODE = 9;
    public static final short SPACE_NODE = 6;
    
    OMContainer getParent();
    
    OMNode getNextOMSibling() throws OMException;
    
    OMNode detach() throws OMException;
    
    void discard() throws OMException;
    
    void insertSiblingAfter(final OMNode p0) throws OMException;
    
    void insertSiblingBefore(final OMNode p0) throws OMException;
    
    int getType();
    
    OMNode getPreviousOMSibling();
    
    @Deprecated
    void serialize(final OutputStream p0) throws XMLStreamException;
    
    @Deprecated
    void serialize(final Writer p0) throws XMLStreamException;
    
    @Deprecated
    void serialize(final OutputStream p0, final OMOutputFormat p1) throws XMLStreamException;
    
    @Deprecated
    void serialize(final Writer p0, final OMOutputFormat p1) throws XMLStreamException;
    
    @Deprecated
    void serializeAndConsume(final OutputStream p0) throws XMLStreamException;
    
    @Deprecated
    void serializeAndConsume(final Writer p0) throws XMLStreamException;
    
    @Deprecated
    void serializeAndConsume(final OutputStream p0, final OMOutputFormat p1) throws XMLStreamException;
    
    @Deprecated
    void serializeAndConsume(final Writer p0, final OMOutputFormat p1) throws XMLStreamException;
    
    void buildWithAttachments();
}
