package org.apache.axiom.soap.impl.llom.soap11;

import org.apache.axiom.soap.impl.intf.AxiomSOAP11Element;
import org.apache.axiom.soap.impl.common.AxiomSOAP11ElementSupport;
import org.apache.axiom.soap.impl.intf.SOAPHelper;
import org.apache.axiom.soap.impl.common.AxiomSOAP11BodySupport;
import org.apache.axiom.core.CoreNode;
import org.apache.axiom.soap.impl.intf.AxiomSOAP11Body;
import org.apache.axiom.soap.impl.llom.SOAPBodyImpl;

public class SOAP11BodyImpl extends SOAPBodyImpl implements AxiomSOAP11Body
{
    public final Class<? extends CoreNode> coreGetNodeClass() {
        return AxiomSOAP11BodySupport.ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAP11BodySupport$org_apache_axiom_soap_impl_intf_AxiomSOAP11Body$coreGetNodeClass(this);
    }
    
    public final SOAPHelper getSOAPHelper() {
        return AxiomSOAP11ElementSupport.ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAP11ElementSupport$org_apache_axiom_soap_impl_intf_AxiomSOAP11Element$getSOAPHelper(this);
    }
}
