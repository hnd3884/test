package io.netty.channel.nio;

import io.netty.util.internal.SystemPropertyUtil;
import io.netty.util.internal.logging.InternalLoggerFactory;
import io.netty.util.concurrent.AbstractScheduledEventExecutor;
import java.util.Collection;
import java.util.ArrayList;
import io.netty.channel.EventLoop;
import java.util.Set;
import java.nio.channels.CancelledKeyException;
import java.util.Iterator;
import java.nio.channels.SelectionKey;
import io.netty.channel.EventLoopException;
import java.nio.channels.SelectableChannel;
import java.lang.reflect.Field;
import java.lang.reflect.AccessibleObject;
import io.netty.util.internal.ReflectionUtil;
import java.security.AccessController;
import io.netty.util.internal.PlatformDependent;
import java.security.PrivilegedAction;
import java.io.IOException;
import io.netty.channel.ChannelException;
import java.util.Queue;
import io.netty.util.internal.ObjectUtil;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.EventLoopTaskQueueFactory;
import io.netty.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.Executor;
import io.netty.channel.SelectStrategy;
import java.util.concurrent.atomic.AtomicLong;
import java.nio.channels.spi.SelectorProvider;
import java.nio.channels.Selector;
import io.netty.util.IntSupplier;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.channel.SingleThreadEventLoop;

public final class NioEventLoop extends SingleThreadEventLoop
{
    private static final InternalLogger logger;
    private static final int CLEANUP_INTERVAL = 256;
    private static final boolean DISABLE_KEY_SET_OPTIMIZATION;
    private static final int MIN_PREMATURE_SELECTOR_RETURNS = 3;
    private static final int SELECTOR_AUTO_REBUILD_THRESHOLD;
    private final IntSupplier selectNowSupplier;
    private Selector selector;
    private Selector unwrappedSelector;
    private SelectedSelectionKeySet selectedKeys;
    private final SelectorProvider provider;
    private static final long AWAKE = -1L;
    private static final long NONE = Long.MAX_VALUE;
    private final AtomicLong nextWakeupNanos;
    private final SelectStrategy selectStrategy;
    private volatile int ioRatio;
    private int cancelledKeys;
    private boolean needsToSelectAgain;
    
    NioEventLoop(final NioEventLoopGroup parent, final Executor executor, final SelectorProvider selectorProvider, final SelectStrategy strategy, final RejectedExecutionHandler rejectedExecutionHandler, final EventLoopTaskQueueFactory taskQueueFactory, final EventLoopTaskQueueFactory tailTaskQueueFactory) {
        super(parent, executor, false, newTaskQueue(taskQueueFactory), newTaskQueue(tailTaskQueueFactory), rejectedExecutionHandler);
        this.selectNowSupplier = new IntSupplier() {
            @Override
            public int get() throws Exception {
                return NioEventLoop.this.selectNow();
            }
        };
        this.nextWakeupNanos = new AtomicLong(-1L);
        this.ioRatio = 50;
        this.provider = ObjectUtil.checkNotNull(selectorProvider, "selectorProvider");
        this.selectStrategy = ObjectUtil.checkNotNull(strategy, "selectStrategy");
        final SelectorTuple selectorTuple = this.openSelector();
        this.selector = selectorTuple.selector;
        this.unwrappedSelector = selectorTuple.unwrappedSelector;
    }
    
    private static Queue<Runnable> newTaskQueue(final EventLoopTaskQueueFactory queueFactory) {
        if (queueFactory == null) {
            return newTaskQueue0(NioEventLoop.DEFAULT_MAX_PENDING_TASKS);
        }
        return queueFactory.newTaskQueue(NioEventLoop.DEFAULT_MAX_PENDING_TASKS);
    }
    
    private SelectorTuple openSelector() {
        Selector unwrappedSelector;
        try {
            unwrappedSelector = this.provider.openSelector();
        }
        catch (final IOException e) {
            throw new ChannelException("failed to open a new selector", e);
        }
        if (NioEventLoop.DISABLE_KEY_SET_OPTIMIZATION) {
            return new SelectorTuple(unwrappedSelector);
        }
        final Object maybeSelectorImplClass = AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Object>() {
            @Override
            public Object run() {
                try {
                    return Class.forName("sun.nio.ch.SelectorImpl", false, PlatformDependent.getSystemClassLoader());
                }
                catch (final Throwable cause) {
                    return cause;
                }
            }
        });
        if (!(maybeSelectorImplClass instanceof Class) || !((Class)maybeSelectorImplClass).isAssignableFrom(unwrappedSelector.getClass())) {
            if (maybeSelectorImplClass instanceof Throwable) {
                final Throwable t = (Throwable)maybeSelectorImplClass;
                NioEventLoop.logger.trace("failed to instrument a special java.util.Set into: {}", unwrappedSelector, t);
            }
            return new SelectorTuple(unwrappedSelector);
        }
        final Class<?> selectorImplClass = (Class<?>)maybeSelectorImplClass;
        final SelectedSelectionKeySet selectedKeySet = new SelectedSelectionKeySet();
        final Object maybeException = AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Object>() {
            @Override
            public Object run() {
                try {
                    final Field selectedKeysField = selectorImplClass.getDeclaredField("selectedKeys");
                    final Field publicSelectedKeysField = selectorImplClass.getDeclaredField("publicSelectedKeys");
                    if (PlatformDependent.javaVersion() >= 9 && PlatformDependent.hasUnsafe()) {
                        final long selectedKeysFieldOffset = PlatformDependent.objectFieldOffset(selectedKeysField);
                        final long publicSelectedKeysFieldOffset = PlatformDependent.objectFieldOffset(publicSelectedKeysField);
                        if (selectedKeysFieldOffset != -1L && publicSelectedKeysFieldOffset != -1L) {
                            PlatformDependent.putObject(unwrappedSelector, selectedKeysFieldOffset, selectedKeySet);
                            PlatformDependent.putObject(unwrappedSelector, publicSelectedKeysFieldOffset, selectedKeySet);
                            return null;
                        }
                    }
                    Throwable cause = ReflectionUtil.trySetAccessible(selectedKeysField, true);
                    if (cause != null) {
                        return cause;
                    }
                    cause = ReflectionUtil.trySetAccessible(publicSelectedKeysField, true);
                    if (cause != null) {
                        return cause;
                    }
                    selectedKeysField.set(unwrappedSelector, selectedKeySet);
                    publicSelectedKeysField.set(unwrappedSelector, selectedKeySet);
                    return null;
                }
                catch (final NoSuchFieldException e) {
                    return e;
                }
                catch (final IllegalAccessException e2) {
                    return e2;
                }
            }
        });
        if (maybeException instanceof Exception) {
            this.selectedKeys = null;
            final Exception e2 = (Exception)maybeException;
            NioEventLoop.logger.trace("failed to instrument a special java.util.Set into: {}", unwrappedSelector, e2);
            return new SelectorTuple(unwrappedSelector);
        }
        this.selectedKeys = selectedKeySet;
        NioEventLoop.logger.trace("instrumented a special java.util.Set into: {}", unwrappedSelector);
        return new SelectorTuple(unwrappedSelector, new SelectedSelectionKeySetSelector(unwrappedSelector, selectedKeySet));
    }
    
    public SelectorProvider selectorProvider() {
        return this.provider;
    }
    
    @Override
    protected Queue<Runnable> newTaskQueue(final int maxPendingTasks) {
        return newTaskQueue0(maxPendingTasks);
    }
    
    private static Queue<Runnable> newTaskQueue0(final int maxPendingTasks) {
        return (maxPendingTasks == Integer.MAX_VALUE) ? PlatformDependent.newMpscQueue() : PlatformDependent.newMpscQueue(maxPendingTasks);
    }
    
    public void register(final SelectableChannel ch, final int interestOps, final NioTask<?> task) {
        ObjectUtil.checkNotNull(ch, "ch");
        if (interestOps == 0) {
            throw new IllegalArgumentException("interestOps must be non-zero.");
        }
        if ((interestOps & ~ch.validOps()) != 0x0) {
            throw new IllegalArgumentException("invalid interestOps: " + interestOps + "(validOps: " + ch.validOps() + ')');
        }
        ObjectUtil.checkNotNull(task, "task");
        if (this.isShutdown()) {
            throw new IllegalStateException("event loop shut down");
        }
        if (this.inEventLoop()) {
            this.register0(ch, interestOps, task);
        }
        else {
            try {
                this.submit(new Runnable() {
                    @Override
                    public void run() {
                        NioEventLoop.this.register0(ch, interestOps, task);
                    }
                }).sync();
            }
            catch (final InterruptedException ignore) {
                Thread.currentThread().interrupt();
            }
        }
    }
    
    private void register0(final SelectableChannel ch, final int interestOps, final NioTask<?> task) {
        try {
            ch.register(this.unwrappedSelector, interestOps, task);
        }
        catch (final Exception e) {
            throw new EventLoopException("failed to register a channel", e);
        }
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
    
    public void rebuildSelector() {
        if (!this.inEventLoop()) {
            this.execute(new Runnable() {
                @Override
                public void run() {
                    NioEventLoop.this.rebuildSelector0();
                }
            });
            return;
        }
        this.rebuildSelector0();
    }
    
    @Override
    public int registeredChannels() {
        return this.selector.keys().size() - this.cancelledKeys;
    }
    
    private void rebuildSelector0() {
        final Selector oldSelector = this.selector;
        if (oldSelector == null) {
            return;
        }
        SelectorTuple newSelectorTuple;
        try {
            newSelectorTuple = this.openSelector();
        }
        catch (final Exception e) {
            NioEventLoop.logger.warn("Failed to create a new Selector.", e);
            return;
        }
        int nChannels = 0;
        for (final SelectionKey key : oldSelector.keys()) {
            final Object a = key.attachment();
            try {
                if (!key.isValid() || key.channel().keyFor(newSelectorTuple.unwrappedSelector) != null) {
                    continue;
                }
                final int interestOps = key.interestOps();
                key.cancel();
                final SelectionKey newKey = key.channel().register(newSelectorTuple.unwrappedSelector, interestOps, a);
                if (a instanceof AbstractNioChannel) {
                    ((AbstractNioChannel)a).selectionKey = newKey;
                }
                ++nChannels;
            }
            catch (final Exception e2) {
                NioEventLoop.logger.warn("Failed to re-register a Channel to the new Selector.", e2);
                if (a instanceof AbstractNioChannel) {
                    final AbstractNioChannel ch = (AbstractNioChannel)a;
                    ch.unsafe().close(ch.unsafe().voidPromise());
                }
                else {
                    final NioTask<SelectableChannel> task = (NioTask<SelectableChannel>)a;
                    invokeChannelUnregistered(task, key, e2);
                }
            }
        }
        this.selector = newSelectorTuple.selector;
        this.unwrappedSelector = newSelectorTuple.unwrappedSelector;
        try {
            oldSelector.close();
        }
        catch (final Throwable t) {
            if (NioEventLoop.logger.isWarnEnabled()) {
                NioEventLoop.logger.warn("Failed to close the old Selector.", t);
            }
        }
        if (NioEventLoop.logger.isInfoEnabled()) {
            NioEventLoop.logger.info("Migrated " + nChannels + " channel(s) to the new Selector.");
        }
    }
    
    @Override
    protected void run() {
        while (true) {
            int selectCnt = 0;
            while (true) {
                try {
                    int strategy = 0;
                    try {
                        Label_0147: {
                        Label_0081:
                            while (true) {
                                strategy = this.selectStrategy.calculateStrategy(this.selectNowSupplier, this.hasTasks());
                                switch (strategy) {
                                    case -2: {}
                                    case -3:
                                    case -1: {
                                        break Label_0081;
                                    }
                                    default: {
                                        break Label_0147;
                                    }
                                }
                            }
                            long curDeadlineNanos = this.nextScheduledTaskDeadlineNanos();
                            if (curDeadlineNanos == -1L) {
                                curDeadlineNanos = Long.MAX_VALUE;
                            }
                            this.nextWakeupNanos.set(curDeadlineNanos);
                            try {
                                if (!this.hasTasks()) {
                                    strategy = this.select(curDeadlineNanos);
                                }
                            }
                            finally {
                                this.nextWakeupNanos.lazySet(-1L);
                            }
                        }
                    }
                    catch (final IOException e) {
                        this.rebuildSelector0();
                        selectCnt = 0;
                        handleLoopException(e);
                    }
                    ++selectCnt;
                    this.cancelledKeys = 0;
                    this.needsToSelectAgain = false;
                    final int ioRatio = this.ioRatio;
                    boolean ranTasks;
                    if (ioRatio == 100) {
                        try {
                            if (strategy > 0) {
                                this.processSelectedKeys();
                            }
                        }
                        finally {
                            ranTasks = this.runAllTasks();
                        }
                    }
                    else if (strategy > 0) {
                        final long ioStartTime = System.nanoTime();
                        try {
                            this.processSelectedKeys();
                        }
                        finally {
                            final long ioTime = System.nanoTime() - ioStartTime;
                            ranTasks = this.runAllTasks(ioTime * (100 - ioRatio) / ioRatio);
                        }
                    }
                    else {
                        ranTasks = this.runAllTasks(0L);
                    }
                    if (ranTasks || strategy > 0) {
                        if (selectCnt > 3 && NioEventLoop.logger.isDebugEnabled()) {
                            NioEventLoop.logger.debug("Selector.select() returned prematurely {} times in a row for Selector {}.", (Object)(selectCnt - 1), this.selector);
                        }
                        selectCnt = 0;
                    }
                    else if (this.unexpectedSelectorWakeup(selectCnt)) {
                        selectCnt = 0;
                    }
                }
                catch (final CancelledKeyException e2) {
                    if (NioEventLoop.logger.isDebugEnabled()) {
                        NioEventLoop.logger.debug(CancelledKeyException.class.getSimpleName() + " raised by a Selector {} - JDK bug?", this.selector, e2);
                    }
                }
                catch (final Error e3) {
                    throw e3;
                }
                catch (final Throwable t) {
                    handleLoopException(t);
                }
                finally {
                    try {
                        if (this.isShuttingDown()) {
                            this.closeAll();
                            if (this.confirmShutdown()) {
                                return;
                            }
                        }
                    }
                    catch (final Error e4) {
                        throw e4;
                    }
                    catch (final Throwable t2) {
                        handleLoopException(t2);
                    }
                }
                continue;
            }
        }
    }
    
    private boolean unexpectedSelectorWakeup(final int selectCnt) {
        if (Thread.interrupted()) {
            if (NioEventLoop.logger.isDebugEnabled()) {
                NioEventLoop.logger.debug("Selector.select() returned prematurely because Thread.currentThread().interrupt() was called. Use NioEventLoop.shutdownGracefully() to shutdown the NioEventLoop.");
            }
            return true;
        }
        if (NioEventLoop.SELECTOR_AUTO_REBUILD_THRESHOLD > 0 && selectCnt >= NioEventLoop.SELECTOR_AUTO_REBUILD_THRESHOLD) {
            NioEventLoop.logger.warn("Selector.select() returned prematurely {} times in a row; rebuilding Selector {}.", (Object)selectCnt, this.selector);
            this.rebuildSelector();
            return true;
        }
        return false;
    }
    
    private static void handleLoopException(final Throwable t) {
        NioEventLoop.logger.warn("Unexpected exception in the selector loop.", t);
        try {
            Thread.sleep(1000L);
        }
        catch (final InterruptedException ex) {}
    }
    
    private void processSelectedKeys() {
        if (this.selectedKeys != null) {
            this.processSelectedKeysOptimized();
        }
        else {
            this.processSelectedKeysPlain(this.selector.selectedKeys());
        }
    }
    
    @Override
    protected void cleanup() {
        try {
            this.selector.close();
        }
        catch (final IOException e) {
            NioEventLoop.logger.warn("Failed to close a selector.", e);
        }
    }
    
    void cancel(final SelectionKey key) {
        key.cancel();
        ++this.cancelledKeys;
        if (this.cancelledKeys >= 256) {
            this.cancelledKeys = 0;
            this.needsToSelectAgain = true;
        }
    }
    
    private void processSelectedKeysPlain(Set<SelectionKey> selectedKeys) {
        if (selectedKeys.isEmpty()) {
            return;
        }
        Iterator<SelectionKey> i = selectedKeys.iterator();
        while (true) {
            final SelectionKey k = i.next();
            final Object a = k.attachment();
            i.remove();
            if (a instanceof AbstractNioChannel) {
                this.processSelectedKey(k, (AbstractNioChannel)a);
            }
            else {
                final NioTask<SelectableChannel> task = (NioTask<SelectableChannel>)a;
                processSelectedKey(k, task);
            }
            if (!i.hasNext()) {
                break;
            }
            if (!this.needsToSelectAgain) {
                continue;
            }
            this.selectAgain();
            selectedKeys = this.selector.selectedKeys();
            if (selectedKeys.isEmpty()) {
                break;
            }
            i = selectedKeys.iterator();
        }
    }
    
    private void processSelectedKeysOptimized() {
        for (int i = 0; i < this.selectedKeys.size; ++i) {
            final SelectionKey k = this.selectedKeys.keys[i];
            this.selectedKeys.keys[i] = null;
            final Object a = k.attachment();
            if (a instanceof AbstractNioChannel) {
                this.processSelectedKey(k, (AbstractNioChannel)a);
            }
            else {
                final NioTask<SelectableChannel> task = (NioTask<SelectableChannel>)a;
                processSelectedKey(k, task);
            }
            if (this.needsToSelectAgain) {
                this.selectedKeys.reset(i + 1);
                this.selectAgain();
                i = -1;
            }
        }
    }
    
    private void processSelectedKey(final SelectionKey k, final AbstractNioChannel ch) {
        final AbstractNioChannel.NioUnsafe unsafe = ch.unsafe();
        if (!k.isValid()) {
            EventLoop eventLoop;
            try {
                eventLoop = ch.eventLoop();
            }
            catch (final Throwable ignored) {
                return;
            }
            if (eventLoop == this) {
                unsafe.close(unsafe.voidPromise());
            }
            return;
        }
        try {
            final int readyOps = k.readyOps();
            if ((readyOps & 0x8) != 0x0) {
                int ops = k.interestOps();
                ops &= 0xFFFFFFF7;
                k.interestOps(ops);
                unsafe.finishConnect();
            }
            if ((readyOps & 0x4) != 0x0) {
                ch.unsafe().forceFlush();
            }
            if ((readyOps & 0x11) != 0x0 || readyOps == 0) {
                unsafe.read();
            }
        }
        catch (final CancelledKeyException ignored2) {
            unsafe.close(unsafe.voidPromise());
        }
    }
    
    private static void processSelectedKey(final SelectionKey k, final NioTask<SelectableChannel> task) {
        int state = 0;
        try {
            task.channelReady(k.channel(), k);
            state = 1;
        }
        catch (final Exception e) {
            k.cancel();
            invokeChannelUnregistered(task, k, e);
            state = 2;
        }
        finally {
            switch (state) {
                case 0: {
                    k.cancel();
                    invokeChannelUnregistered(task, k, null);
                    break;
                }
                case 1: {
                    if (!k.isValid()) {
                        invokeChannelUnregistered(task, k, null);
                        break;
                    }
                    break;
                }
            }
        }
    }
    
    private void closeAll() {
        this.selectAgain();
        final Set<SelectionKey> keys = this.selector.keys();
        final Collection<AbstractNioChannel> channels = new ArrayList<AbstractNioChannel>(keys.size());
        for (final SelectionKey k : keys) {
            final Object a = k.attachment();
            if (a instanceof AbstractNioChannel) {
                channels.add((AbstractNioChannel)a);
            }
            else {
                k.cancel();
                final NioTask<SelectableChannel> task = (NioTask<SelectableChannel>)a;
                invokeChannelUnregistered(task, k, null);
            }
        }
        for (final AbstractNioChannel ch : channels) {
            ch.unsafe().close(ch.unsafe().voidPromise());
        }
    }
    
    private static void invokeChannelUnregistered(final NioTask<SelectableChannel> task, final SelectionKey k, final Throwable cause) {
        try {
            task.channelUnregistered(k.channel(), cause);
        }
        catch (final Exception e) {
            NioEventLoop.logger.warn("Unexpected exception while running NioTask.channelUnregistered()", e);
        }
    }
    
    @Override
    protected void wakeup(final boolean inEventLoop) {
        if (!inEventLoop && this.nextWakeupNanos.getAndSet(-1L) != -1L) {
            this.selector.wakeup();
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
    
    Selector unwrappedSelector() {
        return this.unwrappedSelector;
    }
    
    int selectNow() throws IOException {
        return this.selector.selectNow();
    }
    
    private int select(final long deadlineNanos) throws IOException {
        if (deadlineNanos == Long.MAX_VALUE) {
            return this.selector.select();
        }
        final long timeoutMillis = AbstractScheduledEventExecutor.deadlineToDelayNanos(deadlineNanos + 995000L) / 1000000L;
        return (timeoutMillis <= 0L) ? this.selector.selectNow() : this.selector.select(timeoutMillis);
    }
    
    private void selectAgain() {
        this.needsToSelectAgain = false;
        try {
            this.selector.selectNow();
        }
        catch (final Throwable t) {
            NioEventLoop.logger.warn("Failed to update SelectionKeys.", t);
        }
    }
    
    static {
        logger = InternalLoggerFactory.getInstance(NioEventLoop.class);
        DISABLE_KEY_SET_OPTIMIZATION = SystemPropertyUtil.getBoolean("io.netty.noKeySetOptimization", false);
        final String key = "sun.nio.ch.bugLevel";
        final String bugLevel = SystemPropertyUtil.get("sun.nio.ch.bugLevel");
        if (bugLevel == null) {
            try {
                AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Void>() {
                    @Override
                    public Void run() {
                        System.setProperty("sun.nio.ch.bugLevel", "");
                        return null;
                    }
                });
            }
            catch (final SecurityException e) {
                NioEventLoop.logger.debug("Unable to get/set System Property: sun.nio.ch.bugLevel", e);
            }
        }
        int selectorAutoRebuildThreshold = SystemPropertyUtil.getInt("io.netty.selectorAutoRebuildThreshold", 512);
        if (selectorAutoRebuildThreshold < 3) {
            selectorAutoRebuildThreshold = 0;
        }
        SELECTOR_AUTO_REBUILD_THRESHOLD = selectorAutoRebuildThreshold;
        if (NioEventLoop.logger.isDebugEnabled()) {
            NioEventLoop.logger.debug("-Dio.netty.noKeySetOptimization: {}", (Object)NioEventLoop.DISABLE_KEY_SET_OPTIMIZATION);
            NioEventLoop.logger.debug("-Dio.netty.selectorAutoRebuildThreshold: {}", (Object)NioEventLoop.SELECTOR_AUTO_REBUILD_THRESHOLD);
        }
    }
    
    private static final class SelectorTuple
    {
        final Selector unwrappedSelector;
        final Selector selector;
        
        SelectorTuple(final Selector unwrappedSelector) {
            this.unwrappedSelector = unwrappedSelector;
            this.selector = unwrappedSelector;
        }
        
        SelectorTuple(final Selector unwrappedSelector, final Selector selector) {
            this.unwrappedSelector = unwrappedSelector;
            this.selector = selector;
        }
    }
}
