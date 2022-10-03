package org.apache.axiom.om.impl.intf;

import java.io.IOException;
import java.io.Writer;
import javax.xml.stream.XMLStreamException;
import java.io.Reader;
import javax.xml.namespace.NamespaceContext;
import java.util.Iterator;
import org.apache.axiom.om.impl.common.serializer.push.OutputException;
import org.apache.axiom.om.OMOutputFormat;
import org.apache.axiom.om.impl.common.serializer.push.Serializer;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMNamespace;
import javax.xml.namespace.QName;
import org.apache.axiom.core.DeferringParentNode;
import org.apache.axiom.core.CoreNSAwareElement;
import org.apache.axiom.om.impl.OMElementEx;

public interface AxiomElement extends OMElementEx, AxiomContainer, AxiomChildNode, AxiomNamedInformationItem, CoreNSAwareElement, DeferringParentNode
{
    void _setAttributeValue(final QName p0, final String p1);
    
    OMAttribute addAttribute(final String p0, final String p1, final OMNamespace p2);
    
    OMAttribute addAttribute(final OMAttribute p0);
    
    OMNamespace addNamespaceDeclaration(final String p0, final String p1);
    
    void addNamespaceDeclaration(final OMNamespace p0);
    
    void buildWithAttachments();
    
    void checkChild(final OMNode p0);
    
    OMElement cloneOMElement();
    
    OMNamespace declareDefaultNamespace(final String p0);
    
    OMNamespace declareNamespace(final String p0, final String p1);
    
    OMNamespace declareNamespace(final OMNamespace p0);
    
    void defaultInternalSerialize(final Serializer p0, final OMOutputFormat p1, final boolean p2) throws OutputException;
    
    void detachAndDiscardParent();
    
    void discard();
    
    OMNamespace findNamespace(final String p0, final String p1);
    
    OMNamespace findNamespaceURI(final String p0);
    
    Iterator getAllAttributes();
    
    Iterator getAllDeclaredNamespaces();
    
    OMAttribute getAttribute(final QName p0);
    
    String getAttributeValue(final QName p0);
    
    Iterator getChildElements();
    
    OMNamespace getDefaultNamespace();
    
    OMElement getFirstElement();
    
    int getLineNumber();
    
    NamespaceContext getNamespaceContext(final boolean p0);
    
    Iterator getNamespacesInScope();
    
    String getText();
    
    QName getTextAsQName();
    
    Reader getTextAsStream(final boolean p0);
    
    int getType();
    
    OMNamespace handleNamespace(final String p0, final String p1);
    
    void initName(final String p0, final OMNamespace p1, final boolean p2);
    
    void insertChild(final Class[] p0, final int p1, final OMNode p2);
    
    void internalAppendAttribute(final OMAttribute p0);
    
    void internalSerialize(final Serializer p0, final OMOutputFormat p1, final boolean p2) throws OutputException;
    
    void removeAttribute(final OMAttribute p0);
    
    QName resolveQName(final String p0);
    
    void setComplete(final boolean p0);
    
    void setLineNumber(final int p0);
    
    void setNamespace(final OMNamespace p0);
    
    void setNamespace(final OMNamespace p0, final boolean p1);
    
    void setNamespaceWithNoFindInCurrentScope(final OMNamespace p0);
    
    void setText(final String p0);
    
    void setText(final QName p0);
    
    String toString();
    
    String toStringWithConsume() throws XMLStreamException;
    
    void undeclarePrefix(final String p0);
    
    void writeTextTo(final Writer p0, final boolean p1) throws IOException;
}
