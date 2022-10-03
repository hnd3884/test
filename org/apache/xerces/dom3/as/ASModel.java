package org.apache.xerces.dom3.as;

import org.w3c.dom.DOMException;

public interface ASModel extends ASObject
{
    boolean getIsNamespaceAware();
    
    short getUsageLocation();
    
    String getAsLocation();
    
    void setAsLocation(final String p0);
    
    String getAsHint();
    
    void setAsHint(final String p0);
    
    ASNamedObjectMap getElementDeclarations();
    
    ASNamedObjectMap getAttributeDeclarations();
    
    ASNamedObjectMap getNotationDeclarations();
    
    ASNamedObjectMap getEntityDeclarations();
    
    ASNamedObjectMap getContentModelDeclarations();
    
    void addASModel(final ASModel p0);
    
    ASObjectList getASModels();
    
    void removeAS(final ASModel p0);
    
    boolean validate();
    
    ASElementDeclaration createASElementDeclaration(final String p0, final String p1) throws DOMException;
    
    ASAttributeDeclaration createASAttributeDeclaration(final String p0, final String p1) throws DOMException;
    
    ASNotationDeclaration createASNotationDeclaration(final String p0, final String p1, final String p2, final String p3) throws DOMException;
    
    ASEntityDeclaration createASEntityDeclaration(final String p0) throws DOMException;
    
    ASContentModel createASContentModel(final int p0, final int p1, final short p2) throws DOMASException;
}
