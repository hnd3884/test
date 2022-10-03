package com.sun.xml.internal.org.jvnet.fastinfoset.sax;

import org.xml.sax.SAXException;
import org.xml.sax.ContentHandler;

public interface ExtendedContentHandler extends ContentHandler
{
    void characters(final char[] p0, final int p1, final int p2, final boolean p3) throws SAXException;
}
