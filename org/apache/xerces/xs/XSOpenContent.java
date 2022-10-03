package org.apache.xerces.xs;

public interface XSOpenContent extends XSObject
{
    public static final short MODE_NONE = 0;
    public static final short MODE_INTERLEAVE = 1;
    public static final short MODE_SUFFIX = 2;
    
    short getModeType();
    
    XSWildcard getWildcard();
    
    boolean appliesToEmpty();
}
