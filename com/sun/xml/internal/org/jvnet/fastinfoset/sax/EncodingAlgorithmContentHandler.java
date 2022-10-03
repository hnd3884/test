package com.sun.xml.internal.org.jvnet.fastinfoset.sax;

import org.xml.sax.SAXException;

public interface EncodingAlgorithmContentHandler
{
    void octets(final String p0, final int p1, final byte[] p2, final int p3, final int p4) throws SAXException;
    
    void object(final String p0, final int p1, final Object p2) throws SAXException;
}
