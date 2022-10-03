package org.apache.xerces.dom3.as;

public interface ASAttributeDeclaration extends ASObject
{
    public static final short VALUE_NONE = 0;
    public static final short VALUE_DEFAULT = 1;
    public static final short VALUE_FIXED = 2;
    
    ASDataType getDataType();
    
    void setDataType(final ASDataType p0);
    
    String getDataValue();
    
    void setDataValue(final String p0);
    
    String getEnumAttr();
    
    void setEnumAttr(final String p0);
    
    ASObjectList getOwnerElements();
    
    void setOwnerElements(final ASObjectList p0);
    
    short getDefaultType();
    
    void setDefaultType(final short p0);
}
