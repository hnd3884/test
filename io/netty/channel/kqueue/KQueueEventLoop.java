package io.netty.channel.kqueue;

import io.netty.util.internal.logging.InternalLoggerFactory;
import io.netty.util.internal.PlatformDependent;
import java.io.IOException;
import java.util.Queue;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.collection.IntObjectHashMap;
import io.netty.channel.EventLoopTaskQueueFactory;
import io.netty.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.Executor;
import io.netty.channel.EventLoopGroup;
import io.netty.util.collection.IntObjectMap;
import io.netty.util.IntSupplier;
import io.netty.channel.unix.IovArray;
import io.netty.channel.SelectStrategy;
import io.netty.channel.unix.FileDescriptor;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.channel.SingleThreadEventLoop;

final class KQueueEventLoop extends SingleThreadEventLoop
{
    private static final InternalLogger logger;
    private static final AtomicIntegerFieldUpdater<KQueueEventLoop> WAKEN_UP_UPDATER;
    private static final int KQUEUE_WAKE_UP_IDENT = 0;
    private final boolean allowGrowing;
    private final FileDescriptor kqueueFd;
    private final KQueueEventArray changeList;
    private final KQueueEventArray eventList;
    private final SelectStrategy selectStrategy;
    private final IovArray iovArray;
    private final IntSupplier selectNowSupplier;
    private final IntObjectMap<AbstractKQueueChannel> channels;
    private volatile int wakenUp;
    private volatile int ioRatio;
    
    KQueueEventLoop(final EventLoopGroup parent, final Executor executor, int maxEvents, final SelectStrategy strategy, final RejectedExecutionHandler rejectedExecutionHandler, final EventLoopTaskQueueFactory taskQueueFactory, final EventLoopTaskQueueFactory tailTaskQueueFactory) {
        super(parent, executor, false, newTaskQueue(taskQueueFactory), newTaskQueue(tailTaskQueueFactory), rejectedExecutionHandler);
        this.iovArray = new IovArray();
        this.selectNowSupplier = new IntSupplier() {
            @Override
            public int get() throws Exception {
                return KQueueEventLoop.this.kqueueWaitNow();
            }
        };
        this.channels = new IntObjectHashMap<AbstractKQueueChannel>(4096);
        this.ioRatio = 50;
        this.selectStrategy = ObjectUtil.checkNotNull(strategy, "strategy");
        this.kqueueFd = Native.newKQueue();
        if (maxEvents == 0) {
            this.allowGrowing = true;
            maxEvents = 4096;
        }
        else {
            this.allowGrowing = false;
        }
        this.changeList = new KQueueEventArray(maxEvents);
        this.eventList = new KQueueEventArray(maxEvents);
        final int result = Native.keventAddUserEvent(this.kqueueFd.intValue(), 0);
        if (result < 0) {
            this.cleanup();
            throw new IllegalStateException("kevent failed to add user event with errno: " + -result);
        }
    }
    
    private static Queue<Runnable> newTaskQueue(final EventLoopTaskQueueFactory queueFactory) {
        if (queueFactory == null) {
            return newTaskQueue0(KQueueEventLoop.DEFAULT_MAX_PENDING_TASKS);
        }
        return queueFactory.newTaskQueue(KQueueEventLoop.DEFAULT_MAX_PENDING_TASKS);
    }
    
    void add(final AbstractKQueueChannel ch) {
        assert this.inEventLoop();
        final AbstractKQueueChannel old = this.channels.put(ch.fd().intValue(), ch);
        assert !old.isOpen();
    }
    
    void evSet(final AbstractKQueueChannel ch, final short filter, final short flags, final int fflags) {
        assert this.inEventLoop();
        this.changeList.evSet(ch, filter, flags, fflags);
    }
    
    void remove(final AbstractKQueueChannel ch) throws Exception {
        assert this.inEventLoop();
        final int fd = ch.fd().intValue();
        final AbstractKQueueChannel old = this.channels.remove(fd);
        if (old != null && old != ch) {
            this.channels.put(fd, old);
            assert !ch.isOpen();
        }
        else if (ch.isOpen()) {
            ch.unregisterFilters();
        }
    }
    
    IovArray cleanArray() {
        this.iovArray.clear();
        return this.iovArray;
    }
    
    @Override
    protected void wakeup(final boolean inEventLoop) {
        if (!inEventLoop && KQueueEventLoop.WAKEN_UP_UPDATER.compareAndSet(this, 0, 1)) {
            this.wakeup();
        }
    }
    
    private void wakeup() {
        Native.keventTriggerUserEvent(this.kqueueFd.intValue(), 0);
    }
    
    private int kqueueWait(final boolean oldWakeup) throws IOException {
        if (oldWakeup && this.hasTasks()) {
            return this.kqueueWaitNow();
        }
        final long totalDelay = this.delayNanos(System.nanoTime());
        final int delaySeconds = (int)Math.min(totalDelay / 1000000000L, 2147483647L);
        return this.kqueueWait(delaySeconds, (int)Math.min(totalDelay - delaySeconds * 1000000000L, 2147483647L));
    }
    
    private int kqueueWaitNow() throws IOException {
        return this.kqueueWait(0, 0);
    }
    
    private int kqueueWait(final int timeoutSec, final int timeoutNs) throws IOException {
        final int numEvents = Native.keventWait(this.kqueueFd.intValue(), this.changeList, this.eventList, timeoutSec, timeoutNs);
        this.changeList.clear();
        return numEvents;
    }
    
    private void processReady(final int ready) {
        for (int i = 0; i < ready; ++i) {
            final short filter = this.eventList.filter(i);
            final short flags = this.eventList.flags(i);
            final int fd = this.eventList.fd(i);
            if (filter == Native.EVFILT_USER || (flags & Native.EV_ERROR) != 0x0) {
                assert filter == Native.EVFILT_USER && fd == 0;
            }
            else {
                final AbstractKQueueChannel channel = this.channels.get(fd);
                if (channel == null) {
                    KQueueEventLoop.logger.warn("events[{}]=[{}, {}] had no channel!", i, this.eventList.fd(i), filter);
                }
                else {
                    final AbstractKQueueChannel.AbstractKQueueUnsafe unsafe = (AbstractKQueueChannel.AbstractKQueueUnsafe)channel.unsafe();
                    if (filter == Native.EVFILT_WRITE) {
                        unsafe.writeReady();
                    }
                    else if (filter == Native.EVFILT_READ) {
                        unsafe.readReady(this.eventList.data(i));
                    }
                    else if (filter == Native.EVFILT_SOCK && (this.eventList.fflags(i) & Native.NOTE_RDHUP) != 0x0) {
                        unsafe.readEOF();
                    }
                    if ((flags & Native.EV_EOF) != 0x0) {
                        unsafe.readEOF();
                    }
                }
            }
        }
    }
    
    @Override
    protected void run() {
        while (true) {
            while (true) {
                try {
                    int strategy = 0;
                    Label_0113: {
                    Label_0079:
                        while (true) {
                            strategy = this.selectStrategy.calculateStrategy(this.selectNowSupplier, this.hasTasks());
                            switch (strategy) {
                                case -2: {}
                                case -3:
                                case -1: {
                                    break Label_0079;
                                }
                                default: {
                                    break Label_0113;
                                }
                            }
                        }
                        strategy = this.kqueueWait(KQueueEventLoop.WAKEN_UP_UPDATER.getAndSet(this, 0) == 1);
                        if (this.wakenUp == 1) {
                            this.wakeup();
                        }
                    }
                    final int ioRatio = this.ioRatio;
                    if (ioRatio == 100) {
                        try {
                            if (strategy > 0) {
                                this.processReady(strategy);
                            }
                        }
                        finally {
                            this.runAllTasks();
                        }
                    }
                    else {
                        final long ioStartTime = System.nanoTime();
                        try {
                            if (strategy > 0) {
                                this.processReady(strategy);
                            }
                        }
                        finally {
                            final long ioTime = System.nanoTime() - ioStartTime;
                            this.runAllTasks(ioTime * (100 - ioRatio) / ioRatio);
                        }
                    }
                    if (this.allowGrowing && strategy == this.eventList.capacity()) {
                        this.eventList.realloc(false);
                    }
                }
                catch (final Error e) {
                    throw e;
                }
                catch (final Throwable t) {
                    handleLoopException(t);
                }
                finally {
                    try {
                        if (this.isShuttingDown()) {
                            this.closeAll();
                            if (this.confirmShutdown()) {
                                break;
                            }
                        }
                    }
                    catch (final Error e2) {
                        throw e2;
                    }
                    catch (final Throwable t2) {
                        handleLoopException(t2);
                    }
                }
                continue;
            }
        }
    }
    
    @Override
    protected Queue<Runnable> newTaskQueue(final int maxPendingTasks) {
        return newTaskQueue0(maxPendingTasks);
    }
    
    private static Queue<Runnable> newTaskQueue0(final int maxPendingTasks) {
        return (maxPendingTasks == Integer.MAX_VALUE) ? PlatformDependent.newMpscQueue() : PlatformDependent.newMpscQueue(maxPendingTasks);
    }
    
    public int getIoRatio() {
        return this.ioRatio;
    }
    
    public void setIoRatio(final int ioRatio) {
        if (ioRatio <= 0 || ioRatio > 100) {
            throw new IllegalArgumentException("ioRatio: " + ioRatio + " (expected: 0 < ioRatio <= 100)");
        }
        this.ioRatio = ioRatio;
    }
    
    @Override
    public int registeredChannels() {
        return this.channels.size();
    }
    
    @Override
    protected void cleanup() {
        try {
            this.kqueueFd.close();
        }
        catch (final IOException e) {
            KQueueEventLoop.logger.warn("Failed to close the kqueue fd.", e);
        }
        finally {
            this.changeList.free();
            this.eventList.free();
        }
    }
    
    private void closeAll() {
        try {
            this.kqueueWaitNow();
        }
        catch (final IOException ex) {}
        final AbstractKQueueChannel[] array;
        final AbstractKQueueChannel[] localChannels = array = this.channels.values().toArray(new AbstractKQueueChannel[0]);
        for (final AbstractKQueueChannel ch : array) {
            ch.unsafe().close(ch.unsafe().voidPromise());
        }
    }
    
    private static void handleLoopException(final Throwable t) {
        KQueueEventLoop.logger.warn("Unexpected exception in the selector loop.", t);
        try {
            Thread.sleep(1000L);
        }
        catch (final InterruptedException ex) {}
    }
    
    static {
        logger = InternalLoggerFactory.getInstance(KQueueEventLoop.class);
        WAKEN_UP_UPDATER = AtomicIntegerFieldUpdater.newUpdater(KQueueEventLoop.class, "wakenUp");
        KQueue.ensureAvailability();
    }
}
