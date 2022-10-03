package org.apache.axiom.soap.impl.llom.soap11;

import org.apache.axiom.soap.impl.intf.AxiomSOAP11Element;
import org.apache.axiom.soap.impl.common.AxiomSOAP11ElementSupport;
import org.apache.axiom.soap.impl.intf.SOAPHelper;
import org.apache.axiom.soap.impl.common.AxiomSOAP11HeaderSupport;
import org.apache.axiom.core.CoreNode;
import org.apache.axiom.om.impl.traverse.OMChildrenWithSpecificAttributeIterator;
import javax.xml.namespace.QName;
import org.apache.axiom.om.impl.intf.AxiomContainer;
import org.apache.axiom.om.impl.common.AxiomContainerSupport;
import java.util.Iterator;
import org.apache.axiom.soap.impl.intf.AxiomSOAP11Header;
import org.apache.axiom.soap.impl.llom.SOAPHeaderImpl;

public class SOAP11HeaderImpl extends SOAPHeaderImpl implements AxiomSOAP11Header
{
    @Override
    public Iterator extractHeaderBlocks(final String role) {
        return (Iterator)new OMChildrenWithSpecificAttributeIterator(AxiomContainerSupport.ajc$interMethodDispatch1$org_apache_axiom_om_impl_common_AxiomContainerSupport$org_apache_axiom_om_impl_intf_AxiomContainer$getFirstOMChild(this), new QName("http://schemas.xmlsoap.org/soap/envelope/", "actor"), role, true);
    }
    
    public final Class<? extends CoreNode> coreGetNodeClass() {
        return AxiomSOAP11HeaderSupport.ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAP11HeaderSupport$org_apache_axiom_soap_impl_intf_AxiomSOAP11Header$coreGetNodeClass(this);
    }
    
    public final SOAPHelper getSOAPHelper() {
        return AxiomSOAP11ElementSupport.ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAP11ElementSupport$org_apache_axiom_soap_impl_intf_AxiomSOAP11Element$getSOAPHelper(this);
    }
}
