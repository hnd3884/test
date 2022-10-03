package org.apache.axiom.om.impl.llom;

import org.apache.axiom.core.CoreNode;
import org.apache.axiom.core.ClonePolicy;
import org.apache.axiom.core.CoreNamespaceDeclaration;
import org.apache.axiom.core.CoreNamespaceDeclarationSupport;
import org.apache.axiom.core.NodeType;
import org.apache.axiom.om.impl.common.AxiomNamespaceDeclarationSupport;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.impl.intf.AxiomNamespaceDeclaration;

public final class NamespaceDeclaration extends Attribute implements AxiomNamespaceDeclaration
{
    public OMNamespace declaredNamespace;
    
    public NamespaceDeclaration() {
        AxiomNamespaceDeclarationSupport.ajc$interFieldInit$org_apache_axiom_om_impl_common_AxiomNamespaceDeclarationSupport$org_apache_axiom_om_impl_intf_AxiomNamespaceDeclaration$declaredNamespace(this);
    }
    
    public final String coreGetDeclaredPrefix() {
        return AxiomNamespaceDeclarationSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomNamespaceDeclarationSupport$org_apache_axiom_om_impl_intf_AxiomNamespaceDeclaration$coreGetDeclaredPrefix(this);
    }
    
    public final NodeType coreGetNodeType() {
        return CoreNamespaceDeclarationSupport.ajc$interMethod$org_apache_axiom_core_CoreNamespaceDeclarationSupport$org_apache_axiom_core_CoreNamespaceDeclaration$coreGetNodeType(this);
    }
    
    public final void coreSetDeclaredNamespace(final String prefix, final String namespaceURI) {
        AxiomNamespaceDeclarationSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomNamespaceDeclarationSupport$org_apache_axiom_om_impl_intf_AxiomNamespaceDeclaration$coreSetDeclaredNamespace(this, prefix, namespaceURI);
    }
    
    public final OMNamespace getDeclaredNamespace() {
        return AxiomNamespaceDeclarationSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomNamespaceDeclarationSupport$org_apache_axiom_om_impl_intf_AxiomNamespaceDeclaration$getDeclaredNamespace(this);
    }
    
    public final <T> void init(final ClonePolicy<T> policy, final T options, final CoreNode other) {
        CoreNamespaceDeclarationSupport.ajc$interMethod$org_apache_axiom_core_CoreNamespaceDeclarationSupport$org_apache_axiom_core_CoreNamespaceDeclaration$init(this, policy, options, other);
    }
    
    public final void setDeclaredNamespace(final OMNamespace declaredNamespace) {
        AxiomNamespaceDeclarationSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomNamespaceDeclarationSupport$org_apache_axiom_om_impl_intf_AxiomNamespaceDeclaration$setDeclaredNamespace(this, declaredNamespace);
    }
}
