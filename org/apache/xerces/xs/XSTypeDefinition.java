package org.apache.xerces.xs;

public interface XSTypeDefinition extends XSObject
{
    public static final short COMPLEX_TYPE = 15;
    public static final short SIMPLE_TYPE = 16;
    
    short getTypeCategory();
    
    XSTypeDefinition getBaseType();
    
    boolean isFinal(final short p0);
    
    short getFinal();
    
    boolean getAnonymous();
    
    boolean derivedFromType(final XSTypeDefinition p0, final short p1);
    
    boolean derivedFrom(final String p0, final String p1, final short p2);
    
    XSObject getContext();
}
