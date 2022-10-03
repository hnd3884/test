package com.sun.org.apache.xml.internal.serializer;

import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;

interface ExtendedLexicalHandler extends LexicalHandler
{
    void comment(final String p0) throws SAXException;
}
