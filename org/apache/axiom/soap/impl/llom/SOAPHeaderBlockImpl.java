package org.apache.axiom.soap.impl.llom;

import org.apache.axiom.core.CoreNode;
import org.apache.axiom.core.ClonePolicy;
import org.apache.axiom.soap.SOAPVersion;
import org.apache.axiom.soap.impl.intf.AxiomSOAPElement;
import org.apache.axiom.soap.impl.common.AxiomSOAPElementSupport;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.soap.SOAPProcessingException;
import javax.xml.namespace.QName;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.core.CoreChildNode;
import org.apache.axiom.core.CoreChildNodeSupport;
import org.apache.axiom.core.CoreParentNode;
import org.apache.axiom.soap.impl.common.AxiomSOAPHeaderBlockSupport;
import org.apache.axiom.soap.impl.intf.AxiomSOAPHeaderBlock;
import org.apache.axiom.om.impl.llom.OMSourcedElementImpl;

public abstract class SOAPHeaderBlockImpl extends OMSourcedElementImpl implements AxiomSOAPHeaderBlock
{
    public boolean processed;
    
    public SOAPHeaderBlockImpl() {
        AxiomSOAPHeaderBlockSupport.ajc$interFieldInit$org_apache_axiom_soap_impl_common_AxiomSOAPHeaderBlockSupport$org_apache_axiom_soap_impl_intf_AxiomSOAPHeaderBlock$processed(this);
    }
    
    public void internalSetParent(final CoreParentNode element) {
        CoreChildNodeSupport.ajc$interMethod$org_apache_axiom_core_CoreChildNodeSupport$org_apache_axiom_core_CoreChildNode$internalSetParent(this, element);
        if (element instanceof OMElement) {
            this.checkParent((OMElement)element);
        }
    }
    
    public final boolean getMustUnderstand() throws SOAPProcessingException {
        return AxiomSOAPHeaderBlockSupport.ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAPHeaderBlockSupport$org_apache_axiom_soap_impl_intf_AxiomSOAPHeaderBlock$getMustUnderstand(this);
    }
    
    public final OMFactory getOMFactory() {
        return AxiomSOAPElementSupport.ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAPElementSupport$org_apache_axiom_soap_impl_intf_AxiomSOAPElement$getOMFactory(this);
    }
    
    public final boolean getRelay() {
        return AxiomSOAPHeaderBlockSupport.ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAPHeaderBlockSupport$org_apache_axiom_soap_impl_intf_AxiomSOAPHeaderBlock$getRelay(this);
    }
    
    public final String getRole() {
        return AxiomSOAPHeaderBlockSupport.ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAPHeaderBlockSupport$org_apache_axiom_soap_impl_intf_AxiomSOAPHeaderBlock$getRole(this);
    }
    
    public final SOAPVersion getVersion() {
        return AxiomSOAPHeaderBlockSupport.ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAPHeaderBlockSupport$org_apache_axiom_soap_impl_intf_AxiomSOAPHeaderBlock$getVersion(this);
    }
    
    public final <T> void initAncillaryData(final ClonePolicy<T> policy, final T options, final CoreNode other) {
        AxiomSOAPHeaderBlockSupport.ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAPHeaderBlockSupport$org_apache_axiom_soap_impl_intf_AxiomSOAPHeaderBlock$initAncillaryData(this, policy, options, other);
    }
    
    public final boolean isProcessed() {
        return AxiomSOAPHeaderBlockSupport.ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAPHeaderBlockSupport$org_apache_axiom_soap_impl_intf_AxiomSOAPHeaderBlock$isProcessed(this);
    }
    
    public final void setMustUnderstand(final String mustUnderstand) throws SOAPProcessingException {
        AxiomSOAPHeaderBlockSupport.ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAPHeaderBlockSupport$org_apache_axiom_soap_impl_intf_AxiomSOAPHeaderBlock$setMustUnderstand(this, mustUnderstand);
    }
    
    public final void setMustUnderstand(final boolean mustUnderstand) {
        AxiomSOAPHeaderBlockSupport.ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAPHeaderBlockSupport$org_apache_axiom_soap_impl_intf_AxiomSOAPHeaderBlock$setMustUnderstand(this, mustUnderstand);
    }
    
    public final void setProcessed() {
        AxiomSOAPHeaderBlockSupport.ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAPHeaderBlockSupport$org_apache_axiom_soap_impl_intf_AxiomSOAPHeaderBlock$setProcessed(this);
    }
    
    public final void setRelay(final boolean relay) {
        AxiomSOAPHeaderBlockSupport.ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAPHeaderBlockSupport$org_apache_axiom_soap_impl_intf_AxiomSOAPHeaderBlock$setRelay(this, relay);
    }
    
    public final void setRole(final String role) {
        AxiomSOAPHeaderBlockSupport.ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAPHeaderBlockSupport$org_apache_axiom_soap_impl_intf_AxiomSOAPHeaderBlock$setRole(this, role);
    }
}
