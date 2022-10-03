package org.apache.xerces.dom3.as;

public interface ASContentModel extends ASObject
{
    public static final int AS_UNBOUNDED = Integer.MAX_VALUE;
    public static final short AS_SEQUENCE = 0;
    public static final short AS_CHOICE = 1;
    public static final short AS_ALL = 2;
    public static final short AS_NONE = 3;
    
    short getListOperator();
    
    void setListOperator(final short p0);
    
    int getMinOccurs();
    
    void setMinOccurs(final int p0);
    
    int getMaxOccurs();
    
    void setMaxOccurs(final int p0);
    
    ASObjectList getSubModels();
    
    void setSubModels(final ASObjectList p0);
    
    void removesubModel(final ASObject p0);
    
    void insertsubModel(final ASObject p0) throws DOMASException;
    
    int appendsubModel(final ASObject p0) throws DOMASException;
}
