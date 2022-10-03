package org.apache.axiom.om.impl.intf;

import java.io.Writer;
import javax.xml.stream.XMLStreamException;
import java.io.OutputStream;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.sax.SAXResult;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMElement;
import javax.xml.namespace.QName;
import java.util.Iterator;
import javax.xml.stream.XMLStreamReader;
import org.apache.axiom.om.OMXMLStreamReaderConfiguration;
import org.apache.axiom.om.impl.common.serializer.push.OutputException;
import org.apache.axiom.om.OMOutputFormat;
import org.apache.axiom.om.impl.common.serializer.push.Serializer;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.impl.OMContainerEx;

public interface AxiomContainer extends OMContainerEx, AxiomCoreParentNode
{
    void checkChild(final OMNode p0);
    
    void addChild(final OMNode p0);
    
    void addChild(final OMNode p0, final boolean p1);
    
    void build();
    
    XMLStreamReader defaultGetXMLStreamReader(final boolean p0, final OMXMLStreamReaderConfiguration p1);
    
    void discarded();
    
    Iterator getChildren();
    
    Iterator getChildrenWithLocalName(final String p0);
    
    Iterator getChildrenWithName(final QName p0);
    
    Iterator getChildrenWithNamespaceURI(final String p0);
    
    Iterator getDescendants(final boolean p0);
    
    OMElement getFirstChildWithName(final QName p0) throws OMException;
    
    OMNode getFirstOMChild();
    
    SAXResult getSAXResult();
    
    SAXSource getSAXSource(final boolean p0);
    
    XMLStreamReader getXMLStreamReader();
    
    XMLStreamReader getXMLStreamReader(final boolean p0);
    
    XMLStreamReader getXMLStreamReader(final boolean p0, final OMXMLStreamReaderConfiguration p1);
    
    XMLStreamReader getXMLStreamReaderWithoutCaching();
    
    void notifyChildComplete();
    
    AxiomChildNode prepareNewChild(final OMNode p0);
    
    void removeChildren();
    
    void serialize(final OutputStream p0) throws XMLStreamException;
    
    void serialize(final OutputStream p0, final OMOutputFormat p1) throws XMLStreamException;
    
    void serialize(final Writer p0) throws XMLStreamException;
    
    void serialize(final Writer p0, final OMOutputFormat p1) throws XMLStreamException;
    
    void serializeAndConsume(final OutputStream p0) throws XMLStreamException;
    
    void serializeAndConsume(final OutputStream p0, final OMOutputFormat p1) throws XMLStreamException;
    
    void serializeAndConsume(final Writer p0) throws XMLStreamException;
    
    void serializeAndConsume(final Writer p0, final OMOutputFormat p1) throws XMLStreamException;
}
