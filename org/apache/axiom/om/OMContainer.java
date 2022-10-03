package org.apache.axiom.om;

import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXSource;
import javax.xml.stream.XMLStreamReader;
import java.io.Writer;
import javax.xml.stream.XMLStreamException;
import java.io.OutputStream;
import java.util.Iterator;
import javax.xml.namespace.QName;

public interface OMContainer extends OMSerializable
{
    OMXMLParserWrapper getBuilder();
    
    void addChild(final OMNode p0);
    
    Iterator getChildrenWithName(final QName p0);
    
    Iterator getChildrenWithLocalName(final String p0);
    
    Iterator getChildrenWithNamespaceURI(final String p0);
    
    OMElement getFirstChildWithName(final QName p0) throws OMException;
    
    Iterator getChildren();
    
    Iterator getDescendants(final boolean p0);
    
    OMNode getFirstOMChild();
    
    void removeChildren();
    
    void serialize(final OutputStream p0) throws XMLStreamException;
    
    void serialize(final Writer p0) throws XMLStreamException;
    
    void serialize(final OutputStream p0, final OMOutputFormat p1) throws XMLStreamException;
    
    void serialize(final Writer p0, final OMOutputFormat p1) throws XMLStreamException;
    
    void serializeAndConsume(final OutputStream p0) throws XMLStreamException;
    
    void serializeAndConsume(final Writer p0) throws XMLStreamException;
    
    void serializeAndConsume(final OutputStream p0, final OMOutputFormat p1) throws XMLStreamException;
    
    void serializeAndConsume(final Writer p0, final OMOutputFormat p1) throws XMLStreamException;
    
    XMLStreamReader getXMLStreamReader();
    
    XMLStreamReader getXMLStreamReaderWithoutCaching();
    
    XMLStreamReader getXMLStreamReader(final boolean p0);
    
    XMLStreamReader getXMLStreamReader(final boolean p0, final OMXMLStreamReaderConfiguration p1);
    
    SAXSource getSAXSource(final boolean p0);
    
    SAXResult getSAXResult();
}
