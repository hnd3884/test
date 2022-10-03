package org.apache.axiom.core;

public final class NSAwareAttributeMatcher implements AttributeMatcher
{
    private final Semantics semantics;
    private final boolean matchNSUnawareAttributes;
    private final boolean updatePrefix;
    
    public NSAwareAttributeMatcher(final Semantics semantics, final boolean matchNSUnawareAttributes, final boolean updatePrefix) {
        this.semantics = semantics;
        this.matchNSUnawareAttributes = matchNSUnawareAttributes;
        this.updatePrefix = updatePrefix;
    }
    
    public boolean matches(final CoreAttribute attr, final String namespaceURI, final String name) {
        if (attr instanceof CoreNSAwareAttribute) {
            final CoreNSAwareAttribute nsAwareAttr = (CoreNSAwareAttribute)attr;
            return name.equals(nsAwareAttr.coreGetLocalName()) && namespaceURI.equals(nsAwareAttr.coreGetNamespaceURI());
        }
        return this.matchNSUnawareAttributes && namespaceURI.length() == 0 && attr instanceof CoreNSUnawareAttribute && name.equals(CoreNSUnawareNamedNodeSupport.ajc$interMethodDispatch1$org_apache_axiom_core_CoreNSUnawareNamedNodeSupport$org_apache_axiom_core_CoreNSUnawareNamedNode$coreGetName((CoreNSUnawareNamedNode)attr));
    }
    
    public String getNamespaceURI(final CoreAttribute attr) {
        return ((CoreNSAwareAttribute)attr).coreGetNamespaceURI();
    }
    
    public String getName(final CoreAttribute attr) {
        return ((CoreNSAwareAttribute)attr).coreGetLocalName();
    }
    
    public CoreAttribute createAttribute(final CoreElement element, final String namespaceURI, final String name, final String prefix, final String value) {
        final CoreNSAwareAttribute attr = CoreNodeSupport.ajc$interMethodDispatch1$org_apache_axiom_core_CoreNodeSupport$org_apache_axiom_core_CoreNode$coreCreateNode(element, CoreNSAwareAttribute.class);
        attr.coreSetName(namespaceURI, name, prefix);
        CoreParentNodeSupport.ajc$interMethodDispatch1$org_apache_axiom_core_CoreParentNodeSupport$org_apache_axiom_core_CoreParentNode$coreSetCharacterData(attr, value, null);
        return attr;
    }
    
    public void update(final CoreAttribute attr, final String prefix, final String value) {
        CoreParentNodeSupport.ajc$interMethodDispatch1$org_apache_axiom_core_CoreParentNodeSupport$org_apache_axiom_core_CoreParentNode$coreSetCharacterData(attr, value, this.semantics);
        if (this.updatePrefix && attr instanceof CoreNSAwareAttribute) {
            ((CoreNSAwareAttribute)attr).coreSetPrefix(prefix);
        }
    }
}
