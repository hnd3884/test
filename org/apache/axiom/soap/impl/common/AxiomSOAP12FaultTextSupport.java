package org.apache.axiom.soap.impl.common;

import org.aspectj.lang.NoAspectBoundException;
import org.apache.axiom.core.CoreNode;
import org.apache.axiom.soap.impl.intf.AxiomSOAP12FaultText;
import org.apache.axiom.om.impl.common.OMNamespaceImpl;
import javax.xml.namespace.QName;
import org.apache.axiom.om.OMNamespace;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class AxiomSOAP12FaultTextSupport
{
    private static final OMNamespace LANG_NAMESPACE;
    private static final QName LANG_QNAME;
    private static /* synthetic */ Throwable ajc$initFailureCause;
    public static final /* synthetic */ AxiomSOAP12FaultTextSupport ajc$perSingletonInstance;
    
    static {
        try {
            LANG_NAMESPACE = (OMNamespace)new OMNamespaceImpl("http://www.w3.org/XML/1998/namespace", "xml");
            LANG_QNAME = new QName("http://www.w3.org/XML/1998/namespace", "lang", "xml");
            ajc$postClinit();
        }
        catch (final Throwable ajc$initFailureCause) {
            AxiomSOAP12FaultTextSupport.ajc$initFailureCause = ajc$initFailureCause;
        }
    }
    
    public static Class<? extends CoreNode> ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAP12FaultTextSupport$org_apache_axiom_soap_impl_intf_AxiomSOAP12FaultText$coreGetNodeClass(final AxiomSOAP12FaultText ajc$this_) {
        return AxiomSOAP12FaultText.class;
    }
    
    public static void ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAP12FaultTextSupport$org_apache_axiom_soap_impl_intf_AxiomSOAP12FaultText$setLang(final AxiomSOAP12FaultText ajc$this_, final String lang) {
        ajc$this_.addAttribute("lang", lang, AxiomSOAP12FaultTextSupport.LANG_NAMESPACE);
    }
    
    public static String ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAP12FaultTextSupport$org_apache_axiom_soap_impl_intf_AxiomSOAP12FaultText$getLang(final AxiomSOAP12FaultText ajc$this_) {
        return ajc$this_.getAttributeValue(AxiomSOAP12FaultTextSupport.LANG_QNAME);
    }
    
    public static AxiomSOAP12FaultTextSupport aspectOf() {
        if (AxiomSOAP12FaultTextSupport.ajc$perSingletonInstance == null) {
            throw new NoAspectBoundException("org_apache_axiom_soap_impl_common_AxiomSOAP12FaultTextSupport", AxiomSOAP12FaultTextSupport.ajc$initFailureCause);
        }
        return AxiomSOAP12FaultTextSupport.ajc$perSingletonInstance;
    }
    
    public static boolean hasAspect() {
        return AxiomSOAP12FaultTextSupport.ajc$perSingletonInstance != null;
    }
    
    private static /* synthetic */ void ajc$postClinit() {
        ajc$perSingletonInstance = new AxiomSOAP12FaultTextSupport();
    }
}
