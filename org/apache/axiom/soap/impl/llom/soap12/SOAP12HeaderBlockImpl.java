package org.apache.axiom.soap.impl.llom.soap12;

import org.apache.axiom.soap.impl.intf.AxiomSOAP12Element;
import org.apache.axiom.soap.impl.common.AxiomSOAP12ElementSupport;
import org.apache.axiom.soap.impl.intf.SOAPHelper;
import org.apache.axiom.soap.impl.common.AxiomSOAP12HeaderBlockSupport;
import org.apache.axiom.core.CoreNode;
import org.apache.axiom.soap.SOAPProcessingException;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.soap.impl.intf.AxiomSOAP12HeaderBlock;
import org.apache.axiom.soap.impl.llom.SOAPHeaderBlockImpl;

public class SOAP12HeaderBlockImpl extends SOAPHeaderBlockImpl implements AxiomSOAP12HeaderBlock
{
    public void checkParent(final OMElement parent) throws SOAPProcessingException {
        if (!(parent instanceof SOAP12HeaderImpl)) {
            throw new SOAPProcessingException("Expecting SOAP12HeaderImpl as parent, got " + parent.getClass());
        }
    }
    
    @Override
    public final Class<? extends CoreNode> coreGetNodeClass() {
        return AxiomSOAP12HeaderBlockSupport.ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAP12HeaderBlockSupport$org_apache_axiom_soap_impl_intf_AxiomSOAP12HeaderBlock$coreGetNodeClass(this);
    }
    
    public final SOAPHelper getSOAPHelper() {
        return AxiomSOAP12ElementSupport.ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAP12ElementSupport$org_apache_axiom_soap_impl_intf_AxiomSOAP12Element$getSOAPHelper(this);
    }
}
