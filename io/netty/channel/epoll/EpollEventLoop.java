package io.netty.channel.epoll;

import io.netty.util.internal.logging.InternalLoggerFactory;
import io.netty.util.concurrent.AbstractScheduledEventExecutor;
import io.netty.util.internal.PlatformDependent;
import java.util.Queue;
import java.io.IOException;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.collection.IntObjectHashMap;
import io.netty.channel.EventLoopTaskQueueFactory;
import io.netty.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.Executor;
import io.netty.channel.EventLoopGroup;
import java.util.concurrent.atomic.AtomicLong;
import io.netty.util.IntSupplier;
import io.netty.channel.SelectStrategy;
import io.netty.channel.unix.IovArray;
import io.netty.util.collection.IntObjectMap;
import io.netty.channel.unix.FileDescriptor;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.channel.SingleThreadEventLoop;

class EpollEventLoop extends SingleThreadEventLoop
{
    private static final InternalLogger logger;
    private final FileDescriptor epollFd;
    private final FileDescriptor eventFd;
    private final FileDescriptor timerFd;
    private final IntObjectMap<AbstractEpollChannel> channels;
    private final boolean allowGrowing;
    private final EpollEventArray events;
    private IovArray iovArray;
    private NativeDatagramPacketArray datagramPacketArray;
    private final SelectStrategy selectStrategy;
    private final IntSupplier selectNowSupplier;
    private static final long AWAKE = -1L;
    private static final long NONE = Long.MAX_VALUE;
    private final AtomicLong nextWakeupNanos;
    private boolean pendingWakeup;
    private volatile int ioRatio;
    private static final long MAX_SCHEDULED_TIMERFD_NS = 999999999L;
    
    EpollEventLoop(final EventLoopGroup parent, final Executor executor, final int maxEvents, final SelectStrategy strategy, final RejectedExecutionHandler rejectedExecutionHandler, final EventLoopTaskQueueFactory taskQueueFactory, final EventLoopTaskQueueFactory tailTaskQueueFactory) {
        super(parent, executor, false, newTaskQueue(taskQueueFactory), newTaskQueue(tailTaskQueueFactory), rejectedExecutionHandler);
        this.channels = new IntObjectHashMap<AbstractEpollChannel>(4096);
        this.selectNowSupplier = new IntSupplier() {
            @Override
            public int get() throws Exception {
                return EpollEventLoop.this.epollWaitNow();
            }
        };
        this.nextWakeupNanos = new AtomicLong(-1L);
        this.ioRatio = 50;
        this.selectStrategy = ObjectUtil.checkNotNull(strategy, "strategy");
        if (maxEvents == 0) {
            this.allowGrowing = true;
            this.events = new EpollEventArray(4096);
        }
        else {
            this.allowGrowing = false;
            this.events = new EpollEventArray(maxEvents);
        }
        boolean success = false;
        FileDescriptor epollFd = null;
        FileDescriptor eventFd = null;
        FileDescriptor timerFd = null;
        try {
            epollFd = (this.epollFd = Native.newEpollCreate());
            eventFd = (this.eventFd = Native.newEventFd());
            try {
                Native.epollCtlAdd(epollFd.intValue(), eventFd.intValue(), Native.EPOLLIN | Native.EPOLLET);
            }
            catch (final IOException e) {
                throw new IllegalStateException("Unable to add eventFd filedescriptor to epoll", e);
            }
            timerFd = (this.timerFd = Native.newTimerFd());
            try {
                Native.epollCtlAdd(epollFd.intValue(), timerFd.intValue(), Native.EPOLLIN | Native.EPOLLET);
            }
            catch (final IOException e) {
                throw new IllegalStateException("Unable to add timerFd filedescriptor to epoll", e);
            }
            success = true;
        }
        finally {
            if (!success) {
                if (epollFd != null) {
                    try {
                        epollFd.close();
                    }
                    catch (final Exception ex) {}
                }
                if (eventFd != null) {
                    try {
                        eventFd.close();
                    }
                    catch (final Exception ex2) {}
                }
                if (timerFd != null) {
                    try {
                        timerFd.close();
                    }
                    catch (final Exception ex3) {}
                }
            }
        }
    }
    
    private static Queue<Runnable> newTaskQueue(final EventLoopTaskQueueFactory queueFactory) {
        if (queueFactory == null) {
            return newTaskQueue0(EpollEventLoop.DEFAULT_MAX_PENDING_TASKS);
        }
        return queueFactory.newTaskQueue(EpollEventLoop.DEFAULT_MAX_PENDING_TASKS);
    }
    
    IovArray cleanIovArray() {
        if (this.iovArray == null) {
            this.iovArray = new IovArray();
        }
        else {
            this.iovArray.clear();
        }
        return this.iovArray;
    }
    
    NativeDatagramPacketArray cleanDatagramPacketArray() {
        if (this.datagramPacketArray == null) {
            this.datagramPacketArray = new NativeDatagramPacketArray();
        }
        else {
            this.datagramPacketArray.clear();
        }
        return this.datagramPacketArray;
    }
    
    @Override
    protected void wakeup(final boolean inEventLoop) {
        if (!inEventLoop && this.nextWakeupNanos.getAndSet(-1L) != -1L) {
            Native.eventFdWrite(this.eventFd.intValue(), 1L);
        }
    }
    
    @Override
    protected boolean beforeScheduledTaskSubmitted(final long deadlineNanos) {
        return deadlineNanos < this.nextWakeupNanos.get();
    }
    
    @Override
    protected boolean afterScheduledTaskSubmitted(final long deadlineNanos) {
        return deadlineNanos < this.nextWakeupNanos.get();
    }
    
    void add(final AbstractEpollChannel ch) throws IOException {
        assert this.inEventLoop();
        final int fd = ch.socket.intValue();
        Native.epollCtlAdd(this.epollFd.intValue(), fd, ch.flags);
        final AbstractEpollChannel old = this.channels.put(fd, ch);
        assert !old.isOpen();
    }
    
    void modify(final AbstractEpollChannel ch) throws IOException {
        assert this.inEventLoop();
        Native.epollCtlMod(this.epollFd.intValue(), ch.socket.intValue(), ch.flags);
    }
    
    void remove(final AbstractEpollChannel ch) throws IOException {
        assert this.inEventLoop();
        final int fd = ch.socket.intValue();
        final AbstractEpollChannel old = this.channels.remove(fd);
        if (old != null && old != ch) {
            this.channels.put(fd, old);
            assert !ch.isOpen();
        }
        else if (ch.isOpen()) {
            Native.epollCtlDel(this.epollFd.intValue(), fd);
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
    
    private int epollWait(final long deadlineNanos) throws IOException {
        if (deadlineNanos == Long.MAX_VALUE) {
            return Native.epollWait(this.epollFd, this.events, this.timerFd, Integer.MAX_VALUE, 0);
        }
        final long totalDelay = AbstractScheduledEventExecutor.deadlineToDelayNanos(deadlineNanos);
        final int delaySeconds = (int)Math.min(totalDelay / 1000000000L, 2147483647L);
        final int delayNanos = (int)Math.min(totalDelay - delaySeconds * 1000000000L, 999999999L);
        return Native.epollWait(this.epollFd, this.events, this.timerFd, delaySeconds, delayNanos);
    }
    
    private int epollWaitNoTimerChange() throws IOException {
        return Native.epollWait(this.epollFd, this.events, false);
    }
    
    private int epollWaitNow() throws IOException {
        return Native.epollWait(this.epollFd, this.events, true);
    }
    
    private int epollBusyWait() throws IOException {
        return Native.epollBusyWait(this.epollFd, this.events);
    }
    
    private int epollWaitTimeboxed() throws IOException {
        return Native.epollWait(this.epollFd, this.events, 1000);
    }
    
    @Override
    protected void run() {
        while (true) {
            long prevDeadlineNanos = Long.MAX_VALUE;
            while (true) {
                try {
                    int strategy = 0;
                    Label_0281: {
                        Label_0096: {
                        Label_0088:
                            while (true) {
                                strategy = this.selectStrategy.calculateStrategy(this.selectNowSupplier, this.hasTasks());
                                switch (strategy) {
                                    case -2: {}
                                    case -3: {
                                        break Label_0088;
                                    }
                                    case -1: {
                                        break Label_0096;
                                    }
                                    default: {
                                        break Label_0281;
                                    }
                                }
                            }
                            strategy = this.epollBusyWait();
                            break Label_0281;
                        }
                        if (this.pendingWakeup) {
                            strategy = this.epollWaitTimeboxed();
                            if (strategy != 0) {
                                break Label_0281;
                            }
                            EpollEventLoop.logger.warn("Missed eventfd write (not seen after > 1 second)");
                            this.pendingWakeup = false;
                            if (this.hasTasks()) {
                                break Label_0281;
                            }
                        }
                        long curDeadlineNanos = this.nextScheduledTaskDeadlineNanos();
                        if (curDeadlineNanos == -1L) {
                            curDeadlineNanos = Long.MAX_VALUE;
                        }
                        this.nextWakeupNanos.set(curDeadlineNanos);
                        try {
                            if (!this.hasTasks()) {
                                if (curDeadlineNanos == prevDeadlineNanos) {
                                    strategy = this.epollWaitNoTimerChange();
                                }
                                else {
                                    prevDeadlineNanos = curDeadlineNanos;
                                    strategy = this.epollWait(curDeadlineNanos);
                                }
                            }
                        }
                        finally {
                            if (this.nextWakeupNanos.get() == -1L || this.nextWakeupNanos.getAndSet(-1L) == -1L) {
                                this.pendingWakeup = true;
                            }
                        }
                    }
                    final int ioRatio = this.ioRatio;
                    if (ioRatio == 100) {
                        try {
                            if (strategy > 0 && this.processReady(this.events, strategy)) {
                                prevDeadlineNanos = Long.MAX_VALUE;
                            }
                        }
                        finally {
                            this.runAllTasks();
                        }
                    }
                    else if (strategy > 0) {
                        final long ioStartTime = System.nanoTime();
                        try {
                            if (this.processReady(this.events, strategy)) {
                                prevDeadlineNanos = Long.MAX_VALUE;
                            }
                        }
                        finally {
                            final long ioTime = System.nanoTime() - ioStartTime;
                            this.runAllTasks(ioTime * (100 - ioRatio) / ioRatio);
                        }
                    }
                    else {
                        this.runAllTasks(0L);
                    }
                    if (this.allowGrowing && strategy == this.events.length()) {
                        this.events.increase();
                    }
                }
                catch (final Error e) {
                    throw e;
                }
                catch (final Throwable t) {
                    this.handleLoopException(t);
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
                        this.handleLoopException(t2);
                    }
                }
                continue;
            }
        }
    }
    
    void handleLoopException(final Throwable t) {
        EpollEventLoop.logger.warn("Unexpected exception in the selector loop.", t);
        try {
            Thread.sleep(1000L);
        }
        catch (final InterruptedException ex) {}
    }
    
    private void closeAll() {
        final AbstractEpollChannel[] array;
        final AbstractEpollChannel[] localChannels = array = this.channels.values().toArray(new AbstractEpollChannel[0]);
        for (final AbstractEpollChannel ch : array) {
            ch.unsafe().close(ch.unsafe().voidPromise());
        }
    }
    
    private boolean processReady(final EpollEventArray events, final int ready) {
        boolean timerFired = false;
        for (int i = 0; i < ready; ++i) {
            final int fd = events.fd(i);
            if (fd == this.eventFd.intValue()) {
                this.pendingWakeup = false;
            }
            else if (fd == this.timerFd.intValue()) {
                timerFired = true;
            }
            else {
                final long ev = events.events(i);
                final AbstractEpollChannel ch = this.channels.get(fd);
                if (ch != null) {
                    final AbstractEpollChannel.AbstractEpollUnsafe unsafe = (AbstractEpollChannel.AbstractEpollUnsafe)ch.unsafe();
                    if ((ev & (long)(Native.EPOLLERR | Native.EPOLLOUT)) != 0x0L) {
                        unsafe.epollOutReady();
                    }
                    if ((ev & (long)(Native.EPOLLERR | Native.EPOLLIN)) != 0x0L) {
                        unsafe.epollInReady();
                    }
                    if ((ev & (long)Native.EPOLLRDHUP) != 0x0L) {
                        unsafe.epollRdHupReady();
                    }
                }
                else {
                    try {
                        Native.epollCtlDel(this.epollFd.intValue(), fd);
                    }
                    catch (final IOException ex) {}
                }
            }
        }
        return timerFired;
    }
    
    @Override
    protected void cleanup() {
        try {
            while (this.pendingWakeup) {
                try {
                    final int count = this.epollWaitTimeboxed();
                    if (count == 0) {
                        break;
                    }
                    for (int i = 0; i < count; ++i) {
                        if (this.events.fd(i) == this.eventFd.intValue()) {
                            this.pendingWakeup = false;
                            break;
                        }
                    }
                }
                catch (final IOException ex) {}
            }
            try {
                this.eventFd.close();
            }
            catch (final IOException e) {
                EpollEventLoop.logger.warn("Failed to close the event fd.", e);
            }
            try {
                this.timerFd.close();
            }
            catch (final IOException e) {
                EpollEventLoop.logger.warn("Failed to close the timer fd.", e);
            }
            try {
                this.epollFd.close();
            }
            catch (final IOException e) {
                EpollEventLoop.logger.warn("Failed to close the epoll fd.", e);
            }
        }
        finally {
            if (this.iovArray != null) {
                this.iovArray.release();
                this.iovArray = null;
            }
            if (this.datagramPacketArray != null) {
                this.datagramPacketArray.release();
                this.datagramPacketArray = null;
            }
            this.events.free();
        }
    }
    
    static {
        logger = InternalLoggerFactory.getInstance(EpollEventLoop.class);
        Epoll.ensureAvailability();
    }
}
