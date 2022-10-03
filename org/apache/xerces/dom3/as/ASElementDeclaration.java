package org.apache.xerces.dom3.as;

public interface ASElementDeclaration extends ASObject
{
    public static final short EMPTY_CONTENTTYPE = 1;
    public static final short ANY_CONTENTTYPE = 2;
    public static final short MIXED_CONTENTTYPE = 3;
    public static final short ELEMENTS_CONTENTTYPE = 4;
    
    boolean getStrictMixedContent();
    
    void setStrictMixedContent(final boolean p0);
    
    ASDataType getElementType();
    
    void setElementType(final ASDataType p0);
    
    boolean getIsPCDataOnly();
    
    void setIsPCDataOnly(final boolean p0);
    
    short getContentType();
    
    void setContentType(final short p0);
    
    String getSystemId();
    
    void setSystemId(final String p0);
    
    ASContentModel getAsCM();
    
    void setAsCM(final ASContentModel p0);
    
    ASNamedObjectMap getASAttributeDecls();
    
    void setASAttributeDecls(final ASNamedObjectMap p0);
    
    void addASAttributeDecl(final ASAttributeDeclaration p0);
    
    ASAttributeDeclaration removeASAttributeDecl(final ASAttributeDeclaration p0);
}
