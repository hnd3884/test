package org.apache.axiom.om;

import java.io.OutputStream;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.io.Writer;
import java.io.Reader;
import javax.xml.namespace.QName;
import javax.xml.namespace.NamespaceContext;
import java.util.Iterator;

public interface OMElement extends OMNode, OMContainer, OMNamedInformationItem
{
    Iterator getChildElements();
    
    OMNamespace declareNamespace(final String p0, final String p1);
    
    OMNamespace declareDefaultNamespace(final String p0);
    
    OMNamespace getDefaultNamespace();
    
    OMNamespace declareNamespace(final OMNamespace p0);
    
    void undeclarePrefix(final String p0);
    
    OMNamespace findNamespace(final String p0, final String p1);
    
    OMNamespace findNamespaceURI(final String p0);
    
    Iterator getAllDeclaredNamespaces();
    
    Iterator getNamespacesInScope();
    
    NamespaceContext getNamespaceContext(final boolean p0);
    
    Iterator getAllAttributes();
    
    OMAttribute getAttribute(final QName p0);
    
    String getAttributeValue(final QName p0);
    
    OMAttribute addAttribute(final OMAttribute p0);
    
    OMAttribute addAttribute(final String p0, final String p1, final OMNamespace p2);
    
    void removeAttribute(final OMAttribute p0);
    
    OMElement getFirstElement();
    
    void setText(final String p0);
    
    void setText(final QName p0);
    
    String getText();
    
    Reader getTextAsStream(final boolean p0);
    
    void writeTextTo(final Writer p0, final boolean p1) throws IOException;
    
    QName getTextAsQName();
    
    void setNamespace(final OMNamespace p0);
    
    @Deprecated
    void setNamespaceWithNoFindInCurrentScope(final OMNamespace p0);
    
    String toString();
    
    String toStringWithConsume() throws XMLStreamException;
    
    QName resolveQName(final String p0);
    
    OMElement cloneOMElement();
    
    void setLineNumber(final int p0);
    
    int getLineNumber();
    
    void serialize(final OutputStream p0) throws XMLStreamException;
    
    void serialize(final Writer p0) throws XMLStreamException;
    
    void serialize(final OutputStream p0, final OMOutputFormat p1) throws XMLStreamException;
    
    void serialize(final Writer p0, final OMOutputFormat p1) throws XMLStreamException;
    
    void serializeAndConsume(final OutputStream p0) throws XMLStreamException;
    
    void serializeAndConsume(final Writer p0) throws XMLStreamException;
    
    void serializeAndConsume(final OutputStream p0, final OMOutputFormat p1) throws XMLStreamException;
    
    void serializeAndConsume(final Writer p0, final OMOutputFormat p1) throws XMLStreamException;
}
