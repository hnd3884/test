package org.apache.axiom.soap.impl.llom.soap11;

import org.apache.axiom.soap.impl.intf.AxiomSOAP11Element;
import org.apache.axiom.soap.impl.common.AxiomSOAP11ElementSupport;
import org.apache.axiom.soap.impl.intf.SOAPHelper;
import org.apache.axiom.soap.impl.common.AxiomSOAP11HeaderBlockSupport;
import org.apache.axiom.core.CoreNode;
import org.apache.axiom.soap.SOAPProcessingException;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.soap.impl.intf.AxiomSOAP11HeaderBlock;
import org.apache.axiom.soap.impl.llom.SOAPHeaderBlockImpl;

public class SOAP11HeaderBlockImpl extends SOAPHeaderBlockImpl implements AxiomSOAP11HeaderBlock
{
    public void checkParent(final OMElement parent) throws SOAPProcessingException {
        if (!(parent instanceof SOAP11HeaderImpl)) {
            throw new SOAPProcessingException("Expecting SOAP11HeaderImpl, got " + parent.getClass());
        }
    }
    
    @Override
    public final Class<? extends CoreNode> coreGetNodeClass() {
        return AxiomSOAP11HeaderBlockSupport.ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAP11HeaderBlockSupport$org_apache_axiom_soap_impl_intf_AxiomSOAP11HeaderBlock$coreGetNodeClass(this);
    }
    
    public final SOAPHelper getSOAPHelper() {
        return AxiomSOAP11ElementSupport.ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAP11ElementSupport$org_apache_axiom_soap_impl_intf_AxiomSOAP11Element$getSOAPHelper(this);
    }
}
