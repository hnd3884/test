package com.sun.xml.internal.org.jvnet.fastinfoset.sax;

import com.sun.xml.internal.org.jvnet.fastinfoset.FastInfosetSerializer;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.ContentHandler;

public interface FastInfosetWriter extends ContentHandler, LexicalHandler, EncodingAlgorithmContentHandler, PrimitiveTypeContentHandler, RestrictedAlphabetContentHandler, ExtendedContentHandler, FastInfosetSerializer
{
}
