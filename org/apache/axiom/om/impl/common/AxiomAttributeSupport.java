package org.apache.axiom.om.impl.common;

import org.aspectj.lang.NoAspectBoundException;
import org.apache.axiom.core.Semantics;
import org.apache.axiom.om.impl.intf.AxiomNamedInformationItem;
import org.apache.axiom.om.impl.intf.AxiomElement;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.impl.intf.AxiomAttribute;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class AxiomAttributeSupport
{
    private static /* synthetic */ Throwable ajc$initFailureCause;
    public static final /* synthetic */ AxiomAttributeSupport ajc$perSingletonInstance;
    
    static {
        try {
            ajc$postClinit();
        }
        catch (final Throwable ajc$initFailureCause) {
            AxiomAttributeSupport.ajc$initFailureCause = ajc$initFailureCause;
        }
    }
    
    public static OMElement ajc$interMethod$org_apache_axiom_om_impl_common_AxiomAttributeSupport$org_apache_axiom_om_impl_intf_AxiomAttribute$getOwner(final AxiomAttribute ajc$this_) {
        return (OMElement)ajc$this_.coreGetOwnerElement();
    }
    
    public static void ajc$interMethod$org_apache_axiom_om_impl_common_AxiomAttributeSupport$org_apache_axiom_om_impl_intf_AxiomAttribute$beforeSetLocalName(final AxiomAttribute ajc$this_) {
    }
    
    public static void ajc$interMethod$org_apache_axiom_om_impl_common_AxiomAttributeSupport$org_apache_axiom_om_impl_intf_AxiomAttribute$setNamespace(final AxiomAttribute ajc$this_, final OMNamespace namespace, final boolean decl) {
        AxiomNamedInformationItemSupport.ajc$interMethodDispatch1$org_apache_axiom_om_impl_common_AxiomNamedInformationItemSupport$org_apache_axiom_om_impl_intf_AxiomNamedInformationItem$internalSetNamespace(ajc$this_, NSUtil.handleNamespace((AxiomElement)ajc$this_.getOwner(), namespace, true, decl));
    }
    
    public static void ajc$interMethod$org_apache_axiom_om_impl_common_AxiomAttributeSupport$org_apache_axiom_om_impl_intf_AxiomAttribute$setOMNamespace(final AxiomAttribute ajc$this_, final OMNamespace omNamespace) {
        AxiomNamedInformationItemSupport.ajc$interMethodDispatch1$org_apache_axiom_om_impl_common_AxiomNamedInformationItemSupport$org_apache_axiom_om_impl_intf_AxiomNamedInformationItem$internalSetNamespace(ajc$this_, omNamespace);
    }
    
    public static String ajc$interMethod$org_apache_axiom_om_impl_common_AxiomAttributeSupport$org_apache_axiom_om_impl_intf_AxiomAttribute$getAttributeValue(final AxiomAttribute ajc$this_) {
        return ajc$this_.coreGetCharacterData().toString();
    }
    
    public static void ajc$interMethod$org_apache_axiom_om_impl_common_AxiomAttributeSupport$org_apache_axiom_om_impl_intf_AxiomAttribute$setAttributeValue(final AxiomAttribute ajc$this_, final String value) {
        ajc$this_.coreSetCharacterData(value, AxiomSemantics.INSTANCE);
    }
    
    public static String ajc$interMethod$org_apache_axiom_om_impl_common_AxiomAttributeSupport$org_apache_axiom_om_impl_intf_AxiomAttribute$getAttributeType(final AxiomAttribute ajc$this_) {
        return ajc$this_.coreGetType();
    }
    
    public static void ajc$interMethod$org_apache_axiom_om_impl_common_AxiomAttributeSupport$org_apache_axiom_om_impl_intf_AxiomAttribute$setAttributeType(final AxiomAttribute ajc$this_, final String type) {
        ajc$this_.coreSetType(type);
    }
    
    public static void ajc$interMethod$org_apache_axiom_om_impl_common_AxiomAttributeSupport$org_apache_axiom_om_impl_intf_AxiomAttribute$setSpecified(final AxiomAttribute ajc$this_, final boolean specified) {
        ajc$this_.coreSetSpecified(specified);
    }
    
    public static AxiomAttributeSupport aspectOf() {
        if (AxiomAttributeSupport.ajc$perSingletonInstance == null) {
            throw new NoAspectBoundException("org_apache_axiom_om_impl_common_AxiomAttributeSupport", AxiomAttributeSupport.ajc$initFailureCause);
        }
        return AxiomAttributeSupport.ajc$perSingletonInstance;
    }
    
    public static boolean hasAspect() {
        return AxiomAttributeSupport.ajc$perSingletonInstance != null;
    }
    
    private static /* synthetic */ void ajc$postClinit() {
        ajc$perSingletonInstance = new AxiomAttributeSupport();
    }
}
