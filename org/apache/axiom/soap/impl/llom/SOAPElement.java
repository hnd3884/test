package org.apache.axiom.soap.impl.llom;

import org.apache.axiom.soap.impl.common.AxiomSOAPElementSupport;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.core.CoreChildNode;
import org.apache.axiom.core.CoreChildNodeSupport;
import org.apache.axiom.core.CoreParentNode;
import org.apache.axiom.soap.impl.intf.AxiomSOAPElement;
import org.apache.axiom.om.impl.llom.OMElementImpl;

public abstract class SOAPElement extends OMElementImpl implements AxiomSOAPElement
{
    @Override
    public void internalSetParent(final CoreParentNode element) {
        CoreChildNodeSupport.ajc$interMethod$org_apache_axiom_core_CoreChildNodeSupport$org_apache_axiom_core_CoreChildNode$internalSetParent(this, element);
        if (element instanceof OMElement) {
            this.checkParent((OMElement)element);
        }
    }
    
    public final OMFactory getOMFactory() {
        return AxiomSOAPElementSupport.ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAPElementSupport$org_apache_axiom_soap_impl_intf_AxiomSOAPElement$getOMFactory(this);
    }
}
