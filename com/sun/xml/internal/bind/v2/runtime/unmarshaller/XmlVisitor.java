package com.sun.xml.internal.bind.v2.runtime.unmarshaller;

import org.xml.sax.SAXException;
import javax.xml.namespace.NamespaceContext;

public interface XmlVisitor
{
    void startDocument(final LocatorEx p0, final NamespaceContext p1) throws SAXException;
    
    void endDocument() throws SAXException;
    
    void startElement(final TagName p0) throws SAXException;
    
    void endElement(final TagName p0) throws SAXException;
    
    void startPrefixMapping(final String p0, final String p1) throws SAXException;
    
    void endPrefixMapping(final String p0) throws SAXException;
    
    void text(final CharSequence p0) throws SAXException;
    
    UnmarshallingContext getContext();
    
    TextPredictor getPredictor();
    
    public interface TextPredictor
    {
        boolean expectText();
    }
}
