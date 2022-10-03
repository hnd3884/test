package org.glassfish.jersey.internal.jsr166;

import java.util.concurrent.locks.LockSupport;
import sun.misc.Unsafe;
import sun.misc.Contended;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.BiPredicate;
import java.util.function.BiConsumer;
import java.util.concurrent.Executor;

public class SubmissionPublisher<T> implements Flow.Publisher<T>, AutoCloseable
{
    static final int BUFFER_CAPACITY_LIMIT = 1073741824;
    private static final Executor ASYNC_POOL;
    BufferedSubscription<T> clients;
    volatile boolean closed;
    volatile Throwable closedException;
    final Executor executor;
    final BiConsumer<? super Flow.Subscriber<? super T>, ? super Throwable> onNextHandler;
    final int maxBufferCapacity;
    
    static final int roundCapacity(final int cap) {
        int n = cap - 1;
        n |= n >>> 1;
        n |= n >>> 2;
        n |= n >>> 4;
        n |= n >>> 8;
        n |= n >>> 16;
        return (n <= 0) ? 1 : ((n >= 1073741824) ? 1073741824 : (n + 1));
    }
    
    public SubmissionPublisher(final Executor executor, final int maxBufferCapacity, final BiConsumer<? super Flow.Subscriber<? super T>, ? super Throwable> handler) {
        if (executor == null) {
            throw new NullPointerException();
        }
        if (maxBufferCapacity <= 0) {
            throw new IllegalArgumentException("capacity must be positive");
        }
        this.executor = executor;
        this.onNextHandler = handler;
        this.maxBufferCapacity = roundCapacity(maxBufferCapacity);
    }
    
    public SubmissionPublisher(final Executor executor, final int maxBufferCapacity) {
        this(executor, maxBufferCapacity, null);
    }
    
    public SubmissionPublisher() {
        this(SubmissionPublisher.ASYNC_POOL, Flow.defaultBufferSize(), null);
    }
    
    @Override
    public void subscribe(final Flow.Subscriber<? super T> subscriber) {
        if (subscriber == null) {
            throw new NullPointerException();
        }
        final BufferedSubscription<T> subscription = new BufferedSubscription<T>(subscriber, this.executor, this.onNextHandler, this.maxBufferCapacity);
        synchronized (this) {
            BufferedSubscription<T> b = this.clients;
            BufferedSubscription<T> pred = null;
            while (b != null) {
                final BufferedSubscription<T> next = b.next;
                if (b.isDisabled()) {
                    b.next = null;
                    if (pred == null) {
                        this.clients = next;
                    }
                    else {
                        pred.next = next;
                    }
                }
                else {
                    if (subscriber.equals(b.subscriber)) {
                        b.onError(new IllegalStateException("Duplicate subscribe"));
                        return;
                    }
                    pred = b;
                }
                b = next;
            }
            subscription.onSubscribe();
            final Throwable ex;
            if ((ex = this.closedException) != null) {
                subscription.onError(ex);
                return;
            }
            if (this.closed) {
                subscription.onComplete();
                return;
            }
            if (pred == null) {
                this.clients = subscription;
                return;
            }
            pred.next = subscription;
        }
    }
    
    public int submit(final T item) {
        if (item == null) {
            throw new NullPointerException();
        }
        int lag = 0;
        final boolean complete;
        synchronized (this) {
            complete = this.closed;
            BufferedSubscription<T> b = this.clients;
            if (!complete) {
                BufferedSubscription<T> pred = null;
                BufferedSubscription<T> r = null;
                BufferedSubscription<T> rtail = null;
                while (b != null) {
                    final BufferedSubscription<T> next = b.next;
                    final int stat = b.offer(item);
                    if (stat < 0) {
                        b.next = null;
                        if (pred == null) {
                            this.clients = next;
                        }
                        else {
                            pred.next = next;
                        }
                    }
                    else {
                        if (stat > lag) {
                            lag = stat;
                        }
                        else if (stat == 0) {
                            b.nextRetry = null;
                            if (rtail == null) {
                                r = b;
                            }
                            else {
                                rtail.nextRetry = b;
                            }
                            rtail = b;
                        }
                        pred = b;
                    }
                    b = next;
                }
                while (r != null) {
                    final BufferedSubscription<T> nextRetry = r.nextRetry;
                    r.nextRetry = null;
                    final int stat = r.submit(item);
                    if (stat > lag) {
                        lag = stat;
                    }
                    else if (stat < 0 && this.clients == r) {
                        this.clients = r.next;
                    }
                    r = nextRetry;
                }
            }
        }
        if (complete) {
            throw new IllegalStateException("Closed");
        }
        return lag;
    }
    
    public int offer(final T item, final BiPredicate<Flow.Subscriber<? super T>, ? super T> onDrop) {
        return this.doOffer(0L, item, onDrop);
    }
    
    public int offer(final T item, final long timeout, final TimeUnit unit, final BiPredicate<Flow.Subscriber<? super T>, ? super T> onDrop) {
        return this.doOffer(unit.toNanos(timeout), item, onDrop);
    }
    
    final int doOffer(final long nanos, final T item, final BiPredicate<Flow.Subscriber<? super T>, ? super T> onDrop) {
        if (item == null) {
            throw new NullPointerException();
        }
        int lag = 0;
        int drops = 0;
        final boolean complete;
        synchronized (this) {
            complete = this.closed;
            BufferedSubscription<T> b = this.clients;
            if (!complete) {
                BufferedSubscription<T> pred = null;
                BufferedSubscription<T> r = null;
                BufferedSubscription<T> rtail = null;
                while (b != null) {
                    final BufferedSubscription<T> next = b.next;
                    final int stat = b.offer(item);
                    if (stat < 0) {
                        b.next = null;
                        if (pred == null) {
                            this.clients = next;
                        }
                        else {
                            pred.next = next;
                        }
                    }
                    else {
                        if (stat > lag) {
                            lag = stat;
                        }
                        else if (stat == 0) {
                            b.nextRetry = null;
                            if (rtail == null) {
                                r = b;
                            }
                            else {
                                rtail.nextRetry = b;
                            }
                            rtail = b;
                        }
                        else if (stat > lag) {
                            lag = stat;
                        }
                        pred = b;
                    }
                    b = next;
                }
                while (r != null) {
                    final BufferedSubscription<T> nextRetry = r.nextRetry;
                    r.nextRetry = null;
                    int stat = (nanos > 0L) ? r.timedOffer(item, nanos) : r.offer(item);
                    if (stat == 0 && onDrop != null && onDrop.test(r.subscriber, item)) {
                        stat = r.offer(item);
                    }
                    if (stat == 0) {
                        ++drops;
                    }
                    else if (stat > lag) {
                        lag = stat;
                    }
                    else if (stat < 0 && this.clients == r) {
                        this.clients = r.next;
                    }
                    r = nextRetry;
                }
            }
        }
        if (complete) {
            throw new IllegalStateException("Closed");
        }
        return (drops > 0) ? (-drops) : lag;
    }
    
    @Override
    public void close() {
        if (!this.closed) {
            BufferedSubscription<T> b;
            synchronized (this) {
                b = this.clients;
                this.clients = null;
                this.closed = true;
            }
            while (b != null) {
                final BufferedSubscription<T> next = b.next;
                b.next = null;
                b.onComplete();
                b = next;
            }
        }
    }
    
    public void closeExceptionally(final Throwable error) {
        if (error == null) {
            throw new NullPointerException();
        }
        if (!this.closed) {
            BufferedSubscription<T> b;
            synchronized (this) {
                b = this.clients;
                this.clients = null;
                this.closed = true;
                this.closedException = error;
            }
            while (b != null) {
                final BufferedSubscription<T> next = b.next;
                b.next = null;
                b.onError(error);
                b = next;
            }
        }
    }
    
    public boolean isClosed() {
        return this.closed;
    }
    
    public Throwable getClosedException() {
        return this.closedException;
    }
    
    public boolean hasSubscribers() {
        boolean nonEmpty = false;
        if (!this.closed) {
            synchronized (this) {
                BufferedSubscription<T> clients;
                for (BufferedSubscription<T> b = this.clients; b != null; b = clients) {
                    final BufferedSubscription<T> next = b.next;
                    if (!b.isDisabled()) {
                        nonEmpty = true;
                        break;
                    }
                    b.next = null;
                    clients = next;
                    this.clients = clients;
                }
            }
        }
        return nonEmpty;
    }
    
    public int getNumberOfSubscribers() {
        int count = 0;
        if (!this.closed) {
            synchronized (this) {
                BufferedSubscription<T> pred = null;
                BufferedSubscription<T> next;
                for (BufferedSubscription<T> b = this.clients; b != null; b = next) {
                    next = b.next;
                    if (b.isDisabled()) {
                        b.next = null;
                        if (pred == null) {
                            this.clients = next;
                        }
                        else {
                            pred.next = next;
                        }
                    }
                    else {
                        pred = b;
                        ++count;
                    }
                }
            }
        }
        return count;
    }
    
    public Executor getExecutor() {
        return this.executor;
    }
    
    public int getMaxBufferCapacity() {
        return this.maxBufferCapacity;
    }
    
    public List<Flow.Subscriber<? super T>> getSubscribers() {
        final ArrayList<Flow.Subscriber<? super T>> subs = new ArrayList<Flow.Subscriber<? super T>>();
        synchronized (this) {
            final BufferedSubscription<T> pred = null;
            BufferedSubscription<T> next;
            for (BufferedSubscription<T> b = this.clients; b != null; b = next) {
                next = b.next;
                if (b.isDisabled()) {
                    b.next = null;
                    if (pred == null) {
                        this.clients = next;
                    }
                    else {
                        pred.next = next;
                    }
                }
                else {
                    subs.add(b.subscriber);
                }
            }
        }
        return subs;
    }
    
    public boolean isSubscribed(final Flow.Subscriber<? super T> subscriber) {
        if (subscriber == null) {
            throw new NullPointerException();
        }
        if (!this.closed) {
            synchronized (this) {
                BufferedSubscription<T> pred = null;
                BufferedSubscription<T> next;
                for (BufferedSubscription<T> b = this.clients; b != null; b = next) {
                    next = b.next;
                    if (b.isDisabled()) {
                        b.next = null;
                        if (pred == null) {
                            this.clients = next;
                        }
                        else {
                            pred.next = next;
                        }
                    }
                    else {
                        if (subscriber.equals(b.subscriber)) {
                            return true;
                        }
                        pred = b;
                    }
                }
            }
        }
        return false;
    }
    
    public long estimateMinimumDemand() {
        long min = Long.MAX_VALUE;
        boolean nonEmpty = false;
        synchronized (this) {
            BufferedSubscription<T> pred = null;
            BufferedSubscription<T> next;
            for (BufferedSubscription<T> b = this.clients; b != null; b = next) {
                next = b.next;
                final int n;
                if ((n = b.estimateLag()) < 0) {
                    b.next = null;
                    if (pred == null) {
                        this.clients = next;
                    }
                    else {
                        pred.next = next;
                    }
                }
                else {
                    final long d;
                    if ((d = b.demand - n) < min) {
                        min = d;
                    }
                    nonEmpty = true;
                    pred = b;
                }
            }
        }
        return nonEmpty ? min : 0L;
    }
    
    public int estimateMaximumLag() {
        int max = 0;
        synchronized (this) {
            BufferedSubscription<T> pred = null;
            BufferedSubscription<T> next;
            for (BufferedSubscription<T> b = this.clients; b != null; b = next) {
                next = b.next;
                final int n;
                if ((n = b.estimateLag()) < 0) {
                    b.next = null;
                    if (pred == null) {
                        this.clients = next;
                    }
                    else {
                        pred.next = next;
                    }
                }
                else {
                    if (n > max) {
                        max = n;
                    }
                    pred = b;
                }
            }
        }
        return max;
    }
    
    public CompletableFuture<Void> consume(final Consumer<? super T> consumer) {
        if (consumer == null) {
            throw new NullPointerException();
        }
        final CompletableFuture<Void> status = new CompletableFuture<Void>();
        this.subscribe(new ConsumerSubscriber<Object>(status, consumer));
        return status;
    }
    
    static {
        ASYNC_POOL = (Executor)((ForkJoinPool.getCommonPoolParallelism() > 1) ? ForkJoinPool.commonPool() : new ThreadPerTaskExecutor());
    }
    
    private static final class ThreadPerTaskExecutor implements Executor
    {
        @Override
        public void execute(final Runnable r) {
            new Thread(r).start();
        }
    }
    
    private static final class ConsumerSubscriber<T> implements Flow.Subscriber<T>
    {
        final CompletableFuture<Void> status;
        final Consumer<? super T> consumer;
        Flow.Subscription subscription;
        
        ConsumerSubscriber(final CompletableFuture<Void> status, final Consumer<? super T> consumer) {
            this.status = status;
            this.consumer = consumer;
        }
        
        @Override
        public final void onSubscribe(final Flow.Subscription subscription) {
            this.subscription = subscription;
            this.status.whenComplete((v, e) -> subscription.cancel());
            if (!this.status.isDone()) {
                subscription.request(Long.MAX_VALUE);
            }
        }
        
        @Override
        public final void onError(final Throwable ex) {
            this.status.completeExceptionally(ex);
        }
        
        @Override
        public final void onComplete() {
            this.status.complete(null);
        }
        
        @Override
        public final void onNext(final T item) {
            try {
                this.consumer.accept(item);
            }
            catch (final Throwable ex) {
                this.subscription.cancel();
                this.status.completeExceptionally(ex);
            }
        }
    }
    
    static final class ConsumerTask<T> extends ForkJoinTask<Void> implements Runnable, CompletableFuture.AsynchronousCompletionTask
    {
        final BufferedSubscription<T> consumer;
        
        ConsumerTask(final BufferedSubscription<T> consumer) {
            this.consumer = consumer;
        }
        
        @Override
        public final Void getRawResult() {
            return null;
        }
        
        public final void setRawResult(final Void v) {
        }
        
        public final boolean exec() {
            this.consumer.consume();
            return false;
        }
        
        @Override
        public final void run() {
            this.consumer.consume();
        }
    }
    
    @Contended
    private static final class BufferedSubscription<T> implements Flow.Subscription, ForkJoinPool.ManagedBlocker
    {
        long timeout;
        volatile long demand;
        int maxCapacity;
        int putStat;
        volatile int ctl;
        volatile int head;
        int tail;
        Object[] array;
        Flow.Subscriber<? super T> subscriber;
        Executor executor;
        BiConsumer<? super Flow.Subscriber<? super T>, ? super Throwable> onNextHandler;
        volatile Throwable pendingError;
        volatile Thread waiter;
        T putItem;
        BufferedSubscription<T> next;
        BufferedSubscription<T> nextRetry;
        static final int ACTIVE = 1;
        static final int CONSUME = 2;
        static final int DISABLED = 4;
        static final int ERROR = 8;
        static final int SUBSCRIBE = 16;
        static final int COMPLETE = 32;
        static final long INTERRUPTED = -1L;
        static final int DEFAULT_INITIAL_CAP = 32;
        private static final Unsafe U;
        private static final long CTL;
        private static final long TAIL;
        private static final long HEAD;
        private static final long DEMAND;
        private static final int ABASE;
        private static final int ASHIFT;
        
        BufferedSubscription(final Flow.Subscriber<? super T> subscriber, final Executor executor, final BiConsumer<? super Flow.Subscriber<? super T>, ? super Throwable> onNextHandler, final int maxBufferCapacity) {
            this.subscriber = subscriber;
            this.executor = executor;
            this.onNextHandler = onNextHandler;
            this.maxCapacity = maxBufferCapacity;
            this.array = new Object[(maxBufferCapacity < 32) ? ((maxBufferCapacity < 2) ? 2 : maxBufferCapacity) : 32];
        }
        
        @Override
        public String toString() {
            if (this.subscriber != null) {
                return this.subscriber.toString();
            }
            return super.toString();
        }
        
        final boolean isDisabled() {
            return this.ctl == 4;
        }
        
        final int estimateLag() {
            int n;
            return (this.ctl == 4) ? -1 : (((n = this.tail - this.head) > 0) ? n : 0);
        }
        
        final int offer(final T item) {
            final int h = this.head;
            final int t = this.tail;
            final Object[] a = this.array;
            final int cap;
            final int size;
            int stat;
            if (a != null && (cap = a.length) > 0 && cap >= (size = t + 1 - h)) {
                a[cap - 1 & t] = item;
                this.tail = t + 1;
                stat = size;
            }
            else {
                stat = this.growAndAdd(a, item);
            }
            return (stat > 0 && (this.ctl & 0x3) != 0x3) ? this.startOnOffer(stat) : stat;
        }
        
        private int growAndAdd(final Object[] a, final T item) {
            int cap;
            int stat;
            boolean alloc;
            if ((this.ctl & 0xC) != 0x0) {
                cap = 0;
                stat = -1;
                alloc = false;
            }
            else if (a == null || (cap = a.length) <= 0) {
                cap = 0;
                stat = 1;
                alloc = true;
            }
            else {
                BufferedSubscription.U.fullFence();
                final int h = this.head;
                final int t = this.tail;
                final int size = t + 1 - h;
                if (cap >= size) {
                    a[cap - 1 & t] = item;
                    this.tail = t + 1;
                    stat = size;
                    alloc = false;
                }
                else if (cap >= this.maxCapacity) {
                    stat = 0;
                    alloc = false;
                }
                else {
                    stat = cap + 1;
                    alloc = true;
                }
            }
            if (alloc) {
                final int newCap = (cap > 0) ? (cap << 1) : 1;
                if (newCap <= cap) {
                    stat = 0;
                }
                else {
                    Object[] newArray = null;
                    try {
                        newArray = new Object[newCap];
                    }
                    catch (final Throwable t3) {}
                    if (newArray == null) {
                        if (cap > 0) {
                            this.maxCapacity = cap;
                        }
                        stat = 0;
                    }
                    else {
                        this.array = newArray;
                        final int t2 = this.tail;
                        final int newMask = newCap - 1;
                        if (a != null && cap > 0) {
                            final int mask = cap - 1;
                            for (int j = this.head; j != t2; ++j) {
                                final long k = ((long)(j & mask) << BufferedSubscription.ASHIFT) + BufferedSubscription.ABASE;
                                final Object x = BufferedSubscription.U.getObjectVolatile(a, k);
                                if (x != null && BufferedSubscription.U.compareAndSwapObject(a, k, x, null)) {
                                    newArray[j & newMask] = x;
                                }
                            }
                        }
                        newArray[t2 & newMask] = item;
                        this.tail = t2 + 1;
                    }
                }
            }
            return stat;
        }
        
        final int submit(final T item) {
            int stat;
            if ((stat = this.offer(item)) == 0) {
                this.putItem = item;
                this.timeout = 0L;
                this.putStat = 0;
                if ((stat = this.putStat) == 0) {
                    try {
                        ForkJoinPool.managedBlock(this);
                    }
                    catch (final InterruptedException ie) {
                        this.timeout = -1L;
                    }
                    stat = this.putStat;
                }
                if (this.timeout < 0L) {
                    Thread.currentThread().interrupt();
                }
            }
            return stat;
        }
        
        final int timedOffer(final T item, final long nanos) {
            int stat;
            if ((stat = this.offer(item)) == 0) {
                this.timeout = nanos;
                if (nanos > 0L) {
                    this.putItem = item;
                    this.putStat = 0;
                    if ((stat = this.putStat) == 0) {
                        try {
                            ForkJoinPool.managedBlock(this);
                        }
                        catch (final InterruptedException ie) {
                            this.timeout = -1L;
                        }
                        stat = this.putStat;
                    }
                    if (this.timeout < 0L) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
            return stat;
        }
        
        private int startOnOffer(int stat) {
            int c;
            Executor e;
            while ((c = this.ctl) != 4 && (e = this.executor) != null) {
                if ((c & 0x1) != 0x0) {
                    if ((c & 0x2) != 0x0) {
                        return stat;
                    }
                    if (BufferedSubscription.U.compareAndSwapInt(this, BufferedSubscription.CTL, c, c | 0x2)) {
                        return stat;
                    }
                    continue;
                }
                else {
                    if (this.demand == 0L) {
                        return stat;
                    }
                    if (this.tail == this.head) {
                        return stat;
                    }
                    if (!BufferedSubscription.U.compareAndSwapInt(this, BufferedSubscription.CTL, c, c | 0x3)) {
                        continue;
                    }
                    try {
                        e.execute(new ConsumerTask<Object>(this));
                        return stat;
                    }
                    catch (final RuntimeException | Error ex) {
                        while (((c = this.ctl) & 0x4) == 0x0 && (c & 0x1) != 0x0 && !BufferedSubscription.U.compareAndSwapInt(this, BufferedSubscription.CTL, c, c & 0xFFFFFFFE)) {}
                        throw ex;
                    }
                }
            }
            stat = -1;
            return stat;
        }
        
        private void signalWaiter(final Thread w) {
            this.waiter = null;
            LockSupport.unpark(w);
        }
        
        private void detach() {
            final Thread w = this.waiter;
            this.executor = null;
            this.subscriber = null;
            this.pendingError = null;
            this.signalWaiter(w);
        }
        
        final void onError(final Throwable ex) {
            int c;
            while (((c = this.ctl) & 0xC) == 0x0) {
                if ((c & 0x1) != 0x0) {
                    this.pendingError = ex;
                    if (!BufferedSubscription.U.compareAndSwapInt(this, BufferedSubscription.CTL, c, c | 0x8)) {
                        continue;
                    }
                }
                else {
                    if (!BufferedSubscription.U.compareAndSwapInt(this, BufferedSubscription.CTL, c, 4)) {
                        continue;
                    }
                    final Flow.Subscriber<? super T> s = this.subscriber;
                    if (s != null && ex != null) {
                        try {
                            s.onError(ex);
                        }
                        catch (final Throwable t) {}
                    }
                    this.detach();
                }
            }
        }
        
        private void startOrDisable() {
            final Executor e;
            if ((e = this.executor) != null) {
                try {
                    e.execute(new ConsumerTask<Object>(this));
                }
                catch (final Throwable ex) {
                    Block_5: {
                        int c;
                        while ((c = this.ctl) != 4) {
                            if ((c & 0x1) == 0x0) {
                                break;
                            }
                            if (BufferedSubscription.U.compareAndSwapInt(this, BufferedSubscription.CTL, c, c & 0xFFFFFFFE)) {
                                break Block_5;
                            }
                        }
                        return;
                    }
                    this.onError(ex);
                }
            }
        }
        
        final void onComplete() {
            int c;
            while ((c = this.ctl) != 4) {
                if (BufferedSubscription.U.compareAndSwapInt(this, BufferedSubscription.CTL, c, c | 0x23)) {
                    if ((c & 0x1) == 0x0) {
                        this.startOrDisable();
                    }
                }
            }
        }
        
        final void onSubscribe() {
            int c;
            while ((c = this.ctl) != 4) {
                if (BufferedSubscription.U.compareAndSwapInt(this, BufferedSubscription.CTL, c, c | 0x13)) {
                    if ((c & 0x1) == 0x0) {
                        this.startOrDisable();
                    }
                }
            }
        }
        
        @Override
        public void cancel() {
            int c;
            while ((c = this.ctl) != 4) {
                if ((c & 0x1) != 0x0) {
                    if (!BufferedSubscription.U.compareAndSwapInt(this, BufferedSubscription.CTL, c, c | 0xA)) {
                        continue;
                    }
                }
                else {
                    if (!BufferedSubscription.U.compareAndSwapInt(this, BufferedSubscription.CTL, c, 4)) {
                        continue;
                    }
                    this.detach();
                }
            }
        }
        
        @Override
        public void request(final long n) {
            if (n > 0L) {
                long prev;
                long d;
                do {
                    prev = this.demand;
                    if ((d = prev + n) < prev) {
                        d = Long.MAX_VALUE;
                    }
                } while (!BufferedSubscription.U.compareAndSwapLong(this, BufferedSubscription.DEMAND, prev, d));
                int c;
                while ((c = this.ctl) != 4) {
                    if ((c & 0x1) != 0x0) {
                        if ((c & 0x2) != 0x0) {
                            break;
                        }
                        if (BufferedSubscription.U.compareAndSwapInt(this, BufferedSubscription.CTL, c, c | 0x2)) {
                            break;
                        }
                    }
                    else {
                        final int h;
                        if ((h = this.head) != this.tail) {
                            if (BufferedSubscription.U.compareAndSwapInt(this, BufferedSubscription.CTL, c, c | 0x3)) {
                                this.startOrDisable();
                                break;
                            }
                        }
                        else if (this.head == h && this.tail == h) {
                            break;
                        }
                    }
                    if (this.demand != 0L) {
                        continue;
                    }
                }
                return;
            }
            if (n < 0L) {
                this.onError(new IllegalArgumentException("negative subscription request"));
            }
        }
        
        @Override
        public final boolean isReleasable() {
            final T item = this.putItem;
            if (item != null) {
                if ((this.putStat = this.offer(item)) == 0) {
                    return false;
                }
                this.putItem = null;
            }
            return true;
        }
        
        @Override
        public final boolean block() {
            final T item = this.putItem;
            if (item != null) {
                this.putItem = null;
                long nanos = this.timeout;
                final long deadline = (nanos > 0L) ? (System.nanoTime() + nanos) : 0L;
                while ((this.putStat = this.offer(item)) == 0) {
                    if (Thread.interrupted()) {
                        this.timeout = -1L;
                        if (nanos > 0L) {
                            break;
                        }
                        continue;
                    }
                    else {
                        if (nanos > 0L && (nanos = deadline - System.nanoTime()) <= 0L) {
                            break;
                        }
                        if (this.waiter == null) {
                            this.waiter = Thread.currentThread();
                        }
                        else {
                            if (nanos > 0L) {
                                LockSupport.parkNanos(this, nanos);
                            }
                            else {
                                LockSupport.park(this);
                            }
                            this.waiter = null;
                        }
                    }
                }
            }
            this.waiter = null;
            return true;
        }
        
        final void consume() {
            int h = this.head;
            final Flow.Subscriber<? super T> s;
            if ((s = this.subscriber) != null) {
                while (true) {
                    final long d = this.demand;
                    final int c;
                    if (((c = this.ctl) & 0x1C) != 0x0) {
                        if (!this.checkControl(s, c)) {
                            break;
                        }
                        continue;
                    }
                    else {
                        final Object[] a;
                        final int n;
                        final long i;
                        final Object x;
                        if ((a = this.array) == null || h == this.tail || (n = a.length) == 0 || (x = BufferedSubscription.U.getObjectVolatile(a, i = ((long)(n - 1 & h) << BufferedSubscription.ASHIFT) + BufferedSubscription.ABASE)) == null) {
                            if (!this.checkEmpty(s, c)) {
                                break;
                            }
                            continue;
                        }
                        else if (d == 0L) {
                            if (!this.checkDemand(c)) {
                                break;
                            }
                            continue;
                        }
                        else {
                            if (((c & 0x2) == 0x0 && !BufferedSubscription.U.compareAndSwapInt(this, BufferedSubscription.CTL, c, c | 0x2)) || !BufferedSubscription.U.compareAndSwapObject(a, i, x, null)) {
                                continue;
                            }
                            BufferedSubscription.U.putOrderedInt(this, BufferedSubscription.HEAD, ++h);
                            BufferedSubscription.U.getAndAddLong(this, BufferedSubscription.DEMAND, -1L);
                            final Thread w;
                            if ((w = this.waiter) != null) {
                                this.signalWaiter(w);
                            }
                            try {
                                final T y = (T)x;
                                s.onNext(y);
                            }
                            catch (final Throwable ex) {
                                this.handleOnNext(s, ex);
                            }
                        }
                    }
                }
            }
        }
        
        private boolean checkControl(final Flow.Subscriber<? super T> s, final int c) {
            boolean stat = true;
            if ((c & 0x10) != 0x0) {
                if (BufferedSubscription.U.compareAndSwapInt(this, BufferedSubscription.CTL, c, c & 0xFFFFFFEF)) {
                    try {
                        if (s != null) {
                            s.onSubscribe(this);
                        }
                    }
                    catch (final Throwable ex) {
                        this.onError(ex);
                    }
                }
            }
            else if ((c & 0x8) != 0x0) {
                final Throwable ex = this.pendingError;
                this.ctl = 4;
                if (ex != null) {
                    try {
                        if (s != null) {
                            s.onError(ex);
                        }
                    }
                    catch (final Throwable t) {}
                }
            }
            else {
                this.detach();
                stat = false;
            }
            return stat;
        }
        
        private boolean checkEmpty(final Flow.Subscriber<? super T> s, final int c) {
            boolean stat = true;
            if (this.head == this.tail) {
                if ((c & 0x2) != 0x0) {
                    BufferedSubscription.U.compareAndSwapInt(this, BufferedSubscription.CTL, c, c & 0xFFFFFFFD);
                }
                else if ((c & 0x20) != 0x0) {
                    if (BufferedSubscription.U.compareAndSwapInt(this, BufferedSubscription.CTL, c, 4)) {
                        try {
                            if (s != null) {
                                s.onComplete();
                            }
                        }
                        catch (final Throwable t) {}
                    }
                }
                else if (BufferedSubscription.U.compareAndSwapInt(this, BufferedSubscription.CTL, c, c & 0xFFFFFFFE)) {
                    stat = false;
                }
            }
            return stat;
        }
        
        private boolean checkDemand(final int c) {
            boolean stat = true;
            if (this.demand == 0L) {
                if ((c & 0x2) != 0x0) {
                    BufferedSubscription.U.compareAndSwapInt(this, BufferedSubscription.CTL, c, c & 0xFFFFFFFD);
                }
                else if (BufferedSubscription.U.compareAndSwapInt(this, BufferedSubscription.CTL, c, c & 0xFFFFFFFE)) {
                    stat = false;
                }
            }
            return stat;
        }
        
        private void handleOnNext(final Flow.Subscriber<? super T> s, final Throwable ex) {
            final BiConsumer<? super Flow.Subscriber<? super T>, ? super Throwable> h;
            if ((h = this.onNextHandler) != null) {
                try {
                    h.accept(s, ex);
                }
                catch (final Throwable t) {}
            }
            this.onError(ex);
        }
        
        static {
            U = UnsafeAccessor.getUnsafe();
            try {
                CTL = BufferedSubscription.U.objectFieldOffset(BufferedSubscription.class.getDeclaredField("ctl"));
                TAIL = BufferedSubscription.U.objectFieldOffset(BufferedSubscription.class.getDeclaredField("tail"));
                HEAD = BufferedSubscription.U.objectFieldOffset(BufferedSubscription.class.getDeclaredField("head"));
                DEMAND = BufferedSubscription.U.objectFieldOffset(BufferedSubscription.class.getDeclaredField("demand"));
                ABASE = BufferedSubscription.U.arrayBaseOffset(Object[].class);
                final int scale = BufferedSubscription.U.arrayIndexScale(Object[].class);
                if ((scale & scale - 1) != 0x0) {
                    throw new Error("data type scale not a power of two");
                }
                ASHIFT = 31 - Integer.numberOfLeadingZeros(scale);
            }
            catch (final ReflectiveOperationException e) {
                throw new Error(e);
            }
        }
    }
}
