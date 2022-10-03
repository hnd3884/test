package io.netty.channel;

import io.netty.util.internal.SystemPropertyUtil;
import io.netty.util.internal.ObjectPool;
import io.netty.util.internal.logging.InternalLoggerFactory;
import io.netty.util.concurrent.AbstractEventExecutor;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import io.netty.util.internal.StringUtil;
import io.netty.util.concurrent.Promise;
import io.netty.util.internal.PromiseNotificationUtil;
import io.netty.util.ReferenceCountUtil;
import java.net.SocketAddress;
import io.netty.util.internal.ThrowableUtil;
import io.netty.buffer.ByteBufAllocator;
import io.netty.util.concurrent.OrderedEventExecutor;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.concurrent.EventExecutor;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.ResourceLeakHint;

abstract class AbstractChannelHandlerContext implements ChannelHandlerContext, ResourceLeakHint
{
    private static final InternalLogger logger;
    volatile AbstractChannelHandlerContext next;
    volatile AbstractChannelHandlerContext prev;
    private static final AtomicIntegerFieldUpdater<AbstractChannelHandlerContext> HANDLER_STATE_UPDATER;
    private static final int ADD_PENDING = 1;
    private static final int ADD_COMPLETE = 2;
    private static final int REMOVE_COMPLETE = 3;
    private static final int INIT = 0;
    private final DefaultChannelPipeline pipeline;
    private final String name;
    private final boolean ordered;
    private final int executionMask;
    final EventExecutor executor;
    private ChannelFuture succeededFuture;
    private Tasks invokeTasks;
    private volatile int handlerState;
    
    AbstractChannelHandlerContext(final DefaultChannelPipeline pipeline, final EventExecutor executor, final String name, final Class<? extends ChannelHandler> handlerClass) {
        this.handlerState = 0;
        this.name = ObjectUtil.checkNotNull(name, "name");
        this.pipeline = pipeline;
        this.executor = executor;
        this.executionMask = ChannelHandlerMask.mask(handlerClass);
        this.ordered = (executor == null || executor instanceof OrderedEventExecutor);
    }
    
    @Override
    public Channel channel() {
        return this.pipeline.channel();
    }
    
    @Override
    public ChannelPipeline pipeline() {
        return this.pipeline;
    }
    
    @Override
    public ByteBufAllocator alloc() {
        return this.channel().config().getAllocator();
    }
    
    @Override
    public EventExecutor executor() {
        if (this.executor == null) {
            return this.channel().eventLoop();
        }
        return this.executor;
    }
    
    @Override
    public String name() {
        return this.name;
    }
    
    @Override
    public ChannelHandlerContext fireChannelRegistered() {
        invokeChannelRegistered(this.findContextInbound(2));
        return this;
    }
    
    static void invokeChannelRegistered(final AbstractChannelHandlerContext next) {
        final EventExecutor executor = next.executor();
        if (executor.inEventLoop()) {
            next.invokeChannelRegistered();
        }
        else {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    AbstractChannelHandlerContext.this.invokeChannelRegistered();
                }
            });
        }
    }
    
    private void invokeChannelRegistered() {
        if (this.invokeHandler()) {
            try {
                ((ChannelInboundHandler)this.handler()).channelRegistered(this);
            }
            catch (final Throwable t) {
                this.invokeExceptionCaught(t);
            }
        }
        else {
            this.fireChannelRegistered();
        }
    }
    
    @Override
    public ChannelHandlerContext fireChannelUnregistered() {
        invokeChannelUnregistered(this.findContextInbound(4));
        return this;
    }
    
    static void invokeChannelUnregistered(final AbstractChannelHandlerContext next) {
        final EventExecutor executor = next.executor();
        if (executor.inEventLoop()) {
            next.invokeChannelUnregistered();
        }
        else {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    AbstractChannelHandlerContext.this.invokeChannelUnregistered();
                }
            });
        }
    }
    
    private void invokeChannelUnregistered() {
        if (this.invokeHandler()) {
            try {
                ((ChannelInboundHandler)this.handler()).channelUnregistered(this);
            }
            catch (final Throwable t) {
                this.invokeExceptionCaught(t);
            }
        }
        else {
            this.fireChannelUnregistered();
        }
    }
    
    @Override
    public ChannelHandlerContext fireChannelActive() {
        invokeChannelActive(this.findContextInbound(8));
        return this;
    }
    
    static void invokeChannelActive(final AbstractChannelHandlerContext next) {
        final EventExecutor executor = next.executor();
        if (executor.inEventLoop()) {
            next.invokeChannelActive();
        }
        else {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    AbstractChannelHandlerContext.this.invokeChannelActive();
                }
            });
        }
    }
    
    private void invokeChannelActive() {
        if (this.invokeHandler()) {
            try {
                ((ChannelInboundHandler)this.handler()).channelActive(this);
            }
            catch (final Throwable t) {
                this.invokeExceptionCaught(t);
            }
        }
        else {
            this.fireChannelActive();
        }
    }
    
    @Override
    public ChannelHandlerContext fireChannelInactive() {
        invokeChannelInactive(this.findContextInbound(16));
        return this;
    }
    
    static void invokeChannelInactive(final AbstractChannelHandlerContext next) {
        final EventExecutor executor = next.executor();
        if (executor.inEventLoop()) {
            next.invokeChannelInactive();
        }
        else {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    AbstractChannelHandlerContext.this.invokeChannelInactive();
                }
            });
        }
    }
    
    private void invokeChannelInactive() {
        if (this.invokeHandler()) {
            try {
                ((ChannelInboundHandler)this.handler()).channelInactive(this);
            }
            catch (final Throwable t) {
                this.invokeExceptionCaught(t);
            }
        }
        else {
            this.fireChannelInactive();
        }
    }
    
    @Override
    public ChannelHandlerContext fireExceptionCaught(final Throwable cause) {
        invokeExceptionCaught(this.findContextInbound(1), cause);
        return this;
    }
    
    static void invokeExceptionCaught(final AbstractChannelHandlerContext next, final Throwable cause) {
        ObjectUtil.checkNotNull(cause, "cause");
        final EventExecutor executor = next.executor();
        if (executor.inEventLoop()) {
            next.invokeExceptionCaught(cause);
        }
        else {
            try {
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        AbstractChannelHandlerContext.this.invokeExceptionCaught(cause);
                    }
                });
            }
            catch (final Throwable t) {
                if (AbstractChannelHandlerContext.logger.isWarnEnabled()) {
                    AbstractChannelHandlerContext.logger.warn("Failed to submit an exceptionCaught() event.", t);
                    AbstractChannelHandlerContext.logger.warn("The exceptionCaught() event that was failed to submit was:", cause);
                }
            }
        }
    }
    
    private void invokeExceptionCaught(final Throwable cause) {
        if (this.invokeHandler()) {
            try {
                this.handler().exceptionCaught(this, cause);
            }
            catch (final Throwable error) {
                if (AbstractChannelHandlerContext.logger.isDebugEnabled()) {
                    AbstractChannelHandlerContext.logger.debug("An exception {}was thrown by a user handler's exceptionCaught() method while handling the following exception:", ThrowableUtil.stackTraceToString(error), cause);
                }
                else if (AbstractChannelHandlerContext.logger.isWarnEnabled()) {
                    AbstractChannelHandlerContext.logger.warn("An exception '{}' [enable DEBUG level for full stacktrace] was thrown by a user handler's exceptionCaught() method while handling the following exception:", error, cause);
                }
            }
        }
        else {
            this.fireExceptionCaught(cause);
        }
    }
    
    @Override
    public ChannelHandlerContext fireUserEventTriggered(final Object event) {
        invokeUserEventTriggered(this.findContextInbound(128), event);
        return this;
    }
    
    static void invokeUserEventTriggered(final AbstractChannelHandlerContext next, final Object event) {
        ObjectUtil.checkNotNull(event, "event");
        final EventExecutor executor = next.executor();
        if (executor.inEventLoop()) {
            next.invokeUserEventTriggered(event);
        }
        else {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    AbstractChannelHandlerContext.this.invokeUserEventTriggered(event);
                }
            });
        }
    }
    
    private void invokeUserEventTriggered(final Object event) {
        if (this.invokeHandler()) {
            try {
                ((ChannelInboundHandler)this.handler()).userEventTriggered(this, event);
            }
            catch (final Throwable t) {
                this.invokeExceptionCaught(t);
            }
        }
        else {
            this.fireUserEventTriggered(event);
        }
    }
    
    @Override
    public ChannelHandlerContext fireChannelRead(final Object msg) {
        invokeChannelRead(this.findContextInbound(32), msg);
        return this;
    }
    
    static void invokeChannelRead(final AbstractChannelHandlerContext next, final Object msg) {
        final Object m = next.pipeline.touch(ObjectUtil.checkNotNull(msg, "msg"), next);
        final EventExecutor executor = next.executor();
        if (executor.inEventLoop()) {
            next.invokeChannelRead(m);
        }
        else {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    AbstractChannelHandlerContext.this.invokeChannelRead(m);
                }
            });
        }
    }
    
    private void invokeChannelRead(final Object msg) {
        if (this.invokeHandler()) {
            try {
                ((ChannelInboundHandler)this.handler()).channelRead(this, msg);
            }
            catch (final Throwable t) {
                this.invokeExceptionCaught(t);
            }
        }
        else {
            this.fireChannelRead(msg);
        }
    }
    
    @Override
    public ChannelHandlerContext fireChannelReadComplete() {
        invokeChannelReadComplete(this.findContextInbound(64));
        return this;
    }
    
    static void invokeChannelReadComplete(final AbstractChannelHandlerContext next) {
        final EventExecutor executor = next.executor();
        if (executor.inEventLoop()) {
            next.invokeChannelReadComplete();
        }
        else {
            Tasks tasks = next.invokeTasks;
            if (tasks == null) {
                tasks = (next.invokeTasks = new Tasks(next));
            }
            executor.execute(tasks.invokeChannelReadCompleteTask);
        }
    }
    
    private void invokeChannelReadComplete() {
        if (this.invokeHandler()) {
            try {
                ((ChannelInboundHandler)this.handler()).channelReadComplete(this);
            }
            catch (final Throwable t) {
                this.invokeExceptionCaught(t);
            }
        }
        else {
            this.fireChannelReadComplete();
        }
    }
    
    @Override
    public ChannelHandlerContext fireChannelWritabilityChanged() {
        invokeChannelWritabilityChanged(this.findContextInbound(256));
        return this;
    }
    
    static void invokeChannelWritabilityChanged(final AbstractChannelHandlerContext next) {
        final EventExecutor executor = next.executor();
        if (executor.inEventLoop()) {
            next.invokeChannelWritabilityChanged();
        }
        else {
            Tasks tasks = next.invokeTasks;
            if (tasks == null) {
                tasks = (next.invokeTasks = new Tasks(next));
            }
            executor.execute(tasks.invokeChannelWritableStateChangedTask);
        }
    }
    
    private void invokeChannelWritabilityChanged() {
        if (this.invokeHandler()) {
            try {
                ((ChannelInboundHandler)this.handler()).channelWritabilityChanged(this);
            }
            catch (final Throwable t) {
                this.invokeExceptionCaught(t);
            }
        }
        else {
            this.fireChannelWritabilityChanged();
        }
    }
    
    @Override
    public ChannelFuture bind(final SocketAddress localAddress) {
        return this.bind(localAddress, this.newPromise());
    }
    
    @Override
    public ChannelFuture connect(final SocketAddress remoteAddress) {
        return this.connect(remoteAddress, this.newPromise());
    }
    
    @Override
    public ChannelFuture connect(final SocketAddress remoteAddress, final SocketAddress localAddress) {
        return this.connect(remoteAddress, localAddress, this.newPromise());
    }
    
    @Override
    public ChannelFuture disconnect() {
        return this.disconnect(this.newPromise());
    }
    
    @Override
    public ChannelFuture close() {
        return this.close(this.newPromise());
    }
    
    @Override
    public ChannelFuture deregister() {
        return this.deregister(this.newPromise());
    }
    
    @Override
    public ChannelFuture bind(final SocketAddress localAddress, final ChannelPromise promise) {
        ObjectUtil.checkNotNull(localAddress, "localAddress");
        if (this.isNotValidPromise(promise, false)) {
            return promise;
        }
        final AbstractChannelHandlerContext next = this.findContextOutbound(512);
        final EventExecutor executor = next.executor();
        if (executor.inEventLoop()) {
            next.invokeBind(localAddress, promise);
        }
        else {
            safeExecute(executor, new Runnable() {
                @Override
                public void run() {
                    AbstractChannelHandlerContext.this.invokeBind(localAddress, promise);
                }
            }, promise, null, false);
        }
        return promise;
    }
    
    private void invokeBind(final SocketAddress localAddress, final ChannelPromise promise) {
        if (this.invokeHandler()) {
            try {
                ((ChannelOutboundHandler)this.handler()).bind(this, localAddress, promise);
            }
            catch (final Throwable t) {
                notifyOutboundHandlerException(t, promise);
            }
        }
        else {
            this.bind(localAddress, promise);
        }
    }
    
    @Override
    public ChannelFuture connect(final SocketAddress remoteAddress, final ChannelPromise promise) {
        return this.connect(remoteAddress, null, promise);
    }
    
    @Override
    public ChannelFuture connect(final SocketAddress remoteAddress, final SocketAddress localAddress, final ChannelPromise promise) {
        ObjectUtil.checkNotNull(remoteAddress, "remoteAddress");
        if (this.isNotValidPromise(promise, false)) {
            return promise;
        }
        final AbstractChannelHandlerContext next = this.findContextOutbound(1024);
        final EventExecutor executor = next.executor();
        if (executor.inEventLoop()) {
            next.invokeConnect(remoteAddress, localAddress, promise);
        }
        else {
            safeExecute(executor, new Runnable() {
                @Override
                public void run() {
                    AbstractChannelHandlerContext.this.invokeConnect(remoteAddress, localAddress, promise);
                }
            }, promise, null, false);
        }
        return promise;
    }
    
    private void invokeConnect(final SocketAddress remoteAddress, final SocketAddress localAddress, final ChannelPromise promise) {
        if (this.invokeHandler()) {
            try {
                ((ChannelOutboundHandler)this.handler()).connect(this, remoteAddress, localAddress, promise);
            }
            catch (final Throwable t) {
                notifyOutboundHandlerException(t, promise);
            }
        }
        else {
            this.connect(remoteAddress, localAddress, promise);
        }
    }
    
    @Override
    public ChannelFuture disconnect(final ChannelPromise promise) {
        if (!this.channel().metadata().hasDisconnect()) {
            return this.close(promise);
        }
        if (this.isNotValidPromise(promise, false)) {
            return promise;
        }
        final AbstractChannelHandlerContext next = this.findContextOutbound(2048);
        final EventExecutor executor = next.executor();
        if (executor.inEventLoop()) {
            next.invokeDisconnect(promise);
        }
        else {
            safeExecute(executor, new Runnable() {
                @Override
                public void run() {
                    AbstractChannelHandlerContext.this.invokeDisconnect(promise);
                }
            }, promise, null, false);
        }
        return promise;
    }
    
    private void invokeDisconnect(final ChannelPromise promise) {
        if (this.invokeHandler()) {
            try {
                ((ChannelOutboundHandler)this.handler()).disconnect(this, promise);
            }
            catch (final Throwable t) {
                notifyOutboundHandlerException(t, promise);
            }
        }
        else {
            this.disconnect(promise);
        }
    }
    
    @Override
    public ChannelFuture close(final ChannelPromise promise) {
        if (this.isNotValidPromise(promise, false)) {
            return promise;
        }
        final AbstractChannelHandlerContext next = this.findContextOutbound(4096);
        final EventExecutor executor = next.executor();
        if (executor.inEventLoop()) {
            next.invokeClose(promise);
        }
        else {
            safeExecute(executor, new Runnable() {
                @Override
                public void run() {
                    AbstractChannelHandlerContext.this.invokeClose(promise);
                }
            }, promise, null, false);
        }
        return promise;
    }
    
    private void invokeClose(final ChannelPromise promise) {
        if (this.invokeHandler()) {
            try {
                ((ChannelOutboundHandler)this.handler()).close(this, promise);
            }
            catch (final Throwable t) {
                notifyOutboundHandlerException(t, promise);
            }
        }
        else {
            this.close(promise);
        }
    }
    
    @Override
    public ChannelFuture deregister(final ChannelPromise promise) {
        if (this.isNotValidPromise(promise, false)) {
            return promise;
        }
        final AbstractChannelHandlerContext next = this.findContextOutbound(8192);
        final EventExecutor executor = next.executor();
        if (executor.inEventLoop()) {
            next.invokeDeregister(promise);
        }
        else {
            safeExecute(executor, new Runnable() {
                @Override
                public void run() {
                    AbstractChannelHandlerContext.this.invokeDeregister(promise);
                }
            }, promise, null, false);
        }
        return promise;
    }
    
    private void invokeDeregister(final ChannelPromise promise) {
        if (this.invokeHandler()) {
            try {
                ((ChannelOutboundHandler)this.handler()).deregister(this, promise);
            }
            catch (final Throwable t) {
                notifyOutboundHandlerException(t, promise);
            }
        }
        else {
            this.deregister(promise);
        }
    }
    
    @Override
    public ChannelHandlerContext read() {
        final AbstractChannelHandlerContext next = this.findContextOutbound(16384);
        final EventExecutor executor = next.executor();
        if (executor.inEventLoop()) {
            next.invokeRead();
        }
        else {
            Tasks tasks = next.invokeTasks;
            if (tasks == null) {
                tasks = (next.invokeTasks = new Tasks(next));
            }
            executor.execute(tasks.invokeReadTask);
        }
        return this;
    }
    
    private void invokeRead() {
        if (this.invokeHandler()) {
            try {
                ((ChannelOutboundHandler)this.handler()).read(this);
            }
            catch (final Throwable t) {
                this.invokeExceptionCaught(t);
            }
        }
        else {
            this.read();
        }
    }
    
    @Override
    public ChannelFuture write(final Object msg) {
        return this.write(msg, this.newPromise());
    }
    
    @Override
    public ChannelFuture write(final Object msg, final ChannelPromise promise) {
        this.write(msg, false, promise);
        return promise;
    }
    
    void invokeWrite(final Object msg, final ChannelPromise promise) {
        if (this.invokeHandler()) {
            this.invokeWrite0(msg, promise);
        }
        else {
            this.write(msg, promise);
        }
    }
    
    private void invokeWrite0(final Object msg, final ChannelPromise promise) {
        try {
            ((ChannelOutboundHandler)this.handler()).write(this, msg, promise);
        }
        catch (final Throwable t) {
            notifyOutboundHandlerException(t, promise);
        }
    }
    
    @Override
    public ChannelHandlerContext flush() {
        final AbstractChannelHandlerContext next = this.findContextOutbound(65536);
        final EventExecutor executor = next.executor();
        if (executor.inEventLoop()) {
            next.invokeFlush();
        }
        else {
            Tasks tasks = next.invokeTasks;
            if (tasks == null) {
                tasks = (next.invokeTasks = new Tasks(next));
            }
            safeExecute(executor, tasks.invokeFlushTask, this.channel().voidPromise(), null, false);
        }
        return this;
    }
    
    private void invokeFlush() {
        if (this.invokeHandler()) {
            this.invokeFlush0();
        }
        else {
            this.flush();
        }
    }
    
    private void invokeFlush0() {
        try {
            ((ChannelOutboundHandler)this.handler()).flush(this);
        }
        catch (final Throwable t) {
            this.invokeExceptionCaught(t);
        }
    }
    
    @Override
    public ChannelFuture writeAndFlush(final Object msg, final ChannelPromise promise) {
        this.write(msg, true, promise);
        return promise;
    }
    
    void invokeWriteAndFlush(final Object msg, final ChannelPromise promise) {
        if (this.invokeHandler()) {
            this.invokeWrite0(msg, promise);
            this.invokeFlush0();
        }
        else {
            this.writeAndFlush(msg, promise);
        }
    }
    
    private void write(final Object msg, final boolean flush, final ChannelPromise promise) {
        ObjectUtil.checkNotNull(msg, "msg");
        try {
            if (this.isNotValidPromise(promise, true)) {
                ReferenceCountUtil.release(msg);
                return;
            }
        }
        catch (final RuntimeException e) {
            ReferenceCountUtil.release(msg);
            throw e;
        }
        final AbstractChannelHandlerContext next = this.findContextOutbound(flush ? 98304 : 32768);
        final Object m = this.pipeline.touch(msg, next);
        final EventExecutor executor = next.executor();
        if (executor.inEventLoop()) {
            if (flush) {
                next.invokeWriteAndFlush(m, promise);
            }
            else {
                next.invokeWrite(m, promise);
            }
        }
        else {
            final WriteTask task = WriteTask.newInstance(next, m, promise, flush);
            if (!safeExecute(executor, task, promise, m, !flush)) {
                task.cancel();
            }
        }
    }
    
    @Override
    public ChannelFuture writeAndFlush(final Object msg) {
        return this.writeAndFlush(msg, this.newPromise());
    }
    
    private static void notifyOutboundHandlerException(final Throwable cause, final ChannelPromise promise) {
        PromiseNotificationUtil.tryFailure(promise, cause, (promise instanceof VoidChannelPromise) ? null : AbstractChannelHandlerContext.logger);
    }
    
    @Override
    public ChannelPromise newPromise() {
        return new DefaultChannelPromise(this.channel(), this.executor());
    }
    
    @Override
    public ChannelProgressivePromise newProgressivePromise() {
        return new DefaultChannelProgressivePromise(this.channel(), this.executor());
    }
    
    @Override
    public ChannelFuture newSucceededFuture() {
        ChannelFuture succeededFuture = this.succeededFuture;
        if (succeededFuture == null) {
            succeededFuture = (this.succeededFuture = new SucceededChannelFuture(this.channel(), this.executor()));
        }
        return succeededFuture;
    }
    
    @Override
    public ChannelFuture newFailedFuture(final Throwable cause) {
        return new FailedChannelFuture(this.channel(), this.executor(), cause);
    }
    
    private boolean isNotValidPromise(final ChannelPromise promise, final boolean allowVoidPromise) {
        ObjectUtil.checkNotNull(promise, "promise");
        if (promise.isDone()) {
            if (promise.isCancelled()) {
                return true;
            }
            throw new IllegalArgumentException("promise already done: " + promise);
        }
        else {
            if (promise.channel() != this.channel()) {
                throw new IllegalArgumentException(String.format("promise.channel does not match: %s (expected: %s)", promise.channel(), this.channel()));
            }
            if (promise.getClass() == DefaultChannelPromise.class) {
                return false;
            }
            if (!allowVoidPromise && promise instanceof VoidChannelPromise) {
                throw new IllegalArgumentException(StringUtil.simpleClassName(VoidChannelPromise.class) + " not allowed for this operation");
            }
            if (promise instanceof AbstractChannel.CloseFuture) {
                throw new IllegalArgumentException(StringUtil.simpleClassName(AbstractChannel.CloseFuture.class) + " not allowed in a pipeline");
            }
            return false;
        }
    }
    
    private AbstractChannelHandlerContext findContextInbound(final int mask) {
        AbstractChannelHandlerContext ctx = this;
        final EventExecutor currentExecutor = this.executor();
        do {
            ctx = ctx.next;
        } while (skipContext(ctx, currentExecutor, mask, 510));
        return ctx;
    }
    
    private AbstractChannelHandlerContext findContextOutbound(final int mask) {
        AbstractChannelHandlerContext ctx = this;
        final EventExecutor currentExecutor = this.executor();
        do {
            ctx = ctx.prev;
        } while (skipContext(ctx, currentExecutor, mask, 130560));
        return ctx;
    }
    
    private static boolean skipContext(final AbstractChannelHandlerContext ctx, final EventExecutor currentExecutor, final int mask, final int onlyMask) {
        return (ctx.executionMask & (onlyMask | mask)) == 0x0 || (ctx.executor() == currentExecutor && (ctx.executionMask & mask) == 0x0);
    }
    
    @Override
    public ChannelPromise voidPromise() {
        return this.channel().voidPromise();
    }
    
    final void setRemoved() {
        this.handlerState = 3;
    }
    
    final boolean setAddComplete() {
        while (true) {
            final int oldState = this.handlerState;
            if (oldState == 3) {
                return false;
            }
            if (AbstractChannelHandlerContext.HANDLER_STATE_UPDATER.compareAndSet(this, oldState, 2)) {
                return true;
            }
        }
    }
    
    final void setAddPending() {
        final boolean updated = AbstractChannelHandlerContext.HANDLER_STATE_UPDATER.compareAndSet(this, 0, 1);
        assert updated;
    }
    
    final void callHandlerAdded() throws Exception {
        if (this.setAddComplete()) {
            this.handler().handlerAdded(this);
        }
    }
    
    final void callHandlerRemoved() throws Exception {
        try {
            if (this.handlerState == 2) {
                this.handler().handlerRemoved(this);
            }
        }
        finally {
            this.setRemoved();
        }
    }
    
    private boolean invokeHandler() {
        final int handlerState = this.handlerState;
        return handlerState == 2 || (!this.ordered && handlerState == 1);
    }
    
    @Override
    public boolean isRemoved() {
        return this.handlerState == 3;
    }
    
    @Override
    public <T> Attribute<T> attr(final AttributeKey<T> key) {
        return this.channel().attr(key);
    }
    
    @Override
    public <T> boolean hasAttr(final AttributeKey<T> key) {
        return this.channel().hasAttr(key);
    }
    
    private static boolean safeExecute(final EventExecutor executor, final Runnable runnable, final ChannelPromise promise, final Object msg, final boolean lazy) {
        try {
            if (lazy && executor instanceof AbstractEventExecutor) {
                ((AbstractEventExecutor)executor).lazyExecute(runnable);
            }
            else {
                executor.execute(runnable);
            }
            return true;
        }
        catch (final Throwable cause) {
            try {
                if (msg != null) {
                    ReferenceCountUtil.release(msg);
                }
            }
            finally {
                promise.setFailure(cause);
            }
            return false;
        }
    }
    
    @Override
    public String toHintString() {
        return '\'' + this.name + "' will handle the message from this point.";
    }
    
    @Override
    public String toString() {
        return StringUtil.simpleClassName(ChannelHandlerContext.class) + '(' + this.name + ", " + this.channel() + ')';
    }
    
    static {
        logger = InternalLoggerFactory.getInstance(AbstractChannelHandlerContext.class);
        HANDLER_STATE_UPDATER = AtomicIntegerFieldUpdater.newUpdater(AbstractChannelHandlerContext.class, "handlerState");
    }
    
    static final class WriteTask implements Runnable
    {
        private static final ObjectPool<WriteTask> RECYCLER;
        private static final boolean ESTIMATE_TASK_SIZE_ON_SUBMIT;
        private static final int WRITE_TASK_OVERHEAD;
        private final ObjectPool.Handle<WriteTask> handle;
        private AbstractChannelHandlerContext ctx;
        private Object msg;
        private ChannelPromise promise;
        private int size;
        
        static WriteTask newInstance(final AbstractChannelHandlerContext ctx, final Object msg, final ChannelPromise promise, final boolean flush) {
            final WriteTask task = WriteTask.RECYCLER.get();
            init(task, ctx, msg, promise, flush);
            return task;
        }
        
        private WriteTask(final ObjectPool.Handle<? extends WriteTask> handle) {
            this.handle = (ObjectPool.Handle<WriteTask>)handle;
        }
        
        protected static void init(final WriteTask task, final AbstractChannelHandlerContext ctx, final Object msg, final ChannelPromise promise, final boolean flush) {
            task.ctx = ctx;
            task.msg = msg;
            task.promise = promise;
            if (WriteTask.ESTIMATE_TASK_SIZE_ON_SUBMIT) {
                task.size = ctx.pipeline.estimatorHandle().size(msg) + WriteTask.WRITE_TASK_OVERHEAD;
                ctx.pipeline.incrementPendingOutboundBytes(task.size);
            }
            else {
                task.size = 0;
            }
            if (flush) {
                task.size |= Integer.MIN_VALUE;
            }
        }
        
        @Override
        public void run() {
            try {
                this.decrementPendingOutboundBytes();
                if (this.size >= 0) {
                    this.ctx.invokeWrite(this.msg, this.promise);
                }
                else {
                    this.ctx.invokeWriteAndFlush(this.msg, this.promise);
                }
            }
            finally {
                this.recycle();
            }
        }
        
        void cancel() {
            try {
                this.decrementPendingOutboundBytes();
            }
            finally {
                this.recycle();
            }
        }
        
        private void decrementPendingOutboundBytes() {
            if (WriteTask.ESTIMATE_TASK_SIZE_ON_SUBMIT) {
                this.ctx.pipeline.decrementPendingOutboundBytes(this.size & Integer.MAX_VALUE);
            }
        }
        
        private void recycle() {
            this.ctx = null;
            this.msg = null;
            this.promise = null;
            this.handle.recycle(this);
        }
        
        static {
            RECYCLER = ObjectPool.newPool((ObjectPool.ObjectCreator<WriteTask>)new ObjectPool.ObjectCreator<WriteTask>() {
                @Override
                public WriteTask newObject(final ObjectPool.Handle<WriteTask> handle) {
                    return new WriteTask((ObjectPool.Handle)handle);
                }
            });
            ESTIMATE_TASK_SIZE_ON_SUBMIT = SystemPropertyUtil.getBoolean("io.netty.transport.estimateSizeOnSubmit", true);
            WRITE_TASK_OVERHEAD = SystemPropertyUtil.getInt("io.netty.transport.writeTaskSizeOverhead", 32);
        }
    }
    
    private static final class Tasks
    {
        private final AbstractChannelHandlerContext next;
        private final Runnable invokeChannelReadCompleteTask;
        private final Runnable invokeReadTask;
        private final Runnable invokeChannelWritableStateChangedTask;
        private final Runnable invokeFlushTask;
        
        Tasks(final AbstractChannelHandlerContext next) {
            this.invokeChannelReadCompleteTask = new Runnable() {
                @Override
                public void run() {
                    Tasks.this.next.invokeChannelReadComplete();
                }
            };
            this.invokeReadTask = new Runnable() {
                @Override
                public void run() {
                    Tasks.this.next.invokeRead();
                }
            };
            this.invokeChannelWritableStateChangedTask = new Runnable() {
                @Override
                public void run() {
                    Tasks.this.next.invokeChannelWritabilityChanged();
                }
            };
            this.invokeFlushTask = new Runnable() {
                @Override
                public void run() {
                    Tasks.this.next.invokeFlush();
                }
            };
            this.next = next;
        }
    }
}
