package org.apache.axiom.soap.impl.llom.soap12;

import org.apache.axiom.soap.impl.intf.AxiomSOAP12Element;
import org.apache.axiom.soap.impl.common.AxiomSOAP12ElementSupport;
import org.apache.axiom.soap.impl.intf.SOAPHelper;
import org.apache.axiom.soap.impl.common.AxiomSOAP12HeaderSupport;
import org.apache.axiom.core.CoreNode;
import org.apache.axiom.om.impl.traverse.OMChildrenWithSpecificAttributeIterator;
import javax.xml.namespace.QName;
import org.apache.axiom.om.impl.intf.AxiomContainer;
import org.apache.axiom.om.impl.common.AxiomContainerSupport;
import java.util.Iterator;
import org.apache.axiom.soap.impl.intf.AxiomSOAP12Header;
import org.apache.axiom.soap.impl.llom.SOAPHeaderImpl;

public class SOAP12HeaderImpl extends SOAPHeaderImpl implements AxiomSOAP12Header
{
    @Override
    public Iterator extractHeaderBlocks(final String role) {
        return (Iterator)new OMChildrenWithSpecificAttributeIterator(AxiomContainerSupport.ajc$interMethodDispatch1$org_apache_axiom_om_impl_common_AxiomContainerSupport$org_apache_axiom_om_impl_intf_AxiomContainer$getFirstOMChild(this), new QName("http://www.w3.org/2003/05/soap-envelope", "role"), role, true);
    }
    
    public final Class<? extends CoreNode> coreGetNodeClass() {
        return AxiomSOAP12HeaderSupport.ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAP12HeaderSupport$org_apache_axiom_soap_impl_intf_AxiomSOAP12Header$coreGetNodeClass(this);
    }
    
    public final SOAPHelper getSOAPHelper() {
        return AxiomSOAP12ElementSupport.ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAP12ElementSupport$org_apache_axiom_soap_impl_intf_AxiomSOAP12Element$getSOAPHelper(this);
    }
}
