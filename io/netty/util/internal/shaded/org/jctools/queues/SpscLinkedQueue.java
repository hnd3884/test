package io.netty.util.internal.shaded.org.jctools.queues;

public class SpscLinkedQueue<E> extends BaseLinkedQueue<E>
{
    public SpscLinkedQueue() {
        final LinkedQueueNode<E> node = this.newNode();
        this.spProducerNode(node);
        this.spConsumerNode(node);
        node.soNext(null);
    }
    
    @Override
    public boolean offer(final E e) {
        if (null == e) {
            throw new NullPointerException();
        }
        final LinkedQueueNode<E> nextNode = this.newNode(e);
        final LinkedQueueNode<E> oldNode = this.lpProducerNode();
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
        final LinkedQueueNode<E> head;
        LinkedQueueNode<E> tail = head = this.newNode(s.get());
        for (int i = 1; i < limit; ++i) {
            final LinkedQueueNode<E> temp = this.newNode(s.get());
            tail.spNext(temp);
            tail = temp;
        }
        final LinkedQueueNode<E> oldPNode = this.lpProducerNode();
        this.soProducerNode(tail);
        oldPNode.soNext(head);
        return limit;
    }
    
    @Override
    public void fill(final MessagePassingQueue.Supplier<E> s, final MessagePassingQueue.WaitStrategy wait, final MessagePassingQueue.ExitCondition exit) {
        MessagePassingQueueUtil.fill(this, s, wait, exit);
    }
}
