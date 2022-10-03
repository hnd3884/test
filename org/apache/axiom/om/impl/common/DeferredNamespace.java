package org.apache.axiom.om.impl.common;

import org.apache.axiom.om.impl.intf.AxiomSourcedElement;
import org.apache.axiom.om.OMNamespace;

final class DeferredNamespace implements OMNamespace
{
    private final AxiomSourcedElement element;
    final String uri;
    
    DeferredNamespace(final AxiomSourcedElement element, final String ns) {
        this.element = element;
        this.uri = ns;
    }
    
    public boolean equals(final String uri, final String prefix) {
        final String thisPrefix = this.getPrefix();
        if (this.uri.equals(uri)) {
            if (thisPrefix == null) {
                if (prefix != null) {
                    return false;
                }
            }
            else if (!thisPrefix.equals(prefix)) {
                return false;
            }
            return true;
        }
        return false;
    }
    
    public String getName() {
        return this.uri;
    }
    
    public String getNamespaceURI() {
        return this.uri;
    }
    
    public String getPrefix() {
        if (!AxiomSourcedElementSupport.ajc$interMethodDispatch1$org_apache_axiom_om_impl_common_AxiomSourcedElementSupport$org_apache_axiom_om_impl_intf_AxiomSourcedElement$isExpanded(this.element)) {
            AxiomSourcedElementSupport.ajc$interMethodDispatch1$org_apache_axiom_om_impl_common_AxiomSourcedElementSupport$org_apache_axiom_om_impl_intf_AxiomSourcedElement$forceExpand(this.element);
        }
        final OMNamespace actualNS = AxiomSourcedElementSupport.ajc$interMethodDispatch1$org_apache_axiom_om_impl_common_AxiomSourcedElementSupport$org_apache_axiom_om_impl_intf_AxiomSourcedElement$getNamespace(this.element);
        return (actualNS == null) ? "" : actualNS.getPrefix();
    }
    
    @Override
    public int hashCode() {
        final String thisPrefix = this.getPrefix();
        return this.uri.hashCode() ^ ((thisPrefix != null) ? thisPrefix.hashCode() : 0);
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof OMNamespace)) {
            return false;
        }
        final OMNamespace other = (OMNamespace)obj;
        final String otherPrefix = other.getPrefix();
        final String thisPrefix = this.getPrefix();
        if (this.uri.equals(other.getNamespaceURI())) {
            if (thisPrefix == null) {
                if (otherPrefix != null) {
                    return false;
                }
            }
            else if (!thisPrefix.equals(otherPrefix)) {
                return false;
            }
            return true;
        }
        return false;
    }
}
