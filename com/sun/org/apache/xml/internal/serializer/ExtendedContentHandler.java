package com.sun.org.apache.xml.internal.serializer;

import javax.xml.transform.SourceLocator;
import org.w3c.dom.Node;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.ContentHandler;

interface ExtendedContentHandler extends ContentHandler
{
    public static final int NO_BAD_CHARS = 1;
    public static final int HTML_ATTREMPTY = 2;
    public static final int HTML_ATTRURL = 4;
    
    void addAttribute(final String p0, final String p1, final String p2, final String p3, final String p4, final boolean p5) throws SAXException;
    
    void addAttributes(final Attributes p0) throws SAXException;
    
    void addAttribute(final String p0, final String p1);
    
    void characters(final String p0) throws SAXException;
    
    void characters(final Node p0) throws SAXException;
    
    void endElement(final String p0) throws SAXException;
    
    void startElement(final String p0, final String p1, final String p2) throws SAXException;
    
    void startElement(final String p0) throws SAXException;
    
    void namespaceAfterStartElement(final String p0, final String p1) throws SAXException;
    
    boolean startPrefixMapping(final String p0, final String p1, final boolean p2) throws SAXException;
    
    void entityReference(final String p0) throws SAXException;
    
    NamespaceMappings getNamespaceMappings();
    
    String getPrefix(final String p0);
    
    String getNamespaceURI(final String p0, final boolean p1);
    
    String getNamespaceURIFromPrefix(final String p0);
    
    void setSourceLocator(final SourceLocator p0);
    
    void addUniqueAttribute(final String p0, final String p1, final int p2) throws SAXException;
    
    void addXSLAttribute(final String p0, final String p1, final String p2);
    
    void addAttribute(final String p0, final String p1, final String p2, final String p3, final String p4) throws SAXException;
}
