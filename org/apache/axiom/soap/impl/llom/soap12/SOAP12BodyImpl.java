package org.apache.axiom.soap.impl.llom.soap12;

import org.apache.axiom.soap.impl.intf.AxiomSOAP12Element;
import org.apache.axiom.soap.impl.common.AxiomSOAP12ElementSupport;
import org.apache.axiom.soap.impl.intf.SOAPHelper;
import org.apache.axiom.soap.impl.common.AxiomSOAP12BodySupport;
import org.apache.axiom.core.CoreNode;
import org.apache.axiom.soap.impl.intf.AxiomSOAP12Body;
import org.apache.axiom.soap.impl.llom.SOAPBodyImpl;

public class SOAP12BodyImpl extends SOAPBodyImpl implements AxiomSOAP12Body
{
    public final Class<? extends CoreNode> coreGetNodeClass() {
        return AxiomSOAP12BodySupport.ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAP12BodySupport$org_apache_axiom_soap_impl_intf_AxiomSOAP12Body$coreGetNodeClass(this);
    }
    
    public final SOAPHelper getSOAPHelper() {
        return AxiomSOAP12ElementSupport.ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAP12ElementSupport$org_apache_axiom_soap_impl_intf_AxiomSOAP12Element$getSOAPHelper(this);
    }
}
