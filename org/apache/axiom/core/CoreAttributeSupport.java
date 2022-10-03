package org.apache.axiom.core;

import org.aspectj.lang.NoAspectBoundException;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class CoreAttributeSupport
{
    private static /* synthetic */ Throwable ajc$initFailureCause;
    public static final /* synthetic */ CoreAttributeSupport ajc$perSingletonInstance;
    
    static {
        try {
            ajc$postClinit();
        }
        catch (final Throwable ajc$initFailureCause) {
            CoreAttributeSupport.ajc$initFailureCause = ajc$initFailureCause;
        }
    }
    
    public static void ajc$interFieldInit$org_apache_axiom_core_CoreAttributeSupport$org_apache_axiom_core_CoreAttribute$owner(final CoreAttribute ajc$this_) {
    }
    
    public static void ajc$interFieldInit$org_apache_axiom_core_CoreAttributeSupport$org_apache_axiom_core_CoreAttribute$nextAttribute(final CoreAttribute ajc$this_) {
    }
    
    public static CoreElement ajc$interMethod$org_apache_axiom_core_CoreAttributeSupport$org_apache_axiom_core_CoreAttribute$coreGetOwnerElement(final CoreAttribute ajc$this_) {
        return (ajc$this_.ajc$interFieldGet$org_apache_axiom_core_CoreAttributeSupport$org_apache_axiom_core_CoreAttribute$owner() instanceof CoreElement) ? ((CoreElement)ajc$this_.ajc$interFieldGet$org_apache_axiom_core_CoreAttributeSupport$org_apache_axiom_core_CoreAttribute$owner()) : null;
    }
    
    public static boolean ajc$interMethod$org_apache_axiom_core_CoreAttributeSupport$org_apache_axiom_core_CoreAttribute$coreHasOwnerElement(final CoreAttribute ajc$this_) {
        return ajc$this_.ajc$interFieldGet$org_apache_axiom_core_CoreAttributeSupport$org_apache_axiom_core_CoreAttribute$owner() instanceof CoreElement;
    }
    
    public static void ajc$interMethod$org_apache_axiom_core_CoreAttributeSupport$org_apache_axiom_core_CoreAttribute$internalSetOwnerElement(final CoreAttribute ajc$this_, final CoreElement element) {
        if (element == null) {
            throw new IllegalArgumentException();
        }
        ajc$this_.ajc$interFieldSet$org_apache_axiom_core_CoreAttributeSupport$org_apache_axiom_core_CoreAttribute$owner(element);
    }
    
    public static void ajc$interMethod$org_apache_axiom_core_CoreAttributeSupport$org_apache_axiom_core_CoreAttribute$internalUnsetOwnerElement(final CoreAttribute ajc$this_, final CoreDocument newOwnerDocument) {
        ajc$this_.ajc$interFieldSet$org_apache_axiom_core_CoreAttributeSupport$org_apache_axiom_core_CoreAttribute$owner(newOwnerDocument);
    }
    
    public static CoreNode ajc$interMethod$org_apache_axiom_core_CoreAttributeSupport$org_apache_axiom_core_CoreAttribute$getRootOrOwnerDocument(final CoreAttribute ajc$this_) {
        if (ajc$this_.ajc$interFieldGet$org_apache_axiom_core_CoreAttributeSupport$org_apache_axiom_core_CoreAttribute$owner() == null) {
            return ajc$this_;
        }
        return CoreNodeSupport.ajc$interMethodDispatch1$org_apache_axiom_core_CoreNodeSupport$org_apache_axiom_core_CoreNode$getRootOrOwnerDocument(ajc$this_.ajc$interFieldGet$org_apache_axiom_core_CoreAttributeSupport$org_apache_axiom_core_CoreAttribute$owner());
    }
    
    public static void ajc$interMethod$org_apache_axiom_core_CoreAttributeSupport$org_apache_axiom_core_CoreAttribute$coreSetOwnerDocument(final CoreAttribute ajc$this_, final CoreDocument document) {
        final boolean b = ajc$this_.ajc$interFieldGet$org_apache_axiom_core_CoreAttributeSupport$org_apache_axiom_core_CoreAttribute$owner() instanceof CoreElement;
        ajc$this_.ajc$interFieldSet$org_apache_axiom_core_CoreAttributeSupport$org_apache_axiom_core_CoreAttribute$owner(document);
    }
    
    public static CoreAttribute ajc$interMethod$org_apache_axiom_core_CoreAttributeSupport$org_apache_axiom_core_CoreAttribute$coreGetNextAttribute(final CoreAttribute ajc$this_) {
        return ajc$this_.ajc$interFieldGet$org_apache_axiom_core_CoreAttributeSupport$org_apache_axiom_core_CoreAttribute$nextAttribute();
    }
    
    public static void ajc$interMethod$org_apache_axiom_core_CoreAttributeSupport$org_apache_axiom_core_CoreAttribute$internalSetNextAttribute(final CoreAttribute ajc$this_, final CoreAttribute nextAttribute) {
        ajc$this_.ajc$interFieldSet$org_apache_axiom_core_CoreAttributeSupport$org_apache_axiom_core_CoreAttribute$nextAttribute(nextAttribute);
    }
    
    public static CoreAttribute ajc$interMethod$org_apache_axiom_core_CoreAttributeSupport$org_apache_axiom_core_CoreAttribute$coreGetPreviousAttribute(final CoreAttribute ajc$this_) {
        if (ajc$this_.ajc$interFieldGet$org_apache_axiom_core_CoreAttributeSupport$org_apache_axiom_core_CoreAttribute$owner() instanceof CoreElement) {
            final CoreElement ownerElement = (CoreElement)ajc$this_.ajc$interFieldGet$org_apache_axiom_core_CoreAttributeSupport$org_apache_axiom_core_CoreAttribute$owner();
            CoreAttribute previousAttr;
            CoreAttribute nextAttr;
            for (previousAttr = CoreElementSupport.ajc$interMethodDispatch1$org_apache_axiom_core_CoreElementSupport$org_apache_axiom_core_CoreElement$coreGetFirstAttribute(ownerElement); previousAttr != null; previousAttr = nextAttr) {
                nextAttr = previousAttr.ajc$interFieldGet$org_apache_axiom_core_CoreAttributeSupport$org_apache_axiom_core_CoreAttribute$nextAttribute();
                if (nextAttr == ajc$this_) {
                    break;
                }
            }
            return previousAttr;
        }
        return null;
    }
    
    public static void ajc$interMethod$org_apache_axiom_core_CoreAttributeSupport$org_apache_axiom_core_CoreAttribute$insertAttributeAfter(final CoreAttribute ajc$this_, final CoreAttribute attr) {
        attr.internalSetOwnerElement(ajc$this_.coreGetOwnerElement());
        if (ajc$this_.ajc$interFieldGet$org_apache_axiom_core_CoreAttributeSupport$org_apache_axiom_core_CoreAttribute$nextAttribute() != null) {
            attr.ajc$interFieldSet$org_apache_axiom_core_CoreAttributeSupport$org_apache_axiom_core_CoreAttribute$nextAttribute(ajc$this_.ajc$interFieldGet$org_apache_axiom_core_CoreAttributeSupport$org_apache_axiom_core_CoreAttribute$nextAttribute());
        }
        ajc$this_.ajc$interFieldSet$org_apache_axiom_core_CoreAttributeSupport$org_apache_axiom_core_CoreAttribute$nextAttribute(attr);
    }
    
    public static boolean ajc$interMethod$org_apache_axiom_core_CoreAttributeSupport$org_apache_axiom_core_CoreAttribute$coreRemove(final CoreAttribute ajc$this_, final Semantics semantics) {
        return ajc$this_.internalRemove(semantics, null);
    }
    
    public static boolean ajc$interMethod$org_apache_axiom_core_CoreAttributeSupport$org_apache_axiom_core_CoreAttribute$internalRemove(final CoreAttribute ajc$this_, final Semantics semantics, final CoreElement newOwner) {
        if (ajc$this_.ajc$interFieldGet$org_apache_axiom_core_CoreAttributeSupport$org_apache_axiom_core_CoreAttribute$owner() instanceof CoreElement) {
            final CoreElement ownerElement = (CoreElement)ajc$this_.ajc$interFieldGet$org_apache_axiom_core_CoreAttributeSupport$org_apache_axiom_core_CoreAttribute$owner();
            final CoreAttribute previousAttr = ajc$this_.coreGetPreviousAttribute();
            ajc$this_.ajc$interFieldSet$org_apache_axiom_core_CoreAttributeSupport$org_apache_axiom_core_CoreAttribute$owner((CoreParentNode)((newOwner != null) ? newOwner : semantics.getDetachPolicy().getNewOwnerDocument(ownerElement)));
            if (previousAttr == null) {
                CoreElementSupport.ajc$interMethodDispatch1$org_apache_axiom_core_CoreElementSupport$org_apache_axiom_core_CoreElement$internalSetFirstAttribute(ownerElement, ajc$this_.ajc$interFieldGet$org_apache_axiom_core_CoreAttributeSupport$org_apache_axiom_core_CoreAttribute$nextAttribute());
            }
            else {
                previousAttr.ajc$interFieldSet$org_apache_axiom_core_CoreAttributeSupport$org_apache_axiom_core_CoreAttribute$nextAttribute(ajc$this_.ajc$interFieldGet$org_apache_axiom_core_CoreAttributeSupport$org_apache_axiom_core_CoreAttribute$nextAttribute());
            }
            ajc$this_.ajc$interFieldSet$org_apache_axiom_core_CoreAttributeSupport$org_apache_axiom_core_CoreAttribute$nextAttribute(null);
            return true;
        }
        if (newOwner != null) {
            ajc$this_.ajc$interFieldSet$org_apache_axiom_core_CoreAttributeSupport$org_apache_axiom_core_CoreAttribute$owner(newOwner);
        }
        return false;
    }
    
    public static boolean ajc$interMethod$org_apache_axiom_core_CoreAttributeSupport$org_apache_axiom_core_CoreAttribute$coreGetSpecified(final CoreAttribute ajc$this_) {
        return !CoreNodeSupport.ajc$interMethodDispatch1$org_apache_axiom_core_CoreNodeSupport$org_apache_axiom_core_CoreNode$getFlag(ajc$this_, 8);
    }
    
    public static void ajc$interMethod$org_apache_axiom_core_CoreAttributeSupport$org_apache_axiom_core_CoreAttribute$coreSetSpecified(final CoreAttribute ajc$this_, final boolean specified) {
        CoreNodeSupport.ajc$interMethodDispatch1$org_apache_axiom_core_CoreNodeSupport$org_apache_axiom_core_CoreNode$setFlag(ajc$this_, 8, !specified);
    }
    
    public static CoreAttributeSupport aspectOf() {
        if (CoreAttributeSupport.ajc$perSingletonInstance == null) {
            throw new NoAspectBoundException("org_apache_axiom_core_CoreAttributeSupport", CoreAttributeSupport.ajc$initFailureCause);
        }
        return CoreAttributeSupport.ajc$perSingletonInstance;
    }
    
    public static boolean hasAspect() {
        return CoreAttributeSupport.ajc$perSingletonInstance != null;
    }
    
    private static /* synthetic */ void ajc$postClinit() {
        ajc$perSingletonInstance = new CoreAttributeSupport();
    }
}
