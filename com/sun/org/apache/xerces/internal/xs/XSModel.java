package com.sun.org.apache.xerces.internal.xs;

public interface XSModel
{
    StringList getNamespaces();
    
    XSNamespaceItemList getNamespaceItems();
    
    XSNamedMap getComponents(final short p0);
    
    XSNamedMap getComponentsByNamespace(final short p0, final String p1);
    
    XSObjectList getAnnotations();
    
    XSElementDeclaration getElementDeclaration(final String p0, final String p1);
    
    XSAttributeDeclaration getAttributeDeclaration(final String p0, final String p1);
    
    XSTypeDefinition getTypeDefinition(final String p0, final String p1);
    
    XSAttributeGroupDefinition getAttributeGroup(final String p0, final String p1);
    
    XSModelGroupDefinition getModelGroupDefinition(final String p0, final String p1);
    
    XSNotationDeclaration getNotationDeclaration(final String p0, final String p1);
    
    XSObjectList getSubstitutionGroup(final XSElementDeclaration p0);
}
