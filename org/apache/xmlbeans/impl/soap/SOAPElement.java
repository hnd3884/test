package org.apache.xmlbeans.impl.soap;

import java.util.Iterator;
import org.w3c.dom.Element;

public interface SOAPElement extends Node, Element
{
    SOAPElement addChildElement(final Name p0) throws SOAPException;
    
    SOAPElement addChildElement(final String p0) throws SOAPException;
    
    SOAPElement addChildElement(final String p0, final String p1) throws SOAPException;
    
    SOAPElement addChildElement(final String p0, final String p1, final String p2) throws SOAPException;
    
    SOAPElement addChildElement(final SOAPElement p0) throws SOAPException;
    
    SOAPElement addTextNode(final String p0) throws SOAPException;
    
    SOAPElement addAttribute(final Name p0, final String p1) throws SOAPException;
    
    SOAPElement addNamespaceDeclaration(final String p0, final String p1) throws SOAPException;
    
    String getAttributeValue(final Name p0);
    
    Iterator getAllAttributes();
    
    String getNamespaceURI(final String p0);
    
    Iterator getNamespacePrefixes();
    
    Name getElementName();
    
    boolean removeAttribute(final Name p0);
    
    boolean removeNamespaceDeclaration(final String p0);
    
    Iterator getChildElements();
    
    Iterator getChildElements(final Name p0);
    
    void setEncodingStyle(final String p0) throws SOAPException;
    
    String getEncodingStyle();
    
    void removeContents();
    
    Iterator getVisibleNamespacePrefixes();
}
