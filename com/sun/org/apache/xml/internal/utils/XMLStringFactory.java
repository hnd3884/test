package com.sun.org.apache.xml.internal.utils;

public abstract class XMLStringFactory
{
    public abstract XMLString newstr(final String p0);
    
    public abstract XMLString newstr(final FastStringBuffer p0, final int p1, final int p2);
    
    public abstract XMLString newstr(final char[] p0, final int p1, final int p2);
    
    public abstract XMLString emptystr();
}
