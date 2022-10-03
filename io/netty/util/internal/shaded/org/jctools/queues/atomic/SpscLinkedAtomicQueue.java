package io.netty.util.internal.shaded.org.jctools.queues.atomic;

import io.netty.util.internal.shaded.org.jctools.queues.MessagePassingQueueUtil;
import io.netty.util.internal.shaded.org.jctools.queues.MessagePassingQueue;

public class SpscLinkedAtomicQueue<E> extends BaseLinkedAtomicQueue<E>
{
    public SpscLinkedAtomicQueue() {
        final LinkedQueueAtomicNode<E> node = this.newNode();
        this.spProducerNode(node);
        this.spConsumerNode(node);
        node.soNext(null);
    }
    
    @Override
    public boolean offer(final E e) {
        if (null == e) {
            throw new NullPointerException();
        }
        final LinkedQueueAtomicNode<E> nextNode = this.newNode(e);
        final LinkedQueueAtomicNode<E> oldNode = this.lpProducerNode();
        this.soProducerNode(nextNode);
        oldNode.soNext(nextNode);
        return true;
    }
    
    @Override
    public int fill(final MessagePassingQueue.Supplier<E> s) {
        return MessagePassingQueueUtil.fillUnbounded(this, s);
    }
    
    @Override
    public int fill(final MessagePassingQueue.Supplier<E> s, final int limit) {
        if (null == s) {
            throw new IllegalArgumentException("supplier is null");
        }
        if (limit < 0) {
            throw new IllegalArgumentException("limit is negative:" + limit);
        }
        if (limit == 0) {
            return 0;
        }
        final LinkedQueueAtomicNode<E> head;
        LinkedQueueAtomicNode<E> tail = head = this.newNode(s.get());
        for (int i = 1; i < limit; ++i) {
            final LinkedQueueAtomicNode<E> temp = this.newNode(s.get());
            tail.spNext(temp);
            tail = temp;
        }
        final LinkedQueueAtomicNode<E> oldPNode = this.lpProducerNode();
        this.soProducerNode(tail);
        oldPNode.soNext(head);
        return limit;
    }
    
    @Override
    public void fill(final MessagePassingQueue.Supplier<E> s, final MessagePassingQueue.WaitStrategy wait, final MessagePassingQueue.ExitCondition exit) {
        MessagePassingQueueUtil.fill(this, s, wait, exit);
    }
}
