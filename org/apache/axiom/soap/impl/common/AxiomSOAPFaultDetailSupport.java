package org.apache.axiom.soap.impl.common;

import org.aspectj.lang.NoAspectBoundException;
import org.apache.axiom.om.impl.intf.AxiomElement;
import org.apache.axiom.om.impl.common.AxiomElementSupport;
import java.util.Iterator;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.impl.intf.AxiomContainer;
import org.apache.axiom.om.impl.common.AxiomContainerSupport;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.soap.impl.intf.AxiomSOAPFaultDetail;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class AxiomSOAPFaultDetailSupport
{
    private static /* synthetic */ Throwable ajc$initFailureCause;
    public static final /* synthetic */ AxiomSOAPFaultDetailSupport ajc$perSingletonInstance;
    
    static {
        try {
            ajc$postClinit();
        }
        catch (final Throwable ajc$initFailureCause) {
            AxiomSOAPFaultDetailSupport.ajc$initFailureCause = ajc$initFailureCause;
        }
    }
    
    public static void ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAPFaultDetailSupport$org_apache_axiom_soap_impl_intf_AxiomSOAPFaultDetail$addDetailEntry(final AxiomSOAPFaultDetail ajc$this_, final OMElement detailElement) {
        AxiomContainerSupport.ajc$interMethodDispatch1$org_apache_axiom_om_impl_common_AxiomContainerSupport$org_apache_axiom_om_impl_intf_AxiomContainer$addChild(ajc$this_, (OMNode)detailElement);
    }
    
    public static Iterator ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAPFaultDetailSupport$org_apache_axiom_soap_impl_intf_AxiomSOAPFaultDetail$getAllDetailEntries(final AxiomSOAPFaultDetail ajc$this_) {
        return AxiomElementSupport.ajc$interMethodDispatch1$org_apache_axiom_om_impl_common_AxiomElementSupport$org_apache_axiom_om_impl_intf_AxiomElement$getChildElements(ajc$this_);
    }
    
    public static AxiomSOAPFaultDetailSupport aspectOf() {
        if (AxiomSOAPFaultDetailSupport.ajc$perSingletonInstance == null) {
            throw new NoAspectBoundException("org_apache_axiom_soap_impl_common_AxiomSOAPFaultDetailSupport", AxiomSOAPFaultDetailSupport.ajc$initFailureCause);
        }
        return AxiomSOAPFaultDetailSupport.ajc$perSingletonInstance;
    }
    
    public static boolean hasAspect() {
        return AxiomSOAPFaultDetailSupport.ajc$perSingletonInstance != null;
    }
    
    private static /* synthetic */ void ajc$postClinit() {
        ajc$perSingletonInstance = new AxiomSOAPFaultDetailSupport();
    }
}
