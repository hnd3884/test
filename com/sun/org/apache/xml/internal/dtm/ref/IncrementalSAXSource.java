package com.sun.org.apache.xml.internal.dtm.ref;

import org.xml.sax.SAXException;
import org.xml.sax.InputSource;
import org.xml.sax.DTDHandler;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.ContentHandler;

public interface IncrementalSAXSource
{
    void setContentHandler(final ContentHandler p0);
    
    void setLexicalHandler(final LexicalHandler p0);
    
    void setDTDHandler(final DTDHandler p0);
    
    Object deliverMoreNodes(final boolean p0);
    
    void startParse(final InputSource p0) throws SAXException;
}
