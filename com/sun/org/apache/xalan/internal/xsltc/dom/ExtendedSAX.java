package com.sun.org.apache.xalan.internal.xsltc.dom;

import org.xml.sax.ext.DeclHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.ContentHandler;

public interface ExtendedSAX extends ContentHandler, LexicalHandler, DTDHandler, DeclHandler
{
}
