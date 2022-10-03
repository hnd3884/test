package com.sun.org.apache.xml.internal.dtm.ref;

import org.xml.sax.XMLReader;
import org.xml.sax.InputSource;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.ContentHandler;

public interface CoroutineParser
{
    int getParserCoroutineID();
    
    CoroutineManager getCoroutineManager();
    
    void setContentHandler(final ContentHandler p0);
    
    void setLexHandler(final LexicalHandler p0);
    
    Object doParse(final InputSource p0, final int p1);
    
    Object doMore(final boolean p0, final int p1);
    
    void doTerminate(final int p0);
    
    void init(final CoroutineManager p0, final int p1, final XMLReader p2);
}
