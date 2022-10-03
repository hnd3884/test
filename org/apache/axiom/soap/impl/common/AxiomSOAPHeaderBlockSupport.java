package org.apache.axiom.soap.impl.common;

import org.aspectj.lang.NoAspectBoundException;
import org.apache.axiom.soap.SOAPHeaderBlock;
import org.apache.axiom.soap.SOAPCloneOptions;
import org.apache.axiom.core.CoreNode;
import org.apache.axiom.core.ClonePolicy;
import org.apache.axiom.soap.impl.intf.SOAPHelper;
import org.apache.axiom.soap.SOAPProcessingException;
import org.apache.axiom.om.OMDataSource;
import org.apache.axiom.om.impl.intf.AxiomElement;
import org.apache.axiom.om.impl.common.AxiomElementSupport;
import org.apache.axiom.om.OMDataSourceExt;
import org.apache.axiom.om.impl.intf.AxiomSourcedElement;
import org.apache.axiom.om.impl.common.AxiomSourcedElementSupport;
import javax.xml.namespace.QName;
import org.apache.axiom.soap.SOAPVersion;
import org.apache.axiom.soap.impl.intf.AxiomSOAPHeaderBlock;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class AxiomSOAPHeaderBlockSupport
{
    private static /* synthetic */ Throwable ajc$initFailureCause;
    public static final /* synthetic */ AxiomSOAPHeaderBlockSupport ajc$perSingletonInstance;
    
    static {
        try {
            ajc$postClinit();
        }
        catch (final Throwable ajc$initFailureCause) {
            AxiomSOAPHeaderBlockSupport.ajc$initFailureCause = ajc$initFailureCause;
        }
    }
    
    public static void ajc$interFieldInit$org_apache_axiom_soap_impl_common_AxiomSOAPHeaderBlockSupport$org_apache_axiom_soap_impl_intf_AxiomSOAPHeaderBlock$processed(final AxiomSOAPHeaderBlock ajc$this_) {
    }
    
    public static SOAPVersion ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAPHeaderBlockSupport$org_apache_axiom_soap_impl_intf_AxiomSOAPHeaderBlock$getVersion(final AxiomSOAPHeaderBlock ajc$this_) {
        return ajc$this_.getSOAPHelper().getVersion();
    }
    
    public static boolean ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAPHeaderBlockSupport$org_apache_axiom_soap_impl_intf_AxiomSOAPHeaderBlock$isProcessed(final AxiomSOAPHeaderBlock ajc$this_) {
        return ajc$this_.ajc$interFieldGet$org_apache_axiom_soap_impl_common_AxiomSOAPHeaderBlockSupport$org_apache_axiom_soap_impl_intf_AxiomSOAPHeaderBlock$processed();
    }
    
    public static void ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAPHeaderBlockSupport$org_apache_axiom_soap_impl_intf_AxiomSOAPHeaderBlock$setProcessed(final AxiomSOAPHeaderBlock ajc$this_) {
        ajc$this_.ajc$interFieldSet$org_apache_axiom_soap_impl_common_AxiomSOAPHeaderBlockSupport$org_apache_axiom_soap_impl_intf_AxiomSOAPHeaderBlock$processed(true);
    }
    
    public static String ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAPHeaderBlockSupport$org_apache_axiom_soap_impl_intf_AxiomSOAPHeaderBlock$getAttributeValue(final AxiomSOAPHeaderBlock ajc$this_, final String key, final QName qname) {
        if (!AxiomSourcedElementSupport.ajc$interMethodDispatch1$org_apache_axiom_om_impl_common_AxiomSourcedElementSupport$org_apache_axiom_om_impl_intf_AxiomSourcedElement$isExpanded(ajc$this_)) {
            final OMDataSource ds = AxiomSourcedElementSupport.ajc$interMethodDispatch1$org_apache_axiom_om_impl_common_AxiomSourcedElementSupport$org_apache_axiom_om_impl_intf_AxiomSourcedElement$getDataSource(ajc$this_);
            if (ds instanceof OMDataSourceExt) {
                final OMDataSourceExt dsExt = (OMDataSourceExt)ds;
                if (dsExt.hasProperty(key)) {
                    return (String)dsExt.getProperty(key);
                }
            }
        }
        return AxiomElementSupport.ajc$interMethodDispatch1$org_apache_axiom_om_impl_common_AxiomElementSupport$org_apache_axiom_om_impl_intf_AxiomElement$getAttributeValue(ajc$this_, qname);
    }
    
    public static boolean ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAPHeaderBlockSupport$org_apache_axiom_soap_impl_intf_AxiomSOAPHeaderBlock$getBooleanAttributeValue(final AxiomSOAPHeaderBlock ajc$this_, final String key, final QName qname) {
        final String literal = ajc$this_.ajc$interMethodDispatch2$org_apache_axiom_soap_impl_common_AxiomSOAPHeaderBlockSupport$getAttributeValue(key, qname);
        if (literal == null) {
            return false;
        }
        final Boolean value = ajc$this_.getSOAPHelper().parseBoolean(literal);
        if (value != null) {
            return value;
        }
        throw new SOAPProcessingException("Invalid value for attribute " + qname.getLocalPart() + " in header block " + AxiomSourcedElementSupport.ajc$interMethodDispatch1$org_apache_axiom_om_impl_common_AxiomSourcedElementSupport$org_apache_axiom_om_impl_intf_AxiomSourcedElement$getQName(ajc$this_));
    }
    
    public static boolean ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAPHeaderBlockSupport$org_apache_axiom_soap_impl_intf_AxiomSOAPHeaderBlock$getMustUnderstand(final AxiomSOAPHeaderBlock ajc$this_) throws SOAPProcessingException {
        return ajc$this_.ajc$interMethodDispatch2$org_apache_axiom_soap_impl_common_AxiomSOAPHeaderBlockSupport$getBooleanAttributeValue("org.apache.axiom.soap.SOAPHeader.MUST_UNDERSTAND", ajc$this_.getSOAPHelper().getMustUnderstandAttributeQName());
    }
    
    public static void ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAPHeaderBlockSupport$org_apache_axiom_soap_impl_intf_AxiomSOAPHeaderBlock$setMustUnderstand(final AxiomSOAPHeaderBlock ajc$this_, final String mustUnderstand) throws SOAPProcessingException {
        final SOAPHelper helper = ajc$this_.getSOAPHelper();
        final Boolean value = helper.parseBoolean(mustUnderstand);
        if (value != null) {
            AxiomElementSupport.ajc$interMethodDispatch1$org_apache_axiom_om_impl_common_AxiomElementSupport$org_apache_axiom_om_impl_intf_AxiomElement$_setAttributeValue(ajc$this_, helper.getMustUnderstandAttributeQName(), mustUnderstand);
            return;
        }
        throw new SOAPProcessingException("Invalid value for mustUnderstand attribute");
    }
    
    public static void ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAPHeaderBlockSupport$org_apache_axiom_soap_impl_intf_AxiomSOAPHeaderBlock$setMustUnderstand(final AxiomSOAPHeaderBlock ajc$this_, final boolean mustUnderstand) {
        final SOAPHelper helper = ajc$this_.getSOAPHelper();
        AxiomElementSupport.ajc$interMethodDispatch1$org_apache_axiom_om_impl_common_AxiomElementSupport$org_apache_axiom_om_impl_intf_AxiomElement$_setAttributeValue(ajc$this_, helper.getMustUnderstandAttributeQName(), helper.formatBoolean(mustUnderstand));
    }
    
    public static String ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAPHeaderBlockSupport$org_apache_axiom_soap_impl_intf_AxiomSOAPHeaderBlock$getRole(final AxiomSOAPHeaderBlock ajc$this_) {
        return ajc$this_.ajc$interMethodDispatch2$org_apache_axiom_soap_impl_common_AxiomSOAPHeaderBlockSupport$getAttributeValue("org.apache.axiom.soap.SOAPHeader.ROLE", ajc$this_.getSOAPHelper().getRoleAttributeQName());
    }
    
    public static void ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAPHeaderBlockSupport$org_apache_axiom_soap_impl_intf_AxiomSOAPHeaderBlock$setRole(final AxiomSOAPHeaderBlock ajc$this_, final String role) {
        AxiomElementSupport.ajc$interMethodDispatch1$org_apache_axiom_om_impl_common_AxiomElementSupport$org_apache_axiom_om_impl_intf_AxiomElement$_setAttributeValue(ajc$this_, ajc$this_.getSOAPHelper().getRoleAttributeQName(), role);
    }
    
    public static boolean ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAPHeaderBlockSupport$org_apache_axiom_soap_impl_intf_AxiomSOAPHeaderBlock$getRelay(final AxiomSOAPHeaderBlock ajc$this_) {
        final SOAPHelper helper = ajc$this_.getSOAPHelper();
        final QName attributeQName = helper.getRelayAttributeQName();
        if (attributeQName == null) {
            throw new UnsupportedOperationException("Not supported for " + helper.getSpecName());
        }
        return ajc$this_.ajc$interMethodDispatch2$org_apache_axiom_soap_impl_common_AxiomSOAPHeaderBlockSupport$getBooleanAttributeValue("org.apache.axiom.soap.SOAPHeader.RELAY", attributeQName);
    }
    
    public static void ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAPHeaderBlockSupport$org_apache_axiom_soap_impl_intf_AxiomSOAPHeaderBlock$setRelay(final AxiomSOAPHeaderBlock ajc$this_, final boolean relay) {
        final SOAPHelper helper = ajc$this_.getSOAPHelper();
        final QName attributeQName = helper.getRelayAttributeQName();
        if (attributeQName == null) {
            throw new UnsupportedOperationException("Not supported for " + helper.getSpecName());
        }
        AxiomElementSupport.ajc$interMethodDispatch1$org_apache_axiom_om_impl_common_AxiomElementSupport$org_apache_axiom_om_impl_intf_AxiomElement$_setAttributeValue(ajc$this_, attributeQName, helper.formatBoolean(relay));
    }
    
    public static <T> void ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAPHeaderBlockSupport$org_apache_axiom_soap_impl_intf_AxiomSOAPHeaderBlock$initAncillaryData(final AxiomSOAPHeaderBlock ajc$this_, final ClonePolicy<T> policy, final T options, final CoreNode other) {
        final Boolean processedFlag = (options instanceof SOAPCloneOptions) ? ((SOAPCloneOptions)options).getProcessedFlag() : null;
        if ((processedFlag == null && ((SOAPHeaderBlock)other).isProcessed()) || (processedFlag != null && processedFlag)) {
            ajc$this_.setProcessed();
        }
    }
    
    public static AxiomSOAPHeaderBlockSupport aspectOf() {
        if (AxiomSOAPHeaderBlockSupport.ajc$perSingletonInstance == null) {
            throw new NoAspectBoundException("org_apache_axiom_soap_impl_common_AxiomSOAPHeaderBlockSupport", AxiomSOAPHeaderBlockSupport.ajc$initFailureCause);
        }
        return AxiomSOAPHeaderBlockSupport.ajc$perSingletonInstance;
    }
    
    public static boolean hasAspect() {
        return AxiomSOAPHeaderBlockSupport.ajc$perSingletonInstance != null;
    }
    
    private static /* synthetic */ void ajc$postClinit() {
        ajc$perSingletonInstance = new AxiomSOAPHeaderBlockSupport();
    }
}
