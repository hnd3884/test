package org.apache.xerces.dom3.as;

public interface ASObject
{
    public static final short AS_ELEMENT_DECLARATION = 1;
    public static final short AS_ATTRIBUTE_DECLARATION = 2;
    public static final short AS_NOTATION_DECLARATION = 3;
    public static final short AS_ENTITY_DECLARATION = 4;
    public static final short AS_CONTENTMODEL = 5;
    public static final short AS_MODEL = 6;
    
    short getAsNodeType();
    
    ASModel getOwnerASModel();
    
    void setOwnerASModel(final ASModel p0);
    
    String getNodeName();
    
    void setNodeName(final String p0);
    
    String getPrefix();
    
    void setPrefix(final String p0);
    
    String getLocalName();
    
    void setLocalName(final String p0);
    
    String getNamespaceURI();
    
    void setNamespaceURI(final String p0);
    
    ASObject cloneASObject(final boolean p0);
}
