package org.apache.axiom.soap.impl.common;

import org.aspectj.lang.NoAspectBoundException;
import org.apache.axiom.om.impl.intf.AxiomElement;
import org.apache.axiom.om.impl.common.AxiomElementSupport;
import javax.xml.namespace.QName;
import org.apache.axiom.soap.SOAPFaultSubCode;
import org.apache.axiom.soap.SOAPFaultValue;
import org.apache.axiom.core.CoreNode;
import org.apache.axiom.soap.impl.intf.AxiomSOAP11FaultCode;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class AxiomSOAP11FaultCodeSupport
{
    private static /* synthetic */ Throwable ajc$initFailureCause;
    public static final /* synthetic */ AxiomSOAP11FaultCodeSupport ajc$perSingletonInstance;
    
    static {
        try {
            ajc$postClinit();
        }
        catch (final Throwable ajc$initFailureCause) {
            AxiomSOAP11FaultCodeSupport.ajc$initFailureCause = ajc$initFailureCause;
        }
    }
    
    public static Class<? extends CoreNode> ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAP11FaultCodeSupport$org_apache_axiom_soap_impl_intf_AxiomSOAP11FaultCode$coreGetNodeClass(final AxiomSOAP11FaultCode ajc$this_) {
        return AxiomSOAP11FaultCode.class;
    }
    
    public static SOAPFaultValue ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAP11FaultCodeSupport$org_apache_axiom_soap_impl_intf_AxiomSOAP11FaultCode$getValue(final AxiomSOAP11FaultCode ajc$this_) {
        return null;
    }
    
    public static void ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAP11FaultCodeSupport$org_apache_axiom_soap_impl_intf_AxiomSOAP11FaultCode$setValue(final AxiomSOAP11FaultCode ajc$this_, final SOAPFaultValue value) {
        throw new UnsupportedOperationException();
    }
    
    public static SOAPFaultSubCode ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAP11FaultCodeSupport$org_apache_axiom_soap_impl_intf_AxiomSOAP11FaultCode$getSubCode(final AxiomSOAP11FaultCode ajc$this_) {
        return null;
    }
    
    public static void ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAP11FaultCodeSupport$org_apache_axiom_soap_impl_intf_AxiomSOAP11FaultCode$setSubCode(final AxiomSOAP11FaultCode ajc$this_, final SOAPFaultSubCode subCode) {
        throw new UnsupportedOperationException();
    }
    
    public static QName ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAP11FaultCodeSupport$org_apache_axiom_soap_impl_intf_AxiomSOAP11FaultCode$getValueAsQName(final AxiomSOAP11FaultCode ajc$this_) {
        return AxiomElementSupport.ajc$interMethodDispatch1$org_apache_axiom_om_impl_common_AxiomElementSupport$org_apache_axiom_om_impl_intf_AxiomElement$getTextAsQName(ajc$this_);
    }
    
    public static void ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAP11FaultCodeSupport$org_apache_axiom_soap_impl_intf_AxiomSOAP11FaultCode$setValue(final AxiomSOAP11FaultCode ajc$this_, final QName value) {
        AxiomElementSupport.ajc$interMethodDispatch1$org_apache_axiom_om_impl_common_AxiomElementSupport$org_apache_axiom_om_impl_intf_AxiomElement$setText(ajc$this_, value);
    }
    
    public static AxiomSOAP11FaultCodeSupport aspectOf() {
        if (AxiomSOAP11FaultCodeSupport.ajc$perSingletonInstance == null) {
            throw new NoAspectBoundException("org_apache_axiom_soap_impl_common_AxiomSOAP11FaultCodeSupport", AxiomSOAP11FaultCodeSupport.ajc$initFailureCause);
        }
        return AxiomSOAP11FaultCodeSupport.ajc$perSingletonInstance;
    }
    
    public static boolean hasAspect() {
        return AxiomSOAP11FaultCodeSupport.ajc$perSingletonInstance != null;
    }
    
    private static /* synthetic */ void ajc$postClinit() {
        ajc$perSingletonInstance = new AxiomSOAP11FaultCodeSupport();
    }
}
