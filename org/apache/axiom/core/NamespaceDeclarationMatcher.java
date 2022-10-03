package org.apache.axiom.core;

public final class NamespaceDeclarationMatcher implements AttributeMatcher
{
    private final Semantics semantics;
    
    public NamespaceDeclarationMatcher(final Semantics semantics) {
        this.semantics = semantics;
    }
    
    public boolean matches(final CoreAttribute attr, final String namespaceURI, final String name) {
        if (attr instanceof CoreNamespaceDeclaration) {
            final String prefix = ((CoreNamespaceDeclaration)attr).coreGetDeclaredPrefix();
            return name.equals(prefix);
        }
        return false;
    }
    
    public CoreAttribute createAttribute(final CoreElement element, final String namespaceURI, final String name, final String prefix, final String value) {
        final CoreNamespaceDeclaration decl = CoreNodeSupport.ajc$interMethodDispatch1$org_apache_axiom_core_CoreNodeSupport$org_apache_axiom_core_CoreNode$coreCreateNode(element, CoreNamespaceDeclaration.class);
        decl.coreSetDeclaredNamespace(name, value);
        return decl;
    }
    
    public String getNamespaceURI(final CoreAttribute attr) {
        return null;
    }
    
    public String getName(final CoreAttribute attr) {
        return ((CoreNamespaceDeclaration)attr).coreGetDeclaredPrefix();
    }
    
    public void update(final CoreAttribute attr, final String prefix, final String value) {
        CoreParentNodeSupport.ajc$interMethodDispatch1$org_apache_axiom_core_CoreParentNodeSupport$org_apache_axiom_core_CoreParentNode$coreSetCharacterData(attr, value, this.semantics);
    }
}
