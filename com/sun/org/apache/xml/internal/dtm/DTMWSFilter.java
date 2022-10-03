package com.sun.org.apache.xml.internal.dtm;

public interface DTMWSFilter
{
    public static final short NOTSTRIP = 1;
    public static final short STRIP = 2;
    public static final short INHERIT = 3;
    
    short getShouldStripSpace(final int p0, final DTM p1);
}
