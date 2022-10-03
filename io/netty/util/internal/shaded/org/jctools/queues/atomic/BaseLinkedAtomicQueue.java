package io.netty.util.internal.shaded.org.jctools.queues.atomic;

import io.netty.util.internal.shaded.org.jctools.queues.MessagePassingQueueUtil;
import io.netty.util.internal.shaded.org.jctools.queues.MessagePassingQueue;
import java.util.Iterator;

abstract class BaseLinkedAtomicQueue<E> extends BaseLinkedAtomicQueuePad2<E>
{
    @Override
    public final Iterator<E> iterator() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public String toString() {
        return this.getClass().getName();
    }
    
    protected final LinkedQueueAtomicNode<E> newNode() {
        return new LinkedQueueAtomicNode<E>();
    }
    
    protected final LinkedQueueAtomicNode<E> newNode(final E e) {
        return new LinkedQueueAtomicNode<E>(e);
    }
    
    @Override
    public final int size() {
        LinkedQueueAtomicNode<E> chaserNode;
        LinkedQueueAtomicNode<E> producerNode;
        int size;
        LinkedQueueAtomicNode<E> next;
        for (chaserNode = this.lvConsumerNode(), producerNode = this.lvProducerNode(), size = 0; chaserNode != producerNode && chaserNode != null && size < Integer.MAX_VALUE; chaserNode = next, ++size) {
            next = chaserNode.lvNext();
            if (next == chaserNode) {
                return size;
            }
        }
        return size;
    }
    
    @Override
    public boolean isEmpty() {
        final LinkedQueueAtomicNode<E> consumerNode = this.lvConsumerNode();
        final LinkedQueueAtomicNode<E> producerNode = this.lvProducerNode();
        return consumerNode == producerNode;
    }
    
    protected E getSingleConsumerNodeValue(final LinkedQueueAtomicNode<E> currConsumerNode, final LinkedQueueAtomicNode<E> nextNode) {
        final E nextValue = nextNode.getAndNullValue();
        currConsumerNode.soNext(currConsumerNode);
        this.spConsumerNode(nextNode);
        return nextValue;
    }
    
    @Override
    public E poll() {
        final LinkedQueueAtomicNode<E> currConsumerNode = this.lpConsumerNode();
        LinkedQueueAtomicNode<E> nextNode = currConsumerNode.lvNext();
        if (nextNode != null) {
            return this.getSingleConsumerNodeValue(currConsumerNode, nextNode);
        }
        if (currConsumerNode != this.lvProducerNode()) {
            nextNode = this.spinWaitForNextNode(currConsumerNode);
            return this.getSingleConsumerNodeValue(currConsumerNode, nextNode);
        }
        return null;
    }
    
    @Override
    public E peek() {
        final LinkedQueueAtomicNode<E> currConsumerNode = this.lpConsumerNode();
        LinkedQueueAtomicNode<E> nextNode = currConsumerNode.lvNext();
        if (nextNode != null) {
            return nextNode.lpValue();
        }
        if (currConsumerNode != this.lvProducerNode()) {
            nextNode = this.spinWaitForNextNode(currConsumerNode);
            return nextNode.lpValue();
        }
        return null;
    }
    
    LinkedQueueAtomicNode<E> spinWaitForNextNode(final LinkedQueueAtomicNode<E> currNode) {
        LinkedQueueAtomicNode<E> nextNode;
        while ((nextNode = currNode.lvNext()) == null) {}
        return nextNode;
    }
    
    @Override
    public E relaxedPoll() {
        final LinkedQueueAtomicNode<E> currConsumerNode = this.lpConsumerNode();
        final LinkedQueueAtomicNode<E> nextNode = currConsumerNode.lvNext();
        if (nextNode != null) {
            return this.getSingleConsumerNodeValue(currConsumerNode, nextNode);
        }
        return null;
    }
    
    @Override
    public E relaxedPeek() {
        final LinkedQueueAtomicNode<E> nextNode = this.lpConsumerNode().lvNext();
        if (nextNode != null) {
            return nextNode.lpValue();
        }
        return null;
    }
    
    @Override
    public boolean relaxedOffer(final E e) {
        return this.offer(e);
    }
    
    @Override
    public int drain(final MessagePassingQueue.Consumer<E> c, final int limit) {
        if (null == c) {
            throw new IllegalArgumentException("c is null");
        }
        if (limit < 0) {
            throw new IllegalArgumentException("limit is negative: " + limit);
        }
        if (limit == 0) {
            return 0;
        }
        LinkedQueueAtomicNode<E> chaserNode = this.lpConsumerNode();
        for (int i = 0; i < limit; ++i) {
            final LinkedQueueAtomicNode<E> nextNode = chaserNode.lvNext();
            if (nextNode == null) {
                return i;
            }
            final E nextValue = this.getSingleConsumerNodeValue(chaserNode, nextNode);
            chaserNode = nextNode;
            c.accept(nextValue);
        }
        return limit;
    }
    
    @Override
    public int drain(final MessagePassingQueue.Consumer<E> c) {
        return MessagePassingQueueUtil.drain(this, c);
    }
    
    @Override
    public void drain(final MessagePassingQueue.Consumer<E> c, final MessagePassingQueue.WaitStrategy wait, final MessagePassingQueue.ExitCondition exit) {
        MessagePassingQueueUtil.drain(this, c, wait, exit);
    }
    
    @Override
    public int capacity() {
        return -1;
    }
}
