package org.apache.axiom.soap.impl.common;

import org.aspectj.lang.NoAspectBoundException;
import org.apache.axiom.om.impl.common.serializer.push.OutputException;
import org.apache.axiom.om.impl.common.AxiomElementSupport;
import org.apache.axiom.om.impl.intf.AxiomDocument;
import org.apache.axiom.om.impl.common.AxiomDocumentSupport;
import org.apache.axiom.om.impl.intf.AxiomElement;
import org.apache.axiom.om.OMOutputFormat;
import org.apache.axiom.om.impl.common.serializer.push.Serializer;
import org.apache.axiom.soap.impl.builder.StAXSOAPModelBuilder;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.core.ClonePolicy;
import org.apache.axiom.core.CoreNode;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.impl.intf.AxiomSOAPMessage;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class AxiomSOAPMessageSupport
{
    private static /* synthetic */ Throwable ajc$initFailureCause;
    public static final /* synthetic */ AxiomSOAPMessageSupport ajc$perSingletonInstance;
    
    static {
        try {
            ajc$postClinit();
        }
        catch (final Throwable ajc$initFailureCause) {
            AxiomSOAPMessageSupport.ajc$initFailureCause = ajc$initFailureCause;
        }
    }
    
    public static void ajc$interFieldInit$org_apache_axiom_soap_impl_common_AxiomSOAPMessageSupport$org_apache_axiom_soap_impl_intf_AxiomSOAPMessage$factory(final AxiomSOAPMessage ajc$this_) {
    }
    
    public static Class<? extends CoreNode> ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAPMessageSupport$org_apache_axiom_soap_impl_intf_AxiomSOAPMessage$coreGetNodeClass(final AxiomSOAPMessage ajc$this_) {
        return AxiomSOAPMessage.class;
    }
    
    public static void ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAPMessageSupport$org_apache_axiom_soap_impl_intf_AxiomSOAPMessage$initSOAPFactory(final AxiomSOAPMessage ajc$this_, final SOAPFactory factory) {
        ajc$this_.ajc$interFieldSet$org_apache_axiom_soap_impl_common_AxiomSOAPMessageSupport$org_apache_axiom_soap_impl_intf_AxiomSOAPMessage$factory(factory);
    }
    
    public static <T> void ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAPMessageSupport$org_apache_axiom_soap_impl_intf_AxiomSOAPMessage$initAncillaryData(final AxiomSOAPMessage ajc$this_, final ClonePolicy<T> policy, final T options, final CoreNode other) {
        ajc$this_.ajc$interFieldSet$org_apache_axiom_soap_impl_common_AxiomSOAPMessageSupport$org_apache_axiom_soap_impl_intf_AxiomSOAPMessage$factory((SOAPFactory)((AxiomSOAPMessage)other).getOMFactory());
    }
    
    public static OMFactory ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAPMessageSupport$org_apache_axiom_soap_impl_intf_AxiomSOAPMessage$getOMFactory(final AxiomSOAPMessage ajc$this_) {
        if (ajc$this_.ajc$interFieldGet$org_apache_axiom_soap_impl_common_AxiomSOAPMessageSupport$org_apache_axiom_soap_impl_intf_AxiomSOAPMessage$factory() == null) {
            ajc$this_.ajc$interFieldSet$org_apache_axiom_soap_impl_common_AxiomSOAPMessageSupport$org_apache_axiom_soap_impl_intf_AxiomSOAPMessage$factory(((StAXSOAPModelBuilder)ajc$this_.getBuilder()).getSOAPFactory());
        }
        return (OMFactory)ajc$this_.ajc$interFieldGet$org_apache_axiom_soap_impl_common_AxiomSOAPMessageSupport$org_apache_axiom_soap_impl_intf_AxiomSOAPMessage$factory();
    }
    
    public static void ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAPMessageSupport$org_apache_axiom_soap_impl_intf_AxiomSOAPMessage$internalSerialize(final AxiomSOAPMessage ajc$this_, final Serializer serializer, final OMOutputFormat format, final boolean cache, final boolean includeXMLDeclaration) throws OutputException {
        AxiomElementSupport.ajc$interMethodDispatch1$org_apache_axiom_om_impl_common_AxiomElementSupport$org_apache_axiom_om_impl_intf_AxiomElement$internalSerialize((AxiomElement)AxiomDocumentSupport.ajc$interMethodDispatch1$org_apache_axiom_om_impl_common_AxiomDocumentSupport$org_apache_axiom_om_impl_intf_AxiomDocument$getOMDocumentElement(ajc$this_), serializer, format, cache);
    }
    
    public static AxiomSOAPMessageSupport aspectOf() {
        if (AxiomSOAPMessageSupport.ajc$perSingletonInstance == null) {
            throw new NoAspectBoundException("org_apache_axiom_soap_impl_common_AxiomSOAPMessageSupport", AxiomSOAPMessageSupport.ajc$initFailureCause);
        }
        return AxiomSOAPMessageSupport.ajc$perSingletonInstance;
    }
    
    public static boolean hasAspect() {
        return AxiomSOAPMessageSupport.ajc$perSingletonInstance != null;
    }
    
    private static /* synthetic */ void ajc$postClinit() {
        ajc$perSingletonInstance = new AxiomSOAPMessageSupport();
    }
}
