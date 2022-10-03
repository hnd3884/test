package org.apache.xerces.dom3.as;

public interface ASEntityDeclaration extends ASObject
{
    public static final short INTERNAL_ENTITY = 1;
    public static final short EXTERNAL_ENTITY = 2;
    
    short getEntityType();
    
    void setEntityType(final short p0);
    
    String getEntityValue();
    
    void setEntityValue(final String p0);
    
    String getSystemId();
    
    void setSystemId(final String p0);
    
    String getPublicId();
    
    void setPublicId(final String p0);
}
