package org.apache.axiom.om.impl.common;

import org.aspectj.lang.NoAspectBoundException;
import org.apache.axiom.core.Semantics;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.impl.intf.AxiomNamespaceDeclaration;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class AxiomNamespaceDeclarationSupport
{
    private static /* synthetic */ Throwable ajc$initFailureCause;
    public static final /* synthetic */ AxiomNamespaceDeclarationSupport ajc$perSingletonInstance;
    
    static {
        try {
            ajc$postClinit();
        }
        catch (final Throwable ajc$initFailureCause) {
            AxiomNamespaceDeclarationSupport.ajc$initFailureCause = ajc$initFailureCause;
        }
    }
    
    public static void ajc$interFieldInit$org_apache_axiom_om_impl_common_AxiomNamespaceDeclarationSupport$org_apache_axiom_om_impl_intf_AxiomNamespaceDeclaration$declaredNamespace(final AxiomNamespaceDeclaration ajc$this_) {
    }
    
    public static String ajc$interMethod$org_apache_axiom_om_impl_common_AxiomNamespaceDeclarationSupport$org_apache_axiom_om_impl_intf_AxiomNamespaceDeclaration$coreGetDeclaredPrefix(final AxiomNamespaceDeclaration ajc$this_) {
        return ajc$this_.ajc$interFieldGet$org_apache_axiom_om_impl_common_AxiomNamespaceDeclarationSupport$org_apache_axiom_om_impl_intf_AxiomNamespaceDeclaration$declaredNamespace().getPrefix();
    }
    
    public static OMNamespace ajc$interMethod$org_apache_axiom_om_impl_common_AxiomNamespaceDeclarationSupport$org_apache_axiom_om_impl_intf_AxiomNamespaceDeclaration$getDeclaredNamespace(final AxiomNamespaceDeclaration ajc$this_) {
        final String namespaceURI = ajc$this_.coreGetCharacterData().toString();
        if (!namespaceURI.equals(ajc$this_.ajc$interFieldGet$org_apache_axiom_om_impl_common_AxiomNamespaceDeclarationSupport$org_apache_axiom_om_impl_intf_AxiomNamespaceDeclaration$declaredNamespace().getNamespaceURI())) {
            ajc$this_.ajc$interFieldSet$org_apache_axiom_om_impl_common_AxiomNamespaceDeclarationSupport$org_apache_axiom_om_impl_intf_AxiomNamespaceDeclaration$declaredNamespace((OMNamespace)new OMNamespaceImpl(namespaceURI, ajc$this_.ajc$interFieldGet$org_apache_axiom_om_impl_common_AxiomNamespaceDeclarationSupport$org_apache_axiom_om_impl_intf_AxiomNamespaceDeclaration$declaredNamespace().getPrefix()));
        }
        return ajc$this_.ajc$interFieldGet$org_apache_axiom_om_impl_common_AxiomNamespaceDeclarationSupport$org_apache_axiom_om_impl_intf_AxiomNamespaceDeclaration$declaredNamespace();
    }
    
    public static void ajc$interMethod$org_apache_axiom_om_impl_common_AxiomNamespaceDeclarationSupport$org_apache_axiom_om_impl_intf_AxiomNamespaceDeclaration$coreSetDeclaredNamespace(final AxiomNamespaceDeclaration ajc$this_, final String prefix, final String namespaceURI) {
        ajc$this_.setDeclaredNamespace((OMNamespace)new OMNamespaceImpl(namespaceURI, prefix));
    }
    
    public static void ajc$interMethod$org_apache_axiom_om_impl_common_AxiomNamespaceDeclarationSupport$org_apache_axiom_om_impl_intf_AxiomNamespaceDeclaration$setDeclaredNamespace(final AxiomNamespaceDeclaration ajc$this_, final OMNamespace declaredNamespace) {
        ajc$this_.ajc$interFieldSet$org_apache_axiom_om_impl_common_AxiomNamespaceDeclarationSupport$org_apache_axiom_om_impl_intf_AxiomNamespaceDeclaration$declaredNamespace(declaredNamespace);
        ajc$this_.coreSetCharacterData(declaredNamespace.getNamespaceURI(), AxiomSemantics.INSTANCE);
    }
    
    public static AxiomNamespaceDeclarationSupport aspectOf() {
        if (AxiomNamespaceDeclarationSupport.ajc$perSingletonInstance == null) {
            throw new NoAspectBoundException("org_apache_axiom_om_impl_common_AxiomNamespaceDeclarationSupport", AxiomNamespaceDeclarationSupport.ajc$initFailureCause);
        }
        return AxiomNamespaceDeclarationSupport.ajc$perSingletonInstance;
    }
    
    public static boolean hasAspect() {
        return AxiomNamespaceDeclarationSupport.ajc$perSingletonInstance != null;
    }
    
    private static /* synthetic */ void ajc$postClinit() {
        ajc$perSingletonInstance = new AxiomNamespaceDeclarationSupport();
    }
}
