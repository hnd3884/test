package io.netty.util.internal.shaded.org.jctools.queues;

import io.netty.util.internal.shaded.org.jctools.util.UnsafeAccess;

abstract class BaseLinkedQueueConsumerNodeRef<E> extends BaseLinkedQueuePad1<E>
{
    private static final long C_NODE_OFFSET;
    private LinkedQueueNode<E> consumerNode;
    
    final void spConsumerNode(final LinkedQueueNode<E> newValue) {
        this.consumerNode = newValue;
    }
    
    final LinkedQueueNode<E> lvConsumerNode() {
        return (LinkedQueueNode)UnsafeAccess.UNSAFE.getObjectVolatile(this, BaseLinkedQueueConsumerNodeRef.C_NODE_OFFSET);
    }
    
    final LinkedQueueNode<E> lpConsumerNode() {
        return this.consumerNode;
    }
    
    static {
        C_NODE_OFFSET = UnsafeAccess.fieldOffset(BaseLinkedQueueConsumerNodeRef.class, "consumerNode");
    }
}
