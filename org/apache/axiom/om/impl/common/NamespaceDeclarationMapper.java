package org.apache.axiom.om.impl.common;

import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.impl.intf.AxiomNamespaceDeclaration;
import org.apache.axiom.core.Mapper;

public class NamespaceDeclarationMapper implements Mapper<AxiomNamespaceDeclaration, OMNamespace>
{
    public static final NamespaceDeclarationMapper INSTANCE;
    
    static {
        INSTANCE = new NamespaceDeclarationMapper();
    }
    
    private NamespaceDeclarationMapper() {
    }
    
    public OMNamespace map(final AxiomNamespaceDeclaration namespaceDeclaration) {
        return AxiomNamespaceDeclarationSupport.ajc$interMethodDispatch1$org_apache_axiom_om_impl_common_AxiomNamespaceDeclarationSupport$org_apache_axiom_om_impl_intf_AxiomNamespaceDeclaration$getDeclaredNamespace(namespaceDeclaration);
    }
}
