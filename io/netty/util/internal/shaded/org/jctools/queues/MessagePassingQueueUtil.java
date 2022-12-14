package io.netty.util.internal.shaded.org.jctools.queues;

import io.netty.util.internal.shaded.org.jctools.util.PortableJvmInfo;

public final class MessagePassingQueueUtil
{
    public static <E> int drain(final MessagePassingQueue<E> queue, final MessagePassingQueue.Consumer<E> c, final int limit) {
        if (null == c) {
            throw new IllegalArgumentException("c is null");
        }
        if (limit < 0) {
            throw new IllegalArgumentException("limit is negative: " + limit);
        }
        if (limit == 0) {
            return 0;
        }
        int i;
        E e;
        for (i = 0; i < limit && (e = queue.relaxedPoll()) != null; ++i) {
            c.accept(e);
        }
        return i;
    }
    
    public static <E> int drain(final MessagePassingQueue<E> queue, final MessagePassingQueue.Consumer<E> c) {
        if (null == c) {
            throw new IllegalArgumentException("c is null");
        }
        int i = 0;
        E e;
        while ((e = queue.relaxedPoll()) != null) {
            ++i;
            c.accept(e);
        }
        return i;
    }
    
    public static <E> void drain(final MessagePassingQueue<E> queue, final MessagePassingQueue.Consumer<E> c, final MessagePassingQueue.WaitStrategy wait, final MessagePassingQueue.ExitCondition exit) {
        if (null == c) {
            throw new IllegalArgumentException("c is null");
        }
        if (null == wait) {
            throw new IllegalArgumentException("wait is null");
        }
        if (null == exit) {
            throw new IllegalArgumentException("exit condition is null");
        }
        int idleCounter = 0;
        while (exit.keepRunning()) {
            final E e = queue.relaxedPoll();
            if (e == null) {
                idleCounter = wait.idle(idleCounter);
            }
            else {
                idleCounter = 0;
                c.accept(e);
            }
        }
    }
    
    public static <E> void fill(final MessagePassingQueue<E> q, final MessagePassingQueue.Supplier<E> s, final MessagePassingQueue.WaitStrategy wait, final MessagePassingQueue.ExitCondition exit) {
        if (null == wait) {
            throw new IllegalArgumentException("waiter is null");
        }
        if (null == exit) {
            throw new IllegalArgumentException("exit condition is null");
        }
        int idleCounter = 0;
        while (exit.keepRunning()) {
            if (q.fill(s, PortableJvmInfo.RECOMENDED_OFFER_BATCH) == 0) {
                idleCounter = wait.idle(idleCounter);
            }
            else {
                idleCounter = 0;
            }
        }
    }
    
    public static <E> int fillBounded(final MessagePassingQueue<E> q, final MessagePassingQueue.Supplier<E> s) {
        return fillInBatchesToLimit(q, s, PortableJvmInfo.RECOMENDED_OFFER_BATCH, q.capacity());
    }
    
    public static <E> int fillInBatchesToLimit(final MessagePassingQueue<E> q, final MessagePassingQueue.Supplier<E> s, final int batch, final int limit) {
        long result = 0L;
        do {
            final int filled = q.fill(s, batch);
            if (filled == 0) {
                return (int)result;
            }
            result += filled;
        } while (result <= limit);
        return (int)result;
    }
    
    public static <E> int fillUnbounded(final MessagePassingQueue<E> q, final MessagePassingQueue.Supplier<E> s) {
        return fillInBatchesToLimit(q, s, PortableJvmInfo.RECOMENDED_OFFER_BATCH, 4096);
    }
}
