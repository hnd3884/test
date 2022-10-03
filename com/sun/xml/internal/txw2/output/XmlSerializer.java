package com.sun.xml.internal.txw2.output;

public interface XmlSerializer
{
    void startDocument();
    
    void beginStartTag(final String p0, final String p1, final String p2);
    
    void writeAttribute(final String p0, final String p1, final String p2, final StringBuilder p3);
    
    void writeXmlns(final String p0, final String p1);
    
    void endStartTag(final String p0, final String p1, final String p2);
    
    void endTag();
    
    void text(final StringBuilder p0);
    
    void cdata(final StringBuilder p0);
    
    void comment(final StringBuilder p0);
    
    void endDocument();
    
    void flush();
}
