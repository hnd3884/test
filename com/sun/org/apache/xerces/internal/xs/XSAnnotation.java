package com.sun.org.apache.xerces.internal.xs;

public interface XSAnnotation extends XSObject
{
    public static final short W3C_DOM_ELEMENT = 1;
    public static final short SAX_CONTENTHANDLER = 2;
    public static final short W3C_DOM_DOCUMENT = 3;
    
    boolean writeAnnotation(final Object p0, final short p1);
    
    String getAnnotationString();
}
