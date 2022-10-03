package org.apache.axiom.soap.impl.common;

import org.aspectj.lang.NoAspectBoundException;
import javax.xml.namespace.QName;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.soap.SOAPFaultDetail;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.soap.SOAPFault;
import org.apache.axiom.soap.impl.intf.AxiomSOAPElement;
import org.apache.axiom.soap.SOAPFactory;
import java.io.Writer;
import java.io.PrintWriter;
import java.io.StringWriter;
import org.apache.axiom.soap.impl.intf.AxiomSOAPFault;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class AxiomSOAPFaultSupport
{
    private static /* synthetic */ Throwable ajc$initFailureCause;
    public static final /* synthetic */ AxiomSOAPFaultSupport ajc$perSingletonInstance;
    
    static {
        try {
            ajc$postClinit();
        }
        catch (final Throwable ajc$initFailureCause) {
            AxiomSOAPFaultSupport.ajc$initFailureCause = ajc$initFailureCause;
        }
    }
    
    public static void ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAPFaultSupport$org_apache_axiom_soap_impl_intf_AxiomSOAPFault$setException(final AxiomSOAPFault ajc$this_, final Exception e) {
        final StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        sw.flush();
        final SOAPFactory factory = (SOAPFactory)AxiomSOAPElementSupport.ajc$interMethodDispatch1$org_apache_axiom_soap_impl_common_AxiomSOAPElementSupport$org_apache_axiom_soap_impl_intf_AxiomSOAPElement$getOMFactory(ajc$this_);
        SOAPFaultDetail detail = ajc$this_.getDetail();
        if (detail == null) {
            detail = factory.createSOAPFaultDetail((SOAPFault)ajc$this_);
            ajc$this_.setDetail(detail);
        }
        final OMElement faultDetailEnty = factory.createOMElement("Exception", (OMNamespace)null, (OMContainer)detail);
        faultDetailEnty.setText(sw.getBuffer().toString());
    }
    
    public static Exception ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAPFaultSupport$org_apache_axiom_soap_impl_intf_AxiomSOAPFault$getException(final AxiomSOAPFault ajc$this_) {
        final SOAPFaultDetail detail = ajc$this_.getDetail();
        if (detail == null) {
            return null;
        }
        final OMElement exceptionElement = ajc$this_.getDetail().getFirstChildWithName(new QName("Exception"));
        if (exceptionElement != null) {
            return new Exception(exceptionElement.getText());
        }
        return null;
    }
    
    public static AxiomSOAPFaultSupport aspectOf() {
        if (AxiomSOAPFaultSupport.ajc$perSingletonInstance == null) {
            throw new NoAspectBoundException("org_apache_axiom_soap_impl_common_AxiomSOAPFaultSupport", AxiomSOAPFaultSupport.ajc$initFailureCause);
        }
        return AxiomSOAPFaultSupport.ajc$perSingletonInstance;
    }
    
    public static boolean hasAspect() {
        return AxiomSOAPFaultSupport.ajc$perSingletonInstance != null;
    }
    
    private static /* synthetic */ void ajc$postClinit() {
        ajc$perSingletonInstance = new AxiomSOAPFaultSupport();
    }
}
