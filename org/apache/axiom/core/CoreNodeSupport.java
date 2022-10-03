package org.apache.axiom.core;

import org.aspectj.lang.NoAspectBoundException;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class CoreNodeSupport
{
    private static /* synthetic */ Throwable ajc$initFailureCause;
    public static final /* synthetic */ CoreNodeSupport ajc$perSingletonInstance;
    
    static {
        try {
            ajc$postClinit();
        }
        catch (final Throwable ajc$initFailureCause) {
            CoreNodeSupport.ajc$initFailureCause = ajc$initFailureCause;
        }
    }
    
    public static void ajc$interFieldInit$org_apache_axiom_core_CoreNodeSupport$org_apache_axiom_core_CoreNode$flags(final CoreNode ajc$this_) {
    }
    
    public static Class<? extends CoreNode> ajc$interMethod$org_apache_axiom_core_CoreNodeSupport$org_apache_axiom_core_CoreNode$coreGetNodeClass(final CoreNode ajc$this_) {
        return ajc$this_.coreGetNodeType().getInterface();
    }
    
    public static <T extends CoreNode> T ajc$interMethod$org_apache_axiom_core_CoreNodeSupport$org_apache_axiom_core_CoreNode$coreCreateNode(final CoreNode ajc$this_, final Class<T> type) {
        final CoreNode node = ajc$this_.coreGetNodeFactory().createNode(type);
        node.updateFiliation(ajc$this_);
        return (T)node;
    }
    
    public static void ajc$interMethod$org_apache_axiom_core_CoreNodeSupport$org_apache_axiom_core_CoreNode$updateFiliation(final CoreNode ajc$this_, final CoreNode creator) {
    }
    
    public static CoreDocument ajc$interMethod$org_apache_axiom_core_CoreNodeSupport$org_apache_axiom_core_CoreNode$coreGetOwnerDocument(final CoreNode ajc$this_, final boolean create) {
        final CoreNode root = ajc$this_.getRootOrOwnerDocument();
        if (root instanceof CoreDocument) {
            return (CoreDocument)root;
        }
        if (create) {
            final CoreDocument ownerDocument = root.coreGetNodeFactory().createNode(CoreDocument.class);
            root.coreSetOwnerDocument(ownerDocument);
            return ownerDocument;
        }
        return null;
    }
    
    public static boolean ajc$interMethod$org_apache_axiom_core_CoreNodeSupport$org_apache_axiom_core_CoreNode$coreHasSameOwnerDocument(final CoreNode ajc$this_, final CoreNode other) {
        return other.getRootOrOwnerDocument() == ajc$this_.getRootOrOwnerDocument();
    }
    
    public static boolean ajc$interMethod$org_apache_axiom_core_CoreNodeSupport$org_apache_axiom_core_CoreNode$getFlag(final CoreNode ajc$this_, final int flag) {
        return (ajc$this_.ajc$interFieldGet$org_apache_axiom_core_CoreNodeSupport$org_apache_axiom_core_CoreNode$flags() & flag) != 0x0;
    }
    
    public static void ajc$interMethod$org_apache_axiom_core_CoreNodeSupport$org_apache_axiom_core_CoreNode$setFlag(final CoreNode ajc$this_, final int flag, final boolean value) {
        if (value) {
            ajc$this_.ajc$interFieldSet$org_apache_axiom_core_CoreNodeSupport$org_apache_axiom_core_CoreNode$flags(ajc$this_.ajc$interFieldGet$org_apache_axiom_core_CoreNodeSupport$org_apache_axiom_core_CoreNode$flags() | flag);
        }
        else {
            ajc$this_.ajc$interFieldSet$org_apache_axiom_core_CoreNodeSupport$org_apache_axiom_core_CoreNode$flags(ajc$this_.ajc$interFieldGet$org_apache_axiom_core_CoreNodeSupport$org_apache_axiom_core_CoreNode$flags() & ~flag);
        }
    }
    
    public static <T> CoreNode ajc$interMethod$org_apache_axiom_core_CoreNodeSupport$org_apache_axiom_core_CoreNode$shallowClone(final CoreNode ajc$this_, final ClonePolicy<T> policy, final T options) {
        final CoreNode clone = ajc$this_.coreGetNodeFactory().createNode(policy.getTargetNodeClass(options, ajc$this_));
        clone.init(policy, options, ajc$this_);
        clone.initAncillaryData(policy, options, ajc$this_);
        return clone;
    }
    
    public static <T> CoreNode ajc$interMethod$org_apache_axiom_core_CoreNodeSupport$org_apache_axiom_core_CoreNode$internalClone(final CoreNode ajc$this_, final ClonePolicy<T> policy, final T options, final CoreParentNode targetParent) {
        final CoreNode clone = ajc$this_.shallowClone(policy, options);
        if (targetParent != null) {
            CoreParentNodeSupport.ajc$interMethodDispatch1$org_apache_axiom_core_CoreParentNodeSupport$org_apache_axiom_core_CoreParentNode$coreAppendChild(targetParent, (CoreChildNode)clone, false);
        }
        policy.postProcess(options, clone);
        ajc$this_.cloneChildrenIfNecessary(policy, options, clone);
        return clone;
    }
    
    public static <T> CoreNode ajc$interMethod$org_apache_axiom_core_CoreNodeSupport$org_apache_axiom_core_CoreNode$coreClone(final CoreNode ajc$this_, final ClonePolicy<T> policy, final T options) {
        return ajc$this_.ajc$interMethodDispatch2$org_apache_axiom_core$internalClone(policy, options, null);
    }
    
    public static <T> void ajc$interMethod$org_apache_axiom_core_CoreNodeSupport$org_apache_axiom_core_CoreNode$initAncillaryData(final CoreNode ajc$this_, final ClonePolicy<T> policy, final T options, final CoreNode other) {
    }
    
    public static CoreNodeSupport aspectOf() {
        if (CoreNodeSupport.ajc$perSingletonInstance == null) {
            throw new NoAspectBoundException("org_apache_axiom_core_CoreNodeSupport", CoreNodeSupport.ajc$initFailureCause);
        }
        return CoreNodeSupport.ajc$perSingletonInstance;
    }
    
    public static boolean hasAspect() {
        return CoreNodeSupport.ajc$perSingletonInstance != null;
    }
    
    private static /* synthetic */ void ajc$postClinit() {
        ajc$perSingletonInstance = new CoreNodeSupport();
    }
}
