package com.sun.xml.internal.org.jvnet.fastinfoset.sax;

import org.xml.sax.SAXException;

public interface RestrictedAlphabetContentHandler
{
    void numericCharacters(final char[] p0, final int p1, final int p2) throws SAXException;
    
    void dateTimeCharacters(final char[] p0, final int p1, final int p2) throws SAXException;
    
    void alphabetCharacters(final String p0, final char[] p1, final int p2, final int p3) throws SAXException;
}
