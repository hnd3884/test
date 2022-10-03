package org.htmlparser;

import org.htmlparser.scanners.Scanner;
import java.util.Vector;

public interface Tag extends Node
{
    String getAttribute(final String p0);
    
    void setAttribute(final String p0, final String p1);
    
    void setAttribute(final String p0, final String p1, final char p2);
    
    void removeAttribute(final String p0);
    
    Attribute getAttributeEx(final String p0);
    
    void setAttributeEx(final Attribute p0);
    
    Vector getAttributesEx();
    
    void setAttributesEx(final Vector p0);
    
    String getTagName();
    
    void setTagName(final String p0);
    
    String getRawTagName();
    
    boolean breaksFlow();
    
    boolean isEndTag();
    
    boolean isEmptyXmlTag();
    
    void setEmptyXmlTag(final boolean p0);
    
    String[] getIds();
    
    String[] getEnders();
    
    String[] getEndTagEnders();
    
    Tag getEndTag();
    
    void setEndTag(final Tag p0);
    
    Scanner getThisScanner();
    
    void setThisScanner(final Scanner p0);
    
    int getStartingLineNumber();
    
    int getEndingLineNumber();
}
