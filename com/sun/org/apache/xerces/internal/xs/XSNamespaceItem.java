package com.sun.org.apache.xerces.internal.xs;

public interface XSNamespaceItem
{
    String getSchemaNamespace();
    
    XSNamedMap getComponents(final short p0);
    
    XSObjectList getAnnotations();
    
    XSElementDeclaration getElementDeclaration(final String p0);
    
    XSAttributeDeclaration getAttributeDeclaration(final String p0);
    
    XSTypeDefinition getTypeDefinition(final String p0);
    
    XSAttributeGroupDefinition getAttributeGroup(final String p0);
    
    XSModelGroupDefinition getModelGroupDefinition(final String p0);
    
    XSNotationDeclaration getNotationDeclaration(final String p0);
    
    StringList getDocumentLocations();
}
