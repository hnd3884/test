package org.apache.xerces.xs;

public interface XSIDCDefinition extends XSObject
{
    public static final short IC_KEY = 1;
    public static final short IC_KEYREF = 2;
    public static final short IC_UNIQUE = 3;
    
    short getCategory();
    
    String getSelectorStr();
    
    StringList getFieldStrs();
    
    XSIDCDefinition getRefKey();
    
    XSObjectList getAnnotations();
}
