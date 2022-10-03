package org.apache.axiom.soap.impl.common;

import org.aspectj.lang.NoAspectBoundException;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.soap.SOAPFaultClassifier;
import org.apache.axiom.soap.impl.intf.AxiomSOAPElement;
import javax.xml.namespace.QName;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.impl.intf.AxiomElement;
import org.apache.axiom.om.impl.common.AxiomElementSupport;
import org.apache.axiom.soap.SOAP12Constants;
import org.apache.axiom.soap.impl.intf.AxiomSOAP12FaultClassifier;
import org.apache.axiom.soap.SOAPFaultSubCode;
import org.apache.axiom.soap.SOAPFaultValue;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class AxiomSOAP12FaultClassifierSupport
{
    private static final Class<?>[] sequence;
    private static /* synthetic */ Throwable ajc$initFailureCause;
    public static final /* synthetic */ AxiomSOAP12FaultClassifierSupport ajc$perSingletonInstance;
    
    static {
        try {
            sequence = new Class[] { SOAPFaultValue.class, SOAPFaultSubCode.class };
            ajc$postClinit();
        }
        catch (final Throwable ajc$initFailureCause) {
            AxiomSOAP12FaultClassifierSupport.ajc$initFailureCause = ajc$initFailureCause;
        }
    }
    
    public static SOAPFaultValue ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAP12FaultClassifierSupport$org_apache_axiom_soap_impl_intf_AxiomSOAP12FaultClassifier$getValue(final AxiomSOAP12FaultClassifier ajc$this_) {
        return (SOAPFaultValue)ajc$this_.getFirstChildWithName(SOAP12Constants.QNAME_FAULT_VALUE);
    }
    
    public static void ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAP12FaultClassifierSupport$org_apache_axiom_soap_impl_intf_AxiomSOAP12FaultClassifier$setValue(final AxiomSOAP12FaultClassifier ajc$this_, final SOAPFaultValue value) {
        AxiomElementSupport.ajc$interMethodDispatch1$org_apache_axiom_om_impl_common_AxiomElementSupport$org_apache_axiom_om_impl_intf_AxiomElement$insertChild(ajc$this_, AxiomSOAP12FaultClassifierSupport.sequence, 0, (OMNode)value);
    }
    
    public static SOAPFaultSubCode ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAP12FaultClassifierSupport$org_apache_axiom_soap_impl_intf_AxiomSOAP12FaultClassifier$getSubCode(final AxiomSOAP12FaultClassifier ajc$this_) {
        return (SOAPFaultSubCode)ajc$this_.getFirstChildWithName(SOAP12Constants.QNAME_FAULT_SUBCODE);
    }
    
    public static void ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAP12FaultClassifierSupport$org_apache_axiom_soap_impl_intf_AxiomSOAP12FaultClassifier$setSubCode(final AxiomSOAP12FaultClassifier ajc$this_, final SOAPFaultSubCode subCode) {
        AxiomElementSupport.ajc$interMethodDispatch1$org_apache_axiom_om_impl_common_AxiomElementSupport$org_apache_axiom_om_impl_intf_AxiomElement$insertChild(ajc$this_, AxiomSOAP12FaultClassifierSupport.sequence, 1, (OMNode)subCode);
    }
    
    public static QName ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAP12FaultClassifierSupport$org_apache_axiom_soap_impl_intf_AxiomSOAP12FaultClassifier$getValueAsQName(final AxiomSOAP12FaultClassifier ajc$this_) {
        final SOAPFaultValue value = ajc$this_.getValue();
        return (value == null) ? null : value.getTextAsQName();
    }
    
    public static void ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAP12FaultClassifierSupport$org_apache_axiom_soap_impl_intf_AxiomSOAP12FaultClassifier$setValue(final AxiomSOAP12FaultClassifier ajc$this_, final QName value) {
        SOAPFaultValue valueElement = ajc$this_.getValue();
        if (valueElement == null) {
            valueElement = ((SOAP12Factory)AxiomSOAPElementSupport.ajc$interMethodDispatch1$org_apache_axiom_soap_impl_common_AxiomSOAPElementSupport$org_apache_axiom_soap_impl_intf_AxiomSOAPElement$getOMFactory(ajc$this_)).internalCreateSOAPFaultValue((SOAPFaultClassifier)ajc$this_, null);
        }
        valueElement.setText(value);
    }
    
    public static AxiomSOAP12FaultClassifierSupport aspectOf() {
        if (AxiomSOAP12FaultClassifierSupport.ajc$perSingletonInstance == null) {
            throw new NoAspectBoundException("org_apache_axiom_soap_impl_common_AxiomSOAP12FaultClassifierSupport", AxiomSOAP12FaultClassifierSupport.ajc$initFailureCause);
        }
        return AxiomSOAP12FaultClassifierSupport.ajc$perSingletonInstance;
    }
    
    public static boolean hasAspect() {
        return AxiomSOAP12FaultClassifierSupport.ajc$perSingletonInstance != null;
    }
    
    private static /* synthetic */ void ajc$postClinit() {
        ajc$perSingletonInstance = new AxiomSOAP12FaultClassifierSupport();
    }
}
