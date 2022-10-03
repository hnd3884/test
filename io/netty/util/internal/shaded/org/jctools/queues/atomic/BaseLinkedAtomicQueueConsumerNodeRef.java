package io.netty.util.internal.shaded.org.jctools.queues.atomic;

import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

abstract class BaseLinkedAtomicQueueConsumerNodeRef<E> extends BaseLinkedAtomicQueuePad1<E>
{
    private static final AtomicReferenceFieldUpdater<BaseLinkedAtomicQueueConsumerNodeRef, LinkedQueueAtomicNode> C_NODE_UPDATER;
    private volatile LinkedQueueAtomicNode<E> consumerNode;
    
    final void spConsumerNode(final LinkedQueueAtomicNode<E> newValue) {
        BaseLinkedAtomicQueueConsumerNodeRef.C_NODE_UPDATER.lazySet(this, newValue);
    }
    
    final LinkedQueueAtomicNode<E> lvConsumerNode() {
        return this.consumerNode;
    }
    
    final LinkedQueueAtomicNode<E> lpConsumerNode() {
        return this.consumerNode;
    }
    
    static {
        C_NODE_UPDATER = AtomicReferenceFieldUpdater.newUpdater(BaseLinkedAtomicQueueConsumerNodeRef.class, LinkedQueueAtomicNode.class, "consumerNode");
    }
}
