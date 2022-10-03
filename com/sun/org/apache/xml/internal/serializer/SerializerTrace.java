package com.sun.org.apache.xml.internal.serializer;

import org.xml.sax.Attributes;

public interface SerializerTrace
{
    public static final int EVENTTYPE_STARTDOCUMENT = 1;
    public static final int EVENTTYPE_ENDDOCUMENT = 2;
    public static final int EVENTTYPE_STARTELEMENT = 3;
    public static final int EVENTTYPE_ENDELEMENT = 4;
    public static final int EVENTTYPE_CHARACTERS = 5;
    public static final int EVENTTYPE_IGNORABLEWHITESPACE = 6;
    public static final int EVENTTYPE_PI = 7;
    public static final int EVENTTYPE_COMMENT = 8;
    public static final int EVENTTYPE_ENTITYREF = 9;
    public static final int EVENTTYPE_CDATA = 10;
    public static final int EVENTTYPE_OUTPUT_PSEUDO_CHARACTERS = 11;
    public static final int EVENTTYPE_OUTPUT_CHARACTERS = 12;
    
    boolean hasTraceListeners();
    
    void fireGenerateEvent(final int p0);
    
    void fireGenerateEvent(final int p0, final String p1, final Attributes p2);
    
    void fireGenerateEvent(final int p0, final char[] p1, final int p2, final int p3);
    
    void fireGenerateEvent(final int p0, final String p1, final String p2);
    
    void fireGenerateEvent(final int p0, final String p1);
}
