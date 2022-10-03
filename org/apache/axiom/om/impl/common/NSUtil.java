package org.apache.axiom.om.impl.common;

import org.apache.axiom.util.xml.NSUtils;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.impl.intf.AxiomElement;

public final class NSUtil
{
    private NSUtil() {
    }
    
    public static OMNamespace handleNamespace(final AxiomElement context, OMNamespace ns, final boolean attr, final boolean decl) {
        final String namespaceURI = (ns == null) ? "" : ns.getNamespaceURI();
        String prefix = (ns == null) ? "" : ns.getPrefix();
        if (namespaceURI.length() == 0) {
            if (prefix != null && prefix.length() != 0) {
                throw new IllegalArgumentException("Cannot bind a prefix to the empty namespace name");
            }
            if (!attr && decl && AxiomElementSupport.ajc$interMethodDispatch1$org_apache_axiom_om_impl_common_AxiomElementSupport$org_apache_axiom_om_impl_intf_AxiomElement$getDefaultNamespace(context) != null) {
                AxiomElementSupport.ajc$interMethodDispatch1$org_apache_axiom_om_impl_common_AxiomElementSupport$org_apache_axiom_om_impl_intf_AxiomElement$declareDefaultNamespace(context, "");
            }
            return null;
        }
        else {
            if (attr && prefix != null && prefix.length() == 0) {
                throw new IllegalArgumentException("An attribute with a namespace must be prefixed");
            }
            boolean addNSDecl = false;
            if (context != null && (decl || prefix == null)) {
                final OMNamespace existingNSDecl = AxiomElementSupport.ajc$interMethodDispatch1$org_apache_axiom_om_impl_common_AxiomElementSupport$org_apache_axiom_om_impl_intf_AxiomElement$findNamespace(context, namespaceURI, prefix);
                if (existingNSDecl == null || (prefix != null && !existingNSDecl.getPrefix().equals(prefix)) || (prefix == null && attr && existingNSDecl.getPrefix().length() == 0)) {
                    addNSDecl = decl;
                }
                else {
                    prefix = existingNSDecl.getPrefix();
                    ns = existingNSDecl;
                }
            }
            if (prefix == null) {
                prefix = NSUtils.generatePrefix(namespaceURI);
                ns = (OMNamespace)new OMNamespaceImpl(namespaceURI, prefix);
            }
            if (addNSDecl) {
                AxiomElementSupport.ajc$interMethodDispatch1$org_apache_axiom_om_impl_common_AxiomElementSupport$org_apache_axiom_om_impl_intf_AxiomElement$addNamespaceDeclaration(context, ns);
            }
            return ns;
        }
    }
}
