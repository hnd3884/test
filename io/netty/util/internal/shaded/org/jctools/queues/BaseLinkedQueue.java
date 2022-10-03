package io.netty.util.internal.shaded.org.jctools.queues;

import java.util.Iterator;

abstract class BaseLinkedQueue<E> extends BaseLinkedQueuePad2<E>
{
    @Override
    public final Iterator<E> iterator() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public String toString() {
        return this.getClass().getName();
    }
    
    protected final LinkedQueueNode<E> newNode() {
        return new LinkedQueueNode<E>();
    }
    
    protected final LinkedQueueNode<E> newNode(final E e) {
        return new LinkedQueueNode<E>(e);
    }
    
    @Override
    public final int size() {
        LinkedQueueNode<E> chaserNode;
        LinkedQueueNode<E> producerNode;
        int size;
        LinkedQueueNode<E> next;
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
        final LinkedQueueNode<E> consumerNode = this.lvConsumerNode();
        final LinkedQueueNode<E> producerNode = this.lvProducerNode();
        return consumerNode == producerNode;
    }
    
    protected E getSingleConsumerNodeValue(final LinkedQueueNode<E> currConsumerNode, final LinkedQueueNode<E> nextNode) {
        final E nextValue = nextNode.getAndNullValue();
        currConsumerNode.soNext(currConsumerNode);
        this.spConsumerNode(nextNode);
        return nextValue;
    }
    
    @Override
    public E poll() {
        final LinkedQueueNode<E> currConsumerNode = this.lpConsumerNode();
        LinkedQueueNode<E> nextNode = currConsumerNode.lvNext();
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
        final LinkedQueueNode<E> currConsumerNode = this.lpConsumerNode();
        LinkedQueueNode<E> nextNode = currConsumerNode.lvNext();
        if (nextNode != null) {
            return nextNode.lpValue();
        }
        if (currConsumerNode != this.lvProducerNode()) {
            nextNode = this.spinWaitForNextNode(currConsumerNode);
            return nextNode.lpValue();
        }
        return null;
    }
    
    LinkedQueueNode<E> spinWaitForNextNode(final LinkedQueueNode<E> currNode) {
        LinkedQueueNode<E> nextNode;
        while ((nextNode = currNode.lvNext()) == null) {}
        return nextNode;
    }
    
    @Override
    public E relaxedPoll() {
        final LinkedQueueNode<E> currConsumerNode = this.lpConsumerNode();
        final LinkedQueueNode<E> nextNode = currConsumerNode.lvNext();
        if (nextNode != null) {
            return this.getSingleConsumerNodeValue(currConsumerNode, nextNode);
        }
        return null;
    }
    
    @Override
    public E relaxedPeek() {
        final LinkedQueueNode<E> nextNode = this.lpConsumerNode().lvNext();
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
        LinkedQueueNode<E> chaserNode = this.lpConsumerNode();
        for (int i = 0; i < limit; ++i) {
            final LinkedQueueNode<E> nextNode = chaserNode.lvNext();
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
