package org.apache.axiom.om.impl.common;

import org.aspectj.lang.NoAspectBoundException;
import org.apache.axiom.core.Semantics;
import org.apache.axiom.core.CoreChildNode;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.impl.intf.AxiomContainer;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.core.CoreParentNode;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.impl.intf.AxiomChildNode;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class AxiomChildNodeSupport
{
    private static /* synthetic */ Throwable ajc$initFailureCause;
    public static final /* synthetic */ AxiomChildNodeSupport ajc$perSingletonInstance;
    
    static {
        try {
            ajc$postClinit();
        }
        catch (final Throwable ajc$initFailureCause) {
            AxiomChildNodeSupport.ajc$initFailureCause = ajc$initFailureCause;
        }
    }
    
    public static OMContainer ajc$interMethod$org_apache_axiom_om_impl_common_AxiomChildNodeSupport$org_apache_axiom_om_impl_intf_AxiomChildNode$getParent(final AxiomChildNode ajc$this_) {
        final CoreParentNode parent = ajc$this_.coreGetParent();
        return (parent instanceof OMContainer) ? parent : null;
    }
    
    public static OMNode ajc$interMethod$org_apache_axiom_om_impl_common_AxiomChildNodeSupport$org_apache_axiom_om_impl_intf_AxiomChildNode$getNextOMSibling(final AxiomChildNode ajc$this_) {
        return (OMNode)ajc$this_.coreGetNextSibling();
    }
    
    public static OMNode ajc$interMethod$org_apache_axiom_om_impl_common_AxiomChildNodeSupport$org_apache_axiom_om_impl_intf_AxiomChildNode$getPreviousOMSibling(final AxiomChildNode ajc$this_) {
        return (OMNode)ajc$this_.coreGetPreviousSibling();
    }
    
    public static void ajc$interMethod$org_apache_axiom_om_impl_common_AxiomChildNodeSupport$org_apache_axiom_om_impl_intf_AxiomChildNode$insertSiblingAfter(final AxiomChildNode ajc$this_, final OMNode sibling) throws OMException {
        final AxiomContainer parent = (AxiomContainer)ajc$this_.getParent();
        if (parent == null) {
            throw new OMException("Parent can not be null");
        }
        ajc$this_.coreInsertSiblingAfter(AxiomContainerSupport.ajc$interMethodDispatch1$org_apache_axiom_om_impl_common_AxiomContainerSupport$org_apache_axiom_om_impl_intf_AxiomContainer$prepareNewChild(parent, sibling));
    }
    
    public static void ajc$interMethod$org_apache_axiom_om_impl_common_AxiomChildNodeSupport$org_apache_axiom_om_impl_intf_AxiomChildNode$insertSiblingBefore(final AxiomChildNode ajc$this_, final OMNode sibling) throws OMException {
        final AxiomContainer parent = (AxiomContainer)ajc$this_.getParent();
        if (parent == null) {
            throw new OMException("Parent can not be null");
        }
        ajc$this_.coreInsertSiblingBefore(AxiomContainerSupport.ajc$interMethodDispatch1$org_apache_axiom_om_impl_common_AxiomContainerSupport$org_apache_axiom_om_impl_intf_AxiomContainer$prepareNewChild(parent, sibling));
    }
    
    public static OMNode ajc$interMethod$org_apache_axiom_om_impl_common_AxiomChildNodeSupport$org_apache_axiom_om_impl_intf_AxiomChildNode$detach(final AxiomChildNode ajc$this_) {
        if (!ajc$this_.coreHasParent()) {
            throw new OMException("Nodes that don't have a parent can not be detached");
        }
        ajc$this_.coreDetach(AxiomSemantics.INSTANCE);
        return (OMNode)ajc$this_;
    }
    
    public static AxiomChildNodeSupport aspectOf() {
        if (AxiomChildNodeSupport.ajc$perSingletonInstance == null) {
            throw new NoAspectBoundException("org_apache_axiom_om_impl_common_AxiomChildNodeSupport", AxiomChildNodeSupport.ajc$initFailureCause);
        }
        return AxiomChildNodeSupport.ajc$perSingletonInstance;
    }
    
    public static boolean hasAspect() {
        return AxiomChildNodeSupport.ajc$perSingletonInstance != null;
    }
    
    private static /* synthetic */ void ajc$postClinit() {
        ajc$perSingletonInstance = new AxiomChildNodeSupport();
    }
}
