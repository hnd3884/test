package org.apache.axiom.core;

import org.aspectj.lang.NoAspectBoundException;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.NodeUnavailableException;
import org.apache.axiom.om.impl.builder.StAXBuilder;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class CoreChildNodeSupport
{
    private static /* synthetic */ Throwable ajc$initFailureCause;
    public static final /* synthetic */ CoreChildNodeSupport ajc$perSingletonInstance;
    
    static {
        try {
            ajc$postClinit();
        }
        catch (final Throwable ajc$initFailureCause) {
            CoreChildNodeSupport.ajc$initFailureCause = ajc$initFailureCause;
        }
    }
    
    public static void ajc$interFieldInit$org_apache_axiom_core_CoreChildNodeSupport$org_apache_axiom_core_CoreChildNode$owner(final CoreChildNode ajc$this_) {
    }
    
    public static void ajc$interFieldInit$org_apache_axiom_core_CoreChildNodeSupport$org_apache_axiom_core_CoreChildNode$nextSibling(final CoreChildNode ajc$this_) {
    }
    
    public static void ajc$interFieldInit$org_apache_axiom_core_CoreChildNodeSupport$org_apache_axiom_core_CoreChildNode$previousSibling(final CoreChildNode ajc$this_) {
    }
    
    public static boolean ajc$interMethod$org_apache_axiom_core_CoreChildNodeSupport$org_apache_axiom_core_CoreChildNode$coreHasParent(final CoreChildNode ajc$this_) {
        return CoreNodeSupport.ajc$interMethodDispatch1$org_apache_axiom_core_CoreNodeSupport$org_apache_axiom_core_CoreNode$getFlag(ajc$this_, 4);
    }
    
    public static CoreParentNode ajc$interMethod$org_apache_axiom_core_CoreChildNodeSupport$org_apache_axiom_core_CoreChildNode$coreGetParent(final CoreChildNode ajc$this_) {
        return CoreNodeSupport.ajc$interMethodDispatch1$org_apache_axiom_core_CoreNodeSupport$org_apache_axiom_core_CoreNode$getFlag(ajc$this_, 4) ? ajc$this_.ajc$interFieldGet$org_apache_axiom_core_CoreChildNodeSupport$org_apache_axiom_core_CoreChildNode$owner() : null;
    }
    
    public static CoreElement ajc$interMethod$org_apache_axiom_core_CoreChildNodeSupport$org_apache_axiom_core_CoreChildNode$coreGetParentElement(final CoreChildNode ajc$this_) {
        return (ajc$this_.ajc$interFieldGet$org_apache_axiom_core_CoreChildNodeSupport$org_apache_axiom_core_CoreChildNode$owner() instanceof CoreElement) ? ((CoreElement)ajc$this_.ajc$interFieldGet$org_apache_axiom_core_CoreChildNodeSupport$org_apache_axiom_core_CoreChildNode$owner()) : null;
    }
    
    public static void ajc$interMethod$org_apache_axiom_core_CoreChildNodeSupport$org_apache_axiom_core_CoreChildNode$internalSetParent(final CoreChildNode ajc$this_, final CoreParentNode parent) {
        if (parent == null) {
            throw new IllegalArgumentException();
        }
        ajc$this_.ajc$interFieldSet$org_apache_axiom_core_CoreChildNodeSupport$org_apache_axiom_core_CoreChildNode$owner(parent);
        CoreNodeSupport.ajc$interMethodDispatch1$org_apache_axiom_core_CoreNodeSupport$org_apache_axiom_core_CoreNode$setFlag(ajc$this_, 4, true);
    }
    
    public static void ajc$interMethod$org_apache_axiom_core_CoreChildNodeSupport$org_apache_axiom_core_CoreChildNode$internalUnsetParent(final CoreChildNode ajc$this_, final CoreDocument newOwnerDocument) {
        ajc$this_.ajc$interFieldSet$org_apache_axiom_core_CoreChildNodeSupport$org_apache_axiom_core_CoreChildNode$owner(newOwnerDocument);
        CoreNodeSupport.ajc$interMethodDispatch1$org_apache_axiom_core_CoreNodeSupport$org_apache_axiom_core_CoreNode$setFlag(ajc$this_, 4, false);
    }
    
    public static CoreNode ajc$interMethod$org_apache_axiom_core_CoreChildNodeSupport$org_apache_axiom_core_CoreChildNode$getRootOrOwnerDocument(final CoreChildNode ajc$this_) {
        if (ajc$this_.ajc$interFieldGet$org_apache_axiom_core_CoreChildNodeSupport$org_apache_axiom_core_CoreChildNode$owner() == null) {
            return ajc$this_;
        }
        return CoreNodeSupport.ajc$interMethodDispatch1$org_apache_axiom_core_CoreNodeSupport$org_apache_axiom_core_CoreNode$getRootOrOwnerDocument(ajc$this_.ajc$interFieldGet$org_apache_axiom_core_CoreChildNodeSupport$org_apache_axiom_core_CoreChildNode$owner());
    }
    
    public static void ajc$interMethod$org_apache_axiom_core_CoreChildNodeSupport$org_apache_axiom_core_CoreChildNode$coreSetOwnerDocument(final CoreChildNode ajc$this_, final CoreDocument document) {
        if (CoreNodeSupport.ajc$interMethodDispatch1$org_apache_axiom_core_CoreNodeSupport$org_apache_axiom_core_CoreNode$getFlag(ajc$this_, 4)) {
            throw new IllegalStateException();
        }
        ajc$this_.ajc$interFieldSet$org_apache_axiom_core_CoreChildNodeSupport$org_apache_axiom_core_CoreChildNode$owner(document);
    }
    
    public static CoreChildNode ajc$interMethod$org_apache_axiom_core_CoreChildNodeSupport$org_apache_axiom_core_CoreChildNode$coreGetNextSiblingIfAvailable(final CoreChildNode ajc$this_) {
        return ajc$this_.ajc$interFieldGet$org_apache_axiom_core_CoreChildNodeSupport$org_apache_axiom_core_CoreChildNode$nextSibling();
    }
    
    public static void ajc$interMethod$org_apache_axiom_core_CoreChildNodeSupport$org_apache_axiom_core_CoreChildNode$coreSetNextSibling(final CoreChildNode ajc$this_, final CoreChildNode nextSibling) {
        ajc$this_.ajc$interFieldSet$org_apache_axiom_core_CoreChildNodeSupport$org_apache_axiom_core_CoreChildNode$nextSibling(nextSibling);
    }
    
    public static CoreChildNode ajc$interMethod$org_apache_axiom_core_CoreChildNodeSupport$org_apache_axiom_core_CoreChildNode$coreGetPreviousSibling(final CoreChildNode ajc$this_) {
        return ajc$this_.ajc$interFieldGet$org_apache_axiom_core_CoreChildNodeSupport$org_apache_axiom_core_CoreChildNode$previousSibling();
    }
    
    public static CoreChildNode ajc$interMethod$org_apache_axiom_core_CoreChildNodeSupport$org_apache_axiom_core_CoreChildNode$coreGetPreviousSibling(final CoreChildNode ajc$this_, final NodeFilter filter) {
        CoreChildNode sibling;
        for (sibling = ajc$this_.coreGetPreviousSibling(); sibling != null && !filter.accept(sibling); sibling = sibling.coreGetPreviousSibling()) {}
        return sibling;
    }
    
    public static void ajc$interMethod$org_apache_axiom_core_CoreChildNodeSupport$org_apache_axiom_core_CoreChildNode$coreSetPreviousSibling(final CoreChildNode ajc$this_, final CoreChildNode previousSibling) {
        ajc$this_.ajc$interFieldSet$org_apache_axiom_core_CoreChildNodeSupport$org_apache_axiom_core_CoreChildNode$previousSibling(previousSibling);
    }
    
    public static CoreChildNode ajc$interMethod$org_apache_axiom_core_CoreChildNodeSupport$org_apache_axiom_core_CoreChildNode$coreGetNextSibling(final CoreChildNode ajc$this_) throws OMException {
        CoreChildNode nextSibling = ajc$this_.coreGetNextSiblingIfAvailable();
        if (nextSibling == null) {
            final CoreParentNode parent = ajc$this_.coreGetParent();
            if (parent != null && parent.getBuilder() != null) {
                switch (CoreParentNodeSupport.ajc$interMethodDispatch1$org_apache_axiom_core_CoreParentNodeSupport$org_apache_axiom_core_CoreParentNode$getState(parent)) {
                    case 2: {
                        ((StAXBuilder)parent.getBuilder()).debugDiscarded((Object)parent);
                        throw new NodeUnavailableException();
                    }
                    case 1: {
                        do {
                            CoreParentNodeSupport.ajc$interMethodDispatch1$org_apache_axiom_core_CoreParentNodeSupport$org_apache_axiom_core_CoreParentNode$buildNext(parent);
                        } while (CoreParentNodeSupport.ajc$interMethodDispatch1$org_apache_axiom_core_CoreParentNodeSupport$org_apache_axiom_core_CoreParentNode$getState(parent) == 1 && (nextSibling = ajc$this_.coreGetNextSiblingIfAvailable()) == null);
                        break;
                    }
                }
            }
        }
        return nextSibling;
    }
    
    public static CoreChildNode ajc$interMethod$org_apache_axiom_core_CoreChildNodeSupport$org_apache_axiom_core_CoreChildNode$coreGetNextSibling(final CoreChildNode ajc$this_, final NodeFilter filter) {
        CoreChildNode sibling;
        for (sibling = ajc$this_.coreGetNextSibling(); sibling != null && !filter.accept(sibling); sibling = sibling.coreGetNextSibling()) {}
        return sibling;
    }
    
    public static void ajc$interMethod$org_apache_axiom_core_CoreChildNodeSupport$org_apache_axiom_core_CoreChildNode$coreInsertSiblingAfter(final CoreChildNode ajc$this_, final CoreChildNode sibling) {
        final CoreParentNode parent = ajc$this_.coreGetParent();
        if (parent == null) {
            throw new OMException("Parent can not be null");
        }
        if (ajc$this_ == sibling) {
            throw new OMException("Inserting self as the sibling is not allowed");
        }
        sibling.ajc$interMethodDispatch2$org_apache_axiom_core$internalDetach(null, parent);
        final CoreChildNode nextSibling = ajc$this_.coreGetNextSibling();
        sibling.ajc$interFieldSet$org_apache_axiom_core_CoreChildNodeSupport$org_apache_axiom_core_CoreChildNode$previousSibling(ajc$this_);
        if (nextSibling == null) {
            CoreParentNodeSupport.ajc$interMethodDispatch1$org_apache_axiom_core_CoreParentNodeSupport$org_apache_axiom_core_CoreParentNode$getContent(parent, true).lastChild = sibling;
        }
        else {
            nextSibling.ajc$interFieldSet$org_apache_axiom_core_CoreChildNodeSupport$org_apache_axiom_core_CoreChildNode$previousSibling(sibling);
        }
        sibling.ajc$interFieldSet$org_apache_axiom_core_CoreChildNodeSupport$org_apache_axiom_core_CoreChildNode$nextSibling(nextSibling);
        ajc$this_.ajc$interFieldSet$org_apache_axiom_core_CoreChildNodeSupport$org_apache_axiom_core_CoreChildNode$nextSibling(sibling);
    }
    
    public static void ajc$interMethod$org_apache_axiom_core_CoreChildNodeSupport$org_apache_axiom_core_CoreChildNode$coreInsertSiblingBefore(final CoreChildNode ajc$this_, final CoreChildNode sibling) {
        final CoreParentNode parent = ajc$this_.coreGetParent();
        if (parent == null) {
            throw new OMException("Parent can not be null");
        }
        if (ajc$this_ == sibling) {
            throw new OMException("Inserting self as the sibling is not allowed");
        }
        sibling.ajc$interMethodDispatch2$org_apache_axiom_core$internalDetach(null, parent);
        sibling.ajc$interFieldSet$org_apache_axiom_core_CoreChildNodeSupport$org_apache_axiom_core_CoreChildNode$nextSibling(ajc$this_);
        if (ajc$this_.ajc$interFieldGet$org_apache_axiom_core_CoreChildNodeSupport$org_apache_axiom_core_CoreChildNode$previousSibling() == null) {
            CoreParentNodeSupport.ajc$interMethodDispatch1$org_apache_axiom_core_CoreParentNodeSupport$org_apache_axiom_core_CoreParentNode$getContent(parent, true).firstChild = sibling;
        }
        else {
            ajc$this_.ajc$interFieldGet$org_apache_axiom_core_CoreChildNodeSupport$org_apache_axiom_core_CoreChildNode$previousSibling().ajc$interFieldSet$org_apache_axiom_core_CoreChildNodeSupport$org_apache_axiom_core_CoreChildNode$nextSibling(sibling);
        }
        sibling.ajc$interFieldSet$org_apache_axiom_core_CoreChildNodeSupport$org_apache_axiom_core_CoreChildNode$previousSibling(ajc$this_.ajc$interFieldGet$org_apache_axiom_core_CoreChildNodeSupport$org_apache_axiom_core_CoreChildNode$previousSibling());
        ajc$this_.ajc$interFieldSet$org_apache_axiom_core_CoreChildNodeSupport$org_apache_axiom_core_CoreChildNode$previousSibling(sibling);
    }
    
    public static void ajc$interMethod$org_apache_axiom_core_CoreChildNodeSupport$org_apache_axiom_core_CoreChildNode$coreInsertSiblingsBefore(final CoreChildNode ajc$this_, final CoreDocumentFragment fragment) {
        final Content fragmentContent = CoreParentNodeSupport.ajc$interMethodDispatch1$org_apache_axiom_core_CoreParentNodeSupport$org_apache_axiom_core_CoreParentNode$getContent(fragment, false);
        if (fragmentContent == null || fragmentContent.firstChild == null) {
            return;
        }
        final CoreParentNode parent = ajc$this_.coreGetParent();
        for (CoreChildNode child = fragmentContent.firstChild; child != null; child = child.ajc$interFieldGet$org_apache_axiom_core_CoreChildNodeSupport$org_apache_axiom_core_CoreChildNode$nextSibling()) {
            child.internalSetParent(parent);
        }
        fragmentContent.lastChild.ajc$interFieldSet$org_apache_axiom_core_CoreChildNodeSupport$org_apache_axiom_core_CoreChildNode$nextSibling(ajc$this_);
        if (ajc$this_.ajc$interFieldGet$org_apache_axiom_core_CoreChildNodeSupport$org_apache_axiom_core_CoreChildNode$previousSibling() == null) {
            CoreParentNodeSupport.ajc$interMethodDispatch1$org_apache_axiom_core_CoreParentNodeSupport$org_apache_axiom_core_CoreParentNode$getContent(parent, true).firstChild = fragmentContent.firstChild;
        }
        else {
            ajc$this_.ajc$interFieldGet$org_apache_axiom_core_CoreChildNodeSupport$org_apache_axiom_core_CoreChildNode$previousSibling().ajc$interFieldSet$org_apache_axiom_core_CoreChildNodeSupport$org_apache_axiom_core_CoreChildNode$nextSibling(fragmentContent.firstChild);
        }
        fragmentContent.firstChild.ajc$interFieldSet$org_apache_axiom_core_CoreChildNodeSupport$org_apache_axiom_core_CoreChildNode$previousSibling(ajc$this_.ajc$interFieldGet$org_apache_axiom_core_CoreChildNodeSupport$org_apache_axiom_core_CoreChildNode$previousSibling());
        ajc$this_.ajc$interFieldSet$org_apache_axiom_core_CoreChildNodeSupport$org_apache_axiom_core_CoreChildNode$previousSibling(fragmentContent.lastChild);
        fragmentContent.firstChild = null;
        fragmentContent.lastChild = null;
    }
    
    public static void ajc$interMethod$org_apache_axiom_core_CoreChildNodeSupport$org_apache_axiom_core_CoreChildNode$beforeDetach(final CoreChildNode ajc$this_) {
    }
    
    public static void ajc$interMethod$org_apache_axiom_core_CoreChildNodeSupport$org_apache_axiom_core_CoreChildNode$coreDetach(final CoreChildNode ajc$this_, final Semantics semantics) {
        ajc$this_.ajc$interMethodDispatch2$org_apache_axiom_core$internalDetach(semantics, null);
    }
    
    public static void ajc$interMethod$org_apache_axiom_core_CoreChildNodeSupport$org_apache_axiom_core_CoreChildNode$internalDetach(final CoreChildNode ajc$this_, final Semantics semantics, final CoreParentNode newParent) {
        final CoreParentNode parent = ajc$this_.coreGetParent();
        if (parent != null) {
            ajc$this_.ajc$interMethodDispatch2$org_apache_axiom_core$beforeDetach();
            if (ajc$this_.ajc$interFieldGet$org_apache_axiom_core_CoreChildNodeSupport$org_apache_axiom_core_CoreChildNode$previousSibling() == null) {
                CoreParentNodeSupport.ajc$interMethodDispatch1$org_apache_axiom_core_CoreParentNodeSupport$org_apache_axiom_core_CoreParentNode$getContent(parent, true).firstChild = ajc$this_.ajc$interFieldGet$org_apache_axiom_core_CoreChildNodeSupport$org_apache_axiom_core_CoreChildNode$nextSibling();
            }
            else {
                ajc$this_.ajc$interFieldGet$org_apache_axiom_core_CoreChildNodeSupport$org_apache_axiom_core_CoreChildNode$previousSibling().ajc$interFieldSet$org_apache_axiom_core_CoreChildNodeSupport$org_apache_axiom_core_CoreChildNode$nextSibling(ajc$this_.ajc$interFieldGet$org_apache_axiom_core_CoreChildNodeSupport$org_apache_axiom_core_CoreChildNode$nextSibling());
            }
            if (ajc$this_.ajc$interFieldGet$org_apache_axiom_core_CoreChildNodeSupport$org_apache_axiom_core_CoreChildNode$nextSibling() == null) {
                CoreParentNodeSupport.ajc$interMethodDispatch1$org_apache_axiom_core_CoreParentNodeSupport$org_apache_axiom_core_CoreParentNode$getContent(parent, true).lastChild = ajc$this_.ajc$interFieldGet$org_apache_axiom_core_CoreChildNodeSupport$org_apache_axiom_core_CoreChildNode$previousSibling();
            }
            else {
                ajc$this_.ajc$interFieldGet$org_apache_axiom_core_CoreChildNodeSupport$org_apache_axiom_core_CoreChildNode$nextSibling().ajc$interFieldSet$org_apache_axiom_core_CoreChildNodeSupport$org_apache_axiom_core_CoreChildNode$previousSibling(ajc$this_.ajc$interFieldGet$org_apache_axiom_core_CoreChildNodeSupport$org_apache_axiom_core_CoreChildNode$previousSibling());
            }
            ajc$this_.ajc$interFieldSet$org_apache_axiom_core_CoreChildNodeSupport$org_apache_axiom_core_CoreChildNode$nextSibling(null);
            ajc$this_.ajc$interFieldSet$org_apache_axiom_core_CoreChildNodeSupport$org_apache_axiom_core_CoreChildNode$previousSibling(null);
            if (newParent == null) {
                ajc$this_.internalUnsetParent(semantics.getDetachPolicy().getNewOwnerDocument(parent));
            }
        }
        if (newParent != null) {
            ajc$this_.internalSetParent(newParent);
        }
    }
    
    public static void ajc$interMethod$org_apache_axiom_core_CoreChildNodeSupport$org_apache_axiom_core_CoreChildNode$coreReplaceWith(final CoreChildNode ajc$this_, final CoreChildNode newNode, final Semantics semantics) {
        if (newNode == ajc$this_) {
            return;
        }
        final CoreParentNode parent = ajc$this_.coreGetParent();
        if (parent != null) {
            newNode.ajc$interMethodDispatch2$org_apache_axiom_core$internalDetach(null, parent);
            ajc$this_.ajc$interMethodDispatch2$org_apache_axiom_core$beforeDetach();
            if (ajc$this_.ajc$interFieldGet$org_apache_axiom_core_CoreChildNodeSupport$org_apache_axiom_core_CoreChildNode$previousSibling() == null) {
                CoreParentNodeSupport.ajc$interMethodDispatch1$org_apache_axiom_core_CoreParentNodeSupport$org_apache_axiom_core_CoreParentNode$getContent(parent, true).firstChild = newNode;
            }
            else {
                ajc$this_.ajc$interFieldGet$org_apache_axiom_core_CoreChildNodeSupport$org_apache_axiom_core_CoreChildNode$previousSibling().ajc$interFieldSet$org_apache_axiom_core_CoreChildNodeSupport$org_apache_axiom_core_CoreChildNode$nextSibling(newNode);
                newNode.ajc$interFieldSet$org_apache_axiom_core_CoreChildNodeSupport$org_apache_axiom_core_CoreChildNode$previousSibling(ajc$this_.ajc$interFieldGet$org_apache_axiom_core_CoreChildNodeSupport$org_apache_axiom_core_CoreChildNode$previousSibling());
                ajc$this_.ajc$interFieldSet$org_apache_axiom_core_CoreChildNodeSupport$org_apache_axiom_core_CoreChildNode$previousSibling(null);
            }
            if (ajc$this_.ajc$interFieldGet$org_apache_axiom_core_CoreChildNodeSupport$org_apache_axiom_core_CoreChildNode$nextSibling() == null) {
                CoreParentNodeSupport.ajc$interMethodDispatch1$org_apache_axiom_core_CoreParentNodeSupport$org_apache_axiom_core_CoreParentNode$getContent(parent, true).lastChild = newNode;
            }
            else {
                ajc$this_.ajc$interFieldGet$org_apache_axiom_core_CoreChildNodeSupport$org_apache_axiom_core_CoreChildNode$nextSibling().ajc$interFieldSet$org_apache_axiom_core_CoreChildNodeSupport$org_apache_axiom_core_CoreChildNode$previousSibling(newNode);
                newNode.ajc$interFieldSet$org_apache_axiom_core_CoreChildNodeSupport$org_apache_axiom_core_CoreChildNode$nextSibling(ajc$this_.ajc$interFieldGet$org_apache_axiom_core_CoreChildNodeSupport$org_apache_axiom_core_CoreChildNode$nextSibling());
                ajc$this_.ajc$interFieldSet$org_apache_axiom_core_CoreChildNodeSupport$org_apache_axiom_core_CoreChildNode$nextSibling(null);
            }
            ajc$this_.internalUnsetParent(semantics.getDetachPolicy().getNewOwnerDocument(parent));
        }
    }
    
    public static <T> CoreNode ajc$interMethod$org_apache_axiom_core_CoreChildNodeSupport$org_apache_axiom_core_CoreChildNode$coreClone(final CoreChildNode ajc$this_, final ClonePolicy<T> policy, final T options, final CoreParentNode targetParent) {
        return CoreNodeSupport.ajc$interMethodDispatch1$org_apache_axiom_core_CoreNodeSupport$org_apache_axiom_core_CoreNode$internalClone(ajc$this_, policy, options, targetParent);
    }
    
    public static CoreChildNodeSupport aspectOf() {
        if (CoreChildNodeSupport.ajc$perSingletonInstance == null) {
            throw new NoAspectBoundException("org_apache_axiom_core_CoreChildNodeSupport", CoreChildNodeSupport.ajc$initFailureCause);
        }
        return CoreChildNodeSupport.ajc$perSingletonInstance;
    }
    
    public static boolean hasAspect() {
        return CoreChildNodeSupport.ajc$perSingletonInstance != null;
    }
    
    private static /* synthetic */ void ajc$postClinit() {
        ajc$perSingletonInstance = new CoreChildNodeSupport();
    }
}
