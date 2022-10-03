package org.apache.axiom.core;

import org.aspectj.lang.NoAspectBoundException;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.NodeUnavailableException;
import org.apache.axiom.om.impl.builder.StAXBuilder;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class CoreParentNodeSupport
{
    private static /* synthetic */ Throwable ajc$initFailureCause;
    public static final /* synthetic */ CoreParentNodeSupport ajc$perSingletonInstance;
    
    static {
        try {
            ajc$postClinit();
        }
        catch (final Throwable ajc$initFailureCause) {
            CoreParentNodeSupport.ajc$initFailureCause = ajc$initFailureCause;
        }
    }
    
    public static void ajc$interFieldInit$org_apache_axiom_core_CoreParentNodeSupport$org_apache_axiom_core_CoreParentNode$content(final CoreParentNode ajc$this_) {
    }
    
    public static int ajc$interMethod$org_apache_axiom_core_CoreParentNodeSupport$org_apache_axiom_core_CoreParentNode$getState(final CoreParentNode ajc$this_) {
        return CoreNodeSupport.ajc$interFieldGetDispatch$org_apache_axiom_core_CoreNodeSupport$org_apache_axiom_core_CoreNode$flags(ajc$this_) & 0x3;
    }
    
    public static void ajc$interMethod$org_apache_axiom_core_CoreParentNodeSupport$org_apache_axiom_core_CoreParentNode$coreSetState(final CoreParentNode ajc$this_, final int state) {
        CoreNodeSupport.ajc$interFieldSetDispatch$org_apache_axiom_core_CoreNodeSupport$org_apache_axiom_core_CoreNode$flags(ajc$this_, (CoreNodeSupport.ajc$interFieldGetDispatch$org_apache_axiom_core_CoreNodeSupport$org_apache_axiom_core_CoreNode$flags(ajc$this_) & 0xFFFFFFFC) | state);
    }
    
    public static boolean ajc$interMethod$org_apache_axiom_core_CoreParentNodeSupport$org_apache_axiom_core_CoreParentNode$isExpanded(final CoreParentNode ajc$this_) {
        return true;
    }
    
    public static void ajc$interMethod$org_apache_axiom_core_CoreParentNodeSupport$org_apache_axiom_core_CoreParentNode$forceExpand(final CoreParentNode ajc$this_) {
    }
    
    public static Content ajc$interMethod$org_apache_axiom_core_CoreParentNodeSupport$org_apache_axiom_core_CoreParentNode$getContent(final CoreParentNode ajc$this_, final boolean create) {
        if (ajc$this_.getState() == 3) {
            final Content content = new Content();
            final CoreCharacterDataNode cdata = ajc$this_.coreGetNodeFactory().createNode(CoreCharacterDataNode.class);
            CoreChildNodeSupport.ajc$interMethodDispatch1$org_apache_axiom_core_CoreChildNodeSupport$org_apache_axiom_core_CoreChildNode$internalSetParent(cdata, ajc$this_);
            CoreCharacterDataNodeSupport.ajc$interMethodDispatch1$org_apache_axiom_core_CoreCharacterDataNodeSupport$org_apache_axiom_core_CoreCharacterDataNode$coreSetCharacterData(cdata, ajc$this_.ajc$interFieldGet$org_apache_axiom_core_CoreParentNodeSupport$org_apache_axiom_core_CoreParentNode$content());
            content.firstChild = cdata;
            content.lastChild = cdata;
            ajc$this_.ajc$interFieldSet$org_apache_axiom_core_CoreParentNodeSupport$org_apache_axiom_core_CoreParentNode$content(content);
            ajc$this_.coreSetState(0);
            return content;
        }
        Content content = (Content)ajc$this_.ajc$interFieldGet$org_apache_axiom_core_CoreParentNodeSupport$org_apache_axiom_core_CoreParentNode$content();
        if (content == null && create) {
            content = new Content();
            ajc$this_.ajc$interFieldSet$org_apache_axiom_core_CoreParentNodeSupport$org_apache_axiom_core_CoreParentNode$content(content);
        }
        return content;
    }
    
    public static CoreChildNode ajc$interMethod$org_apache_axiom_core_CoreParentNodeSupport$org_apache_axiom_core_CoreParentNode$coreGetFirstChildIfAvailable(final CoreParentNode ajc$this_) {
        ajc$this_.forceExpand();
        final Content content = ajc$this_.ajc$interMethodDispatch2$org_apache_axiom_core$getContent(false);
        return (content == null) ? null : content.firstChild;
    }
    
    public static CoreChildNode ajc$interMethod$org_apache_axiom_core_CoreParentNodeSupport$org_apache_axiom_core_CoreParentNode$coreGetLastKnownChild(final CoreParentNode ajc$this_) {
        final Content content = ajc$this_.ajc$interMethodDispatch2$org_apache_axiom_core$getContent(false);
        return (content == null) ? null : content.lastChild;
    }
    
    public static void ajc$interMethod$org_apache_axiom_core_CoreParentNodeSupport$org_apache_axiom_core_CoreParentNode$buildNext(final CoreParentNode ajc$this_) {
        final OMXMLParserWrapper builder = ajc$this_.getBuilder();
        if (builder == null) {
            throw new IllegalStateException("The node has no builder");
        }
        if (((StAXOMBuilder)builder).isClosed()) {
            throw new OMException("The builder has already been closed");
        }
        if (!builder.isCompleted()) {
            builder.next();
            return;
        }
        throw new IllegalStateException("Builder is already complete");
    }
    
    public static CoreChildNode ajc$interMethod$org_apache_axiom_core_CoreParentNodeSupport$org_apache_axiom_core_CoreParentNode$coreGetFirstChild(final CoreParentNode ajc$this_) {
        CoreChildNode firstChild = ajc$this_.coreGetFirstChildIfAvailable();
        if (firstChild == null) {
            switch (ajc$this_.getState()) {
                case 2: {
                    ((StAXBuilder)ajc$this_.getBuilder()).debugDiscarded((Object)ajc$this_);
                    throw new NodeUnavailableException();
                }
                case 1: {
                    do {
                        ajc$this_.buildNext();
                    } while (ajc$this_.getState() == 1 && (firstChild = ajc$this_.coreGetFirstChildIfAvailable()) == null);
                    break;
                }
            }
        }
        return firstChild;
    }
    
    public static CoreChildNode ajc$interMethod$org_apache_axiom_core_CoreParentNodeSupport$org_apache_axiom_core_CoreParentNode$coreGetFirstChild(final CoreParentNode ajc$this_, final NodeFilter filter) {
        CoreChildNode child;
        for (child = ajc$this_.coreGetFirstChild(); child != null && !filter.accept(child); child = CoreChildNodeSupport.ajc$interMethodDispatch1$org_apache_axiom_core_CoreChildNodeSupport$org_apache_axiom_core_CoreChildNode$coreGetNextSibling(child)) {}
        return child;
    }
    
    public static CoreChildNode ajc$interMethod$org_apache_axiom_core_CoreParentNodeSupport$org_apache_axiom_core_CoreParentNode$coreGetLastChild(final CoreParentNode ajc$this_) {
        ajc$this_.build();
        return ajc$this_.coreGetLastKnownChild();
    }
    
    public static CoreChildNode ajc$interMethod$org_apache_axiom_core_CoreParentNodeSupport$org_apache_axiom_core_CoreParentNode$coreGetLastChild(final CoreParentNode ajc$this_, final NodeFilter filter) {
        CoreChildNode child;
        for (child = ajc$this_.coreGetLastChild(); child != null && !filter.accept(child); child = CoreChildNodeSupport.ajc$interMethodDispatch1$org_apache_axiom_core_CoreChildNodeSupport$org_apache_axiom_core_CoreChildNode$coreGetPreviousSibling(child)) {}
        return child;
    }
    
    public static void ajc$interMethod$org_apache_axiom_core_CoreParentNodeSupport$org_apache_axiom_core_CoreParentNode$coreAppendChild(final CoreParentNode ajc$this_, final CoreChildNode child, final boolean fromBuilder) {
        final CoreParentNode parent = CoreChildNodeSupport.ajc$interMethodDispatch1$org_apache_axiom_core_CoreChildNodeSupport$org_apache_axiom_core_CoreChildNode$coreGetParent(child);
        if (!fromBuilder) {
            ajc$this_.build();
        }
        final Content content = ajc$this_.ajc$interMethodDispatch2$org_apache_axiom_core$getContent(true);
        if (parent == ajc$this_ && child == content.lastChild) {
            return;
        }
        CoreChildNodeSupport.ajc$interMethodDispatch1$org_apache_axiom_core_CoreChildNodeSupport$org_apache_axiom_core_CoreChildNode$internalDetach(child, null, ajc$this_);
        if (content.firstChild == null) {
            content.firstChild = child;
        }
        else {
            CoreChildNodeSupport.ajc$interFieldSetDispatch$org_apache_axiom_core_CoreChildNodeSupport$org_apache_axiom_core_CoreChildNode$previousSibling(child, content.lastChild);
            CoreChildNodeSupport.ajc$interFieldSetDispatch$org_apache_axiom_core_CoreChildNodeSupport$org_apache_axiom_core_CoreChildNode$nextSibling(content.lastChild, child);
        }
        content.lastChild = child;
    }
    
    public static void ajc$interMethod$org_apache_axiom_core_CoreParentNodeSupport$org_apache_axiom_core_CoreParentNode$coreAppendChildren(final CoreParentNode ajc$this_, final CoreDocumentFragment fragment) {
        NonDeferringParentNodeSupport.ajc$interMethodDispatch1$org_apache_axiom_core_NonDeferringParentNodeSupport$org_apache_axiom_core_NonDeferringParentNode$build(fragment);
        final Content fragmentContent = fragment.ajc$interMethodDispatch2$org_apache_axiom_core$getContent(false);
        if (fragmentContent == null || fragmentContent.firstChild == null) {
            return;
        }
        ajc$this_.build();
        for (CoreChildNode child = fragmentContent.firstChild; child != null; child = CoreChildNodeSupport.ajc$interFieldGetDispatch$org_apache_axiom_core_CoreChildNodeSupport$org_apache_axiom_core_CoreChildNode$nextSibling(child)) {
            CoreChildNodeSupport.ajc$interMethodDispatch1$org_apache_axiom_core_CoreChildNodeSupport$org_apache_axiom_core_CoreChildNode$internalSetParent(child, ajc$this_);
        }
        final Content content = ajc$this_.ajc$interMethodDispatch2$org_apache_axiom_core$getContent(true);
        if (content.firstChild == null) {
            content.firstChild = fragmentContent.firstChild;
        }
        else {
            CoreChildNodeSupport.ajc$interFieldSetDispatch$org_apache_axiom_core_CoreChildNodeSupport$org_apache_axiom_core_CoreChildNode$previousSibling(fragmentContent.firstChild, content.lastChild);
            CoreChildNodeSupport.ajc$interFieldSetDispatch$org_apache_axiom_core_CoreChildNodeSupport$org_apache_axiom_core_CoreChildNode$nextSibling(content.lastChild, fragmentContent.firstChild);
        }
        content.lastChild = fragmentContent.lastChild;
        fragmentContent.firstChild = null;
        fragmentContent.lastChild = null;
    }
    
    public static void ajc$interMethod$org_apache_axiom_core_CoreParentNodeSupport$org_apache_axiom_core_CoreParentNode$coreRemoveChildren(final CoreParentNode ajc$this_, final Semantics semantics) {
        if (ajc$this_.getState() == 3) {
            ajc$this_.coreSetState(0);
            ajc$this_.ajc$interFieldSet$org_apache_axiom_core_CoreParentNodeSupport$org_apache_axiom_core_CoreParentNode$content(null);
        }
        else {
            CoreChildNode child = ajc$this_.coreGetFirstChildIfAvailable();
            boolean updateState;
            if (ajc$this_.getState() == 1 && ajc$this_.getBuilder() != null) {
                final CoreChildNode lastChild = ajc$this_.coreGetLastKnownChild();
                if (lastChild instanceof CoreParentNode) {
                    ((CoreParentNode)lastChild).build();
                }
                ((StAXOMBuilder)ajc$this_.getBuilder()).discard((OMContainer)ajc$this_);
                updateState = true;
            }
            else {
                updateState = false;
            }
            if (child != null) {
                final CoreDocument newOwnerDocument = semantics.getDetachPolicy().getNewOwnerDocument(ajc$this_);
                do {
                    final CoreChildNode nextSibling = CoreChildNodeSupport.ajc$interFieldGetDispatch$org_apache_axiom_core_CoreChildNodeSupport$org_apache_axiom_core_CoreChildNode$nextSibling(child);
                    CoreChildNodeSupport.ajc$interFieldSetDispatch$org_apache_axiom_core_CoreChildNodeSupport$org_apache_axiom_core_CoreChildNode$previousSibling(child, null);
                    CoreChildNodeSupport.ajc$interFieldSetDispatch$org_apache_axiom_core_CoreChildNodeSupport$org_apache_axiom_core_CoreChildNode$nextSibling(child, null);
                    CoreChildNodeSupport.ajc$interMethodDispatch1$org_apache_axiom_core_CoreChildNodeSupport$org_apache_axiom_core_CoreChildNode$internalUnsetParent(child, newOwnerDocument);
                    child = nextSibling;
                } while (child != null);
            }
            ajc$this_.ajc$interFieldSet$org_apache_axiom_core_CoreParentNodeSupport$org_apache_axiom_core_CoreParentNode$content(null);
            if (updateState) {
                ajc$this_.coreSetState(0);
            }
        }
    }
    
    public static Object ajc$interMethod$org_apache_axiom_core_CoreParentNodeSupport$org_apache_axiom_core_CoreParentNode$internalGetCharacterData(final CoreParentNode ajc$this_, final ElementAction elementAction) {
        if (ajc$this_.getState() == 3) {
            return ajc$this_.ajc$interFieldGet$org_apache_axiom_core_CoreParentNodeSupport$org_apache_axiom_core_CoreParentNode$content();
        }
        Object textContent = null;
        StringBuilder buffer = null;
        int depth = 0;
        CoreChildNode child = ajc$this_.coreGetFirstChild();
        boolean visited = false;
        while (child != null) {
            if (visited) {
                visited = false;
            }
            else if (child instanceof CoreElement) {
                switch (elementAction) {
                    case RETURN_NULL: {
                        return null;
                    }
                    case RECURSE: {
                        final CoreChildNode firstChild = ((CoreParentNode)child).coreGetFirstChild();
                        if (firstChild != null) {
                            child = firstChild;
                            ++depth;
                            continue;
                        }
                        break;
                    }
                }
            }
            else if (child instanceof CoreCharacterDataNode || child instanceof CoreCDATASection) {
                final Object textValue = ((CoreCharacterDataContainer)child).coreGetCharacterData();
                if (textValue instanceof CharacterData || ((String)textValue).length() != 0) {
                    if (textContent == null) {
                        textContent = textValue;
                    }
                    else {
                        if (buffer == null) {
                            buffer = new StringBuilder(textContent.toString());
                        }
                        buffer.append(textValue.toString());
                    }
                }
            }
            final CoreChildNode nextSibling = CoreChildNodeSupport.ajc$interMethodDispatch1$org_apache_axiom_core_CoreChildNodeSupport$org_apache_axiom_core_CoreChildNode$coreGetNextSibling(child);
            if (depth > 0 && nextSibling == null) {
                --depth;
                child = (CoreChildNode)CoreChildNodeSupport.ajc$interMethodDispatch1$org_apache_axiom_core_CoreChildNodeSupport$org_apache_axiom_core_CoreChildNode$coreGetParent(child);
                visited = true;
            }
            else {
                child = nextSibling;
            }
        }
        if (textContent == null) {
            return "";
        }
        if (buffer != null) {
            return buffer.toString();
        }
        return textContent;
    }
    
    public static void ajc$interMethod$org_apache_axiom_core_CoreParentNodeSupport$org_apache_axiom_core_CoreParentNode$coreSetCharacterData(final CoreParentNode ajc$this_, final Object data, final Semantics semantics) {
        ajc$this_.coreRemoveChildren(semantics);
        if (data != null && (data instanceof CharacterData || ((String)data).length() > 0)) {
            ajc$this_.coreSetState(3);
            ajc$this_.ajc$interFieldSet$org_apache_axiom_core_CoreParentNodeSupport$org_apache_axiom_core_CoreParentNode$content(data);
        }
    }
    
    public static <T> NodeIterator<T> ajc$interMethod$org_apache_axiom_core_CoreParentNodeSupport$org_apache_axiom_core_CoreParentNode$coreGetNodes(final CoreParentNode ajc$this_, final Axis axis, final Class<T> type, final Semantics semantics) {
        return (NodeIterator<T>)new CoreParentNode$CoreParentNodeSupport.CoreParentNode$CoreParentNodeSupport$1(ajc$this_, ajc$this_, axis, (Class)type, semantics);
    }
    
    public static <T extends CoreElement> NodeIterator<T> ajc$interMethod$org_apache_axiom_core_CoreParentNodeSupport$org_apache_axiom_core_CoreParentNode$coreGetElements(final CoreParentNode ajc$this_, final Axis axis, final Class<T> type, final ElementMatcher<? super T> matcher, final String namespaceURI, final String name, final Semantics semantics) {
        return new ElementsIterator<T>(ajc$this_, axis, type, matcher, namespaceURI, name, semantics);
    }
    
    public static <T> void ajc$interMethod$org_apache_axiom_core_CoreParentNodeSupport$org_apache_axiom_core_CoreParentNode$cloneChildrenIfNecessary(final CoreParentNode ajc$this_, final ClonePolicy<T> policy, final T options, final CoreNode clone) {
        final CoreParentNode targetParent = (CoreParentNode)clone;
        if (policy.cloneChildren(options, ajc$this_.coreGetNodeType()) && targetParent.isExpanded()) {
            if (ajc$this_.getState() == 3) {
                Object content = ajc$this_.ajc$interFieldGet$org_apache_axiom_core_CoreParentNodeSupport$org_apache_axiom_core_CoreParentNode$content();
                if (content instanceof CharacterData) {
                    content = ((CharacterData)content).clone(policy, options);
                }
                targetParent.coreSetCharacterData(content, null);
            }
            else {
                for (CoreChildNode child = ajc$this_.coreGetFirstChild(); child != null; child = CoreChildNodeSupport.ajc$interMethodDispatch1$org_apache_axiom_core_CoreChildNodeSupport$org_apache_axiom_core_CoreChildNode$coreGetNextSibling(child)) {
                    CoreChildNodeSupport.ajc$interMethodDispatch1$org_apache_axiom_core_CoreChildNodeSupport$org_apache_axiom_core_CoreChildNode$coreClone(child, policy, options, targetParent);
                }
            }
        }
    }
    
    public static CoreParentNodeSupport aspectOf() {
        if (CoreParentNodeSupport.ajc$perSingletonInstance == null) {
            throw new NoAspectBoundException("org_apache_axiom_core_CoreParentNodeSupport", CoreParentNodeSupport.ajc$initFailureCause);
        }
        return CoreParentNodeSupport.ajc$perSingletonInstance;
    }
    
    public static boolean hasAspect() {
        return CoreParentNodeSupport.ajc$perSingletonInstance != null;
    }
    
    private static /* synthetic */ void ajc$postClinit() {
        ajc$perSingletonInstance = new CoreParentNodeSupport();
    }
}
