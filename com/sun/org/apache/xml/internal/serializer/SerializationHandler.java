package com.sun.org.apache.xml.internal.serializer;

import javax.xml.transform.Transformer;
import org.xml.sax.SAXException;
import java.io.IOException;
import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.ext.DeclHandler;

public interface SerializationHandler extends ExtendedContentHandler, ExtendedLexicalHandler, XSLOutputAttributes, DeclHandler, DTDHandler, ErrorHandler, DOMSerializer, Serializer
{
    void setContentHandler(final ContentHandler p0);
    
    void close();
    
    void serialize(final Node p0) throws IOException;
    
    boolean setEscaping(final boolean p0) throws SAXException;
    
    void setIndentAmount(final int p0);
    
    void setTransformer(final Transformer p0);
    
    Transformer getTransformer();
    
    void setNamespaceMappings(final NamespaceMappings p0);
    
    void flushPending() throws SAXException;
    
    void setDTDEntityExpansion(final boolean p0);
    
    void setIsStandalone(final boolean p0);
}
