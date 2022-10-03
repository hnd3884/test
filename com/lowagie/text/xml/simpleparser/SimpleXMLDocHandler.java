package com.lowagie.text.xml.simpleparser;

import java.util.HashMap;

public interface SimpleXMLDocHandler
{
    void startElement(final String p0, final HashMap p1);
    
    void endElement(final String p0);
    
    void startDocument();
    
    void endDocument();
    
    void text(final String p0);
}
