package io.netty.util.internal.shaded.org.jctools.queues.atomic;

import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

abstract class BaseLinkedAtomicQueueProducerNodeRef<E> extends BaseLinkedAtomicQueuePad0<E>
{
    private static final AtomicReferenceFieldUpdater<BaseLinkedAtomicQueueProducerNodeRef, LinkedQueueAtomicNode> P_NODE_UPDATER;
    private volatile LinkedQueueAtomicNode<E> producerNode;
    
    final void spProducerNode(final LinkedQueueAtomicNode<E> newValue) {
        BaseLinkedAtomicQueueProducerNodeRef.P_NODE_UPDATER.lazySet(this, newValue);
    }
    
    final void soProducerNode(final LinkedQueueAtomicNode<E> newValue) {
        BaseLinkedAtomicQueueProducerNodeRef.P_NODE_UPDATER.lazySet(this, newValue);
    }
    
    final LinkedQueueAtomicNode<E> lvProducerNode() {
        return this.producerNode;
    }
    
    final boolean casProducerNode(final LinkedQueueAtomicNode<E> expect, final LinkedQueueAtomicNode<E> newValue) {
        return BaseLinkedAtomicQueueProducerNodeRef.P_NODE_UPDATER.compareAndSet(this, expect, newValue);
    }
    
    final LinkedQueueAtomicNode<E> lpProducerNode() {
        return this.producerNode;
    }
    
    protected final LinkedQueueAtomicNode<E> xchgProducerNode(final LinkedQueueAtomicNode<E> newValue) {
        return BaseLinkedAtomicQueueProducerNodeRef.P_NODE_UPDATER.getAndSet(this, newValue);
    }
    
    static {
        P_NODE_UPDATER = AtomicReferenceFieldUpdater.newUpdater(BaseLinkedAtomicQueueProducerNodeRef.class, LinkedQueueAtomicNode.class, "producerNode");
    }
}
