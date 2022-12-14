package org.glassfish.jersey.internal.util.collection;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.BlockingQueue;

public interface TransferQueue<E> extends BlockingQueue<E>
{
    boolean tryTransfer(final E p0);
    
    void transfer(final E p0) throws InterruptedException;
    
    boolean tryTransfer(final E p0, final long p1, final TimeUnit p2) throws InterruptedException;
    
    boolean hasWaitingConsumer();
    
    int getWaitingConsumerCount();
}
