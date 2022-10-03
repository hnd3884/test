package org.apache.axiom.core;

import java.util.NoSuchElementException;
import java.util.ConcurrentModificationException;

public abstract class AbstractNodeIterator<T> implements NodeIterator<T>
{
    private final CoreParentNode startNode;
    private final Axis axis;
    private final Class<T> type;
    private final Semantics semantics;
    private CoreNode currentNode;
    private CoreParentNode currentParent;
    private CoreNode nextNode;
    private boolean hasNext;
    private int depth;
    
    public AbstractNodeIterator(final CoreParentNode startNode, final Axis axis, final Class<T> type, final Semantics semantics) {
        this.startNode = startNode;
        this.axis = axis;
        this.type = type;
        this.semantics = semantics;
    }
    
    protected abstract boolean matches(final CoreNode p0) throws CoreModelException;
    
    public final boolean hasNext() {
        if (!this.hasNext) {
            CoreNode node = this.currentNode;
            if (node instanceof CoreChildNode && CoreChildNodeSupport.ajc$interMethodDispatch1$org_apache_axiom_core_CoreChildNodeSupport$org_apache_axiom_core_CoreChildNode$coreGetParent((CoreChildNode)node) != this.currentParent) {
                throw new ConcurrentModificationException("The current node has been removed using a method other than Iterator#remove()");
            }
            Label_0043: {
                break Label_0043;
                try {
                    do {
                        Label_0259: {
                            switch (this.axis) {
                                case CHILDREN: {
                                    if (node == null) {
                                        node = CoreParentNodeSupport.ajc$interMethodDispatch1$org_apache_axiom_core_CoreParentNodeSupport$org_apache_axiom_core_CoreParentNode$coreGetFirstChild(this.startNode);
                                        break;
                                    }
                                    node = CoreChildNodeSupport.ajc$interMethodDispatch1$org_apache_axiom_core_CoreChildNodeSupport$org_apache_axiom_core_CoreChildNode$coreGetNextSibling((CoreChildNode)node);
                                    break;
                                }
                                case DESCENDANTS:
                                case DESCENDANTS_OR_SELF: {
                                    if (node == null) {
                                        if (this.axis == Axis.DESCENDANTS) {
                                            node = CoreParentNodeSupport.ajc$interMethodDispatch1$org_apache_axiom_core_CoreParentNodeSupport$org_apache_axiom_core_CoreParentNode$coreGetFirstChild(this.startNode);
                                            ++this.depth;
                                            break;
                                        }
                                        node = this.startNode;
                                        break;
                                    }
                                    else {
                                        boolean visitChildren = true;
                                        while (true) {
                                            if (visitChildren && node instanceof CoreParentNode && this.semantics.isParentNode(node.coreGetNodeType())) {
                                                final CoreChildNode firstChild = CoreParentNodeSupport.ajc$interMethodDispatch1$org_apache_axiom_core_CoreParentNodeSupport$org_apache_axiom_core_CoreParentNode$coreGetFirstChild((CoreParentNode)node);
                                                if (firstChild != null) {
                                                    ++this.depth;
                                                    node = firstChild;
                                                    break Label_0259;
                                                }
                                            }
                                            if (this.depth == 0) {
                                                node = null;
                                                break Label_0259;
                                            }
                                            final CoreChildNode nextSibling = CoreChildNodeSupport.ajc$interMethodDispatch1$org_apache_axiom_core_CoreChildNodeSupport$org_apache_axiom_core_CoreChildNode$coreGetNextSibling((CoreChildNode)node);
                                            if (nextSibling != null) {
                                                node = nextSibling;
                                                break Label_0259;
                                            }
                                            --this.depth;
                                            node = CoreChildNodeSupport.ajc$interMethodDispatch1$org_apache_axiom_core_CoreChildNodeSupport$org_apache_axiom_core_CoreChildNode$coreGetParent((CoreChildNode)node);
                                            visitChildren = false;
                                        }
                                    }
                                    break;
                                }
                            }
                        }
                        if (node != null) {
                            continue;
                        }
                        break;
                    } while (!this.matches(node));
                }
                catch (final CoreModelException ex) {
                    throw this.semantics.toUncheckedException(ex);
                }
            }
            this.nextNode = node;
            this.hasNext = true;
        }
        return this.nextNode != null;
    }
    
    public final T next() {
        if (this.hasNext()) {
            this.currentNode = this.nextNode;
            this.currentParent = ((this.currentNode instanceof CoreChildNode) ? CoreChildNodeSupport.ajc$interMethodDispatch1$org_apache_axiom_core_CoreChildNodeSupport$org_apache_axiom_core_CoreChildNode$coreGetParent((CoreChildNode)this.currentNode) : null);
            this.hasNext = false;
            return this.type.cast(this.currentNode);
        }
        throw new NoSuchElementException();
    }
    
    public final void remove() {
        if (this.currentNode == null) {
            throw new IllegalStateException();
        }
        this.hasNext();
        if (this.currentNode instanceof CoreChildNode) {
            CoreChildNodeSupport.ajc$interMethodDispatch1$org_apache_axiom_core_CoreChildNodeSupport$org_apache_axiom_core_CoreChildNode$coreDetach((CoreChildNode)this.currentNode, this.semantics);
        }
        this.currentNode = null;
    }
    
    public final void replace(final CoreChildNode newNode) throws CoreModelException {
        this.hasNext();
        CoreChildNodeSupport.ajc$interMethodDispatch1$org_apache_axiom_core_CoreChildNodeSupport$org_apache_axiom_core_CoreChildNode$coreReplaceWith((CoreChildNode)this.currentNode, newNode, this.semantics);
    }
}
