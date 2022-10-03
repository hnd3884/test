package io.netty.handler.ssl;

import io.netty.util.concurrent.DefaultPromise;
import io.netty.channel.AbstractCoalescingBufferQueue;
import io.netty.util.internal.logging.InternalLoggerFactory;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.ChannelOption;
import io.netty.util.concurrent.FutureListener;
import io.netty.util.concurrent.PromiseNotifier;
import javax.net.ssl.SSLSession;
import io.netty.util.concurrent.EventExecutor;
import java.util.concurrent.RejectedExecutionException;
import java.util.List;
import io.netty.buffer.ByteBufUtil;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SocketChannel;
import java.io.IOException;
import io.netty.handler.codec.DecoderException;
import java.nio.channels.ClosedChannelException;
import io.netty.buffer.CompositeByteBuf;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.channel.ChannelFutureListener;
import io.netty.buffer.ByteBufAllocator;
import javax.net.ssl.SSLEngineResult;
import javax.net.ssl.SSLException;
import io.netty.buffer.Unpooled;
import io.netty.util.internal.PlatformDependent;
import io.netty.handler.codec.UnsupportedMessageTypeException;
import io.netty.buffer.ByteBuf;
import java.net.SocketAddress;
import io.netty.util.ReferenceCountUtil;
import javax.net.ssl.SSLHandshakeException;
import io.netty.channel.ChannelOutboundInvoker;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelPromise;
import io.netty.channel.ChannelFuture;
import io.netty.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.concurrent.ImmediateExecutor;
import io.netty.channel.Channel;
import io.netty.util.concurrent.Promise;
import java.nio.ByteBuffer;
import java.util.concurrent.Executor;
import javax.net.ssl.SSLEngine;
import io.netty.channel.ChannelHandlerContext;
import java.util.regex.Pattern;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.channel.ChannelOutboundHandler;
import io.netty.handler.codec.ByteToMessageDecoder;

public class SslHandler extends ByteToMessageDecoder implements ChannelOutboundHandler
{
    private static final InternalLogger logger;
    private static final Pattern IGNORABLE_CLASS_IN_STACK;
    private static final Pattern IGNORABLE_ERROR_MESSAGE;
    private static final int STATE_SENT_FIRST_MESSAGE = 1;
    private static final int STATE_FLUSHED_BEFORE_HANDSHAKE = 2;
    private static final int STATE_READ_DURING_HANDSHAKE = 4;
    private static final int STATE_HANDSHAKE_STARTED = 8;
    private static final int STATE_NEEDS_FLUSH = 16;
    private static final int STATE_OUTBOUND_CLOSED = 32;
    private static final int STATE_CLOSE_NOTIFY = 64;
    private static final int STATE_PROCESS_TASK = 128;
    private static final int STATE_FIRE_CHANNEL_READ = 256;
    private static final int STATE_UNWRAP_REENTRY = 512;
    private static final int MAX_PLAINTEXT_LENGTH = 16384;
    private volatile ChannelHandlerContext ctx;
    private final SSLEngine engine;
    private final SslEngineType engineType;
    private final Executor delegatedTaskExecutor;
    private final boolean jdkCompatibilityMode;
    private final ByteBuffer[] singleBuffer;
    private final boolean startTls;
    private final SslTasksRunner sslTaskRunnerForUnwrap;
    private final SslTasksRunner sslTaskRunner;
    private SslHandlerCoalescingBufferQueue pendingUnencryptedWrites;
    private Promise<Channel> handshakePromise;
    private final LazyChannelPromise sslClosePromise;
    private int packetLength;
    private short state;
    private volatile long handshakeTimeoutMillis;
    private volatile long closeNotifyFlushTimeoutMillis;
    private volatile long closeNotifyReadTimeoutMillis;
    volatile int wrapDataSize;
    
    public SslHandler(final SSLEngine engine) {
        this(engine, false);
    }
    
    public SslHandler(final SSLEngine engine, final boolean startTls) {
        this(engine, startTls, ImmediateExecutor.INSTANCE);
    }
    
    public SslHandler(final SSLEngine engine, final Executor delegatedTaskExecutor) {
        this(engine, false, delegatedTaskExecutor);
    }
    
    public SslHandler(final SSLEngine engine, final boolean startTls, final Executor delegatedTaskExecutor) {
        this.singleBuffer = new ByteBuffer[1];
        this.sslTaskRunnerForUnwrap = new SslTasksRunner(true);
        this.sslTaskRunner = new SslTasksRunner(false);
        this.handshakePromise = new LazyChannelPromise();
        this.sslClosePromise = new LazyChannelPromise();
        this.handshakeTimeoutMillis = 10000L;
        this.closeNotifyFlushTimeoutMillis = 3000L;
        this.wrapDataSize = 16384;
        this.engine = ObjectUtil.checkNotNull(engine, "engine");
        this.delegatedTaskExecutor = ObjectUtil.checkNotNull(delegatedTaskExecutor, "delegatedTaskExecutor");
        this.engineType = SslEngineType.forEngine(engine);
        this.startTls = startTls;
        this.jdkCompatibilityMode = this.engineType.jdkCompatibilityMode(engine);
        this.setCumulator(this.engineType.cumulator);
    }
    
    public long getHandshakeTimeoutMillis() {
        return this.handshakeTimeoutMillis;
    }
    
    public void setHandshakeTimeout(final long handshakeTimeout, final TimeUnit unit) {
        ObjectUtil.checkNotNull(unit, "unit");
        this.setHandshakeTimeoutMillis(unit.toMillis(handshakeTimeout));
    }
    
    public void setHandshakeTimeoutMillis(final long handshakeTimeoutMillis) {
        this.handshakeTimeoutMillis = ObjectUtil.checkPositiveOrZero(handshakeTimeoutMillis, "handshakeTimeoutMillis");
    }
    
    public final void setWrapDataSize(final int wrapDataSize) {
        this.wrapDataSize = wrapDataSize;
    }
    
    @Deprecated
    public long getCloseNotifyTimeoutMillis() {
        return this.getCloseNotifyFlushTimeoutMillis();
    }
    
    @Deprecated
    public void setCloseNotifyTimeout(final long closeNotifyTimeout, final TimeUnit unit) {
        this.setCloseNotifyFlushTimeout(closeNotifyTimeout, unit);
    }
    
    @Deprecated
    public void setCloseNotifyTimeoutMillis(final long closeNotifyFlushTimeoutMillis) {
        this.setCloseNotifyFlushTimeoutMillis(closeNotifyFlushTimeoutMillis);
    }
    
    public final long getCloseNotifyFlushTimeoutMillis() {
        return this.closeNotifyFlushTimeoutMillis;
    }
    
    public final void setCloseNotifyFlushTimeout(final long closeNotifyFlushTimeout, final TimeUnit unit) {
        this.setCloseNotifyFlushTimeoutMillis(unit.toMillis(closeNotifyFlushTimeout));
    }
    
    public final void setCloseNotifyFlushTimeoutMillis(final long closeNotifyFlushTimeoutMillis) {
        this.closeNotifyFlushTimeoutMillis = ObjectUtil.checkPositiveOrZero(closeNotifyFlushTimeoutMillis, "closeNotifyFlushTimeoutMillis");
    }
    
    public final long getCloseNotifyReadTimeoutMillis() {
        return this.closeNotifyReadTimeoutMillis;
    }
    
    public final void setCloseNotifyReadTimeout(final long closeNotifyReadTimeout, final TimeUnit unit) {
        this.setCloseNotifyReadTimeoutMillis(unit.toMillis(closeNotifyReadTimeout));
    }
    
    public final void setCloseNotifyReadTimeoutMillis(final long closeNotifyReadTimeoutMillis) {
        this.closeNotifyReadTimeoutMillis = ObjectUtil.checkPositiveOrZero(closeNotifyReadTimeoutMillis, "closeNotifyReadTimeoutMillis");
    }
    
    public SSLEngine engine() {
        return this.engine;
    }
    
    public String applicationProtocol() {
        final SSLEngine engine = this.engine();
        if (!(engine instanceof ApplicationProtocolAccessor)) {
            return null;
        }
        return ((ApplicationProtocolAccessor)engine).getNegotiatedApplicationProtocol();
    }
    
    public Future<Channel> handshakeFuture() {
        return this.handshakePromise;
    }
    
    @Deprecated
    public ChannelFuture close() {
        return this.closeOutbound();
    }
    
    @Deprecated
    public ChannelFuture close(final ChannelPromise promise) {
        return this.closeOutbound(promise);
    }
    
    public ChannelFuture closeOutbound() {
        return this.closeOutbound(this.ctx.newPromise());
    }
    
    public ChannelFuture closeOutbound(final ChannelPromise promise) {
        final ChannelHandlerContext ctx = this.ctx;
        if (ctx.executor().inEventLoop()) {
            this.closeOutbound0(promise);
        }
        else {
            ctx.executor().execute(new Runnable() {
                @Override
                public void run() {
                    SslHandler.this.closeOutbound0(promise);
                }
            });
        }
        return promise;
    }
    
    private void closeOutbound0(final ChannelPromise promise) {
        this.setState(32);
        this.engine.closeOutbound();
        try {
            this.flush(this.ctx, promise);
        }
        catch (final Exception e) {
            if (!promise.tryFailure(e)) {
                SslHandler.logger.warn("{} flush() raised a masked exception.", this.ctx.channel(), e);
            }
        }
    }
    
    public Future<Channel> sslCloseFuture() {
        return this.sslClosePromise;
    }
    
    public void handlerRemoved0(final ChannelHandlerContext ctx) throws Exception {
        try {
            if (!this.pendingUnencryptedWrites.isEmpty()) {
                this.pendingUnencryptedWrites.releaseAndFailAll(ctx, new ChannelException("Pending write on removal of SslHandler"));
            }
            this.pendingUnencryptedWrites = null;
            SSLHandshakeException cause = null;
            if (!this.handshakePromise.isDone()) {
                cause = new SSLHandshakeException("SslHandler removed before handshake completed");
                if (this.handshakePromise.tryFailure(cause)) {
                    ctx.fireUserEventTriggered((Object)new SslHandshakeCompletionEvent(cause));
                }
            }
            if (!this.sslClosePromise.isDone()) {
                if (cause == null) {
                    cause = new SSLHandshakeException("SslHandler removed before handshake completed");
                }
                this.notifyClosePromise(cause);
            }
        }
        finally {
            ReferenceCountUtil.release(this.engine);
        }
    }
    
    @Override
    public void bind(final ChannelHandlerContext ctx, final SocketAddress localAddress, final ChannelPromise promise) throws Exception {
        ctx.bind(localAddress, promise);
    }
    
    @Override
    public void connect(final ChannelHandlerContext ctx, final SocketAddress remoteAddress, final SocketAddress localAddress, final ChannelPromise promise) throws Exception {
        ctx.connect(remoteAddress, localAddress, promise);
    }
    
    @Override
    public void deregister(final ChannelHandlerContext ctx, final ChannelPromise promise) throws Exception {
        ctx.deregister(promise);
    }
    
    @Override
    public void disconnect(final ChannelHandlerContext ctx, final ChannelPromise promise) throws Exception {
        this.closeOutboundAndChannel(ctx, promise, true);
    }
    
    @Override
    public void close(final ChannelHandlerContext ctx, final ChannelPromise promise) throws Exception {
        this.closeOutboundAndChannel(ctx, promise, false);
    }
    
    @Override
    public void read(final ChannelHandlerContext ctx) throws Exception {
        if (!this.handshakePromise.isDone()) {
            this.setState(4);
        }
        ctx.read();
    }
    
    private static IllegalStateException newPendingWritesNullException() {
        return new IllegalStateException("pendingUnencryptedWrites is null, handlerRemoved0 called?");
    }
    
    @Override
    public void write(final ChannelHandlerContext ctx, final Object msg, final ChannelPromise promise) throws Exception {
        if (!(msg instanceof ByteBuf)) {
            final UnsupportedMessageTypeException exception = new UnsupportedMessageTypeException(msg, (Class<?>[])new Class[] { ByteBuf.class });
            ReferenceCountUtil.safeRelease(msg);
            promise.setFailure((Throwable)exception);
        }
        else if (this.pendingUnencryptedWrites == null) {
            ReferenceCountUtil.safeRelease(msg);
            promise.setFailure((Throwable)newPendingWritesNullException());
        }
        else {
            this.pendingUnencryptedWrites.add((ByteBuf)msg, promise);
        }
    }
    
    @Override
    public void flush(final ChannelHandlerContext ctx) throws Exception {
        if (this.startTls && !this.isStateSet(1)) {
            this.setState(1);
            this.pendingUnencryptedWrites.writeAndRemoveAll(ctx);
            this.forceFlush(ctx);
            this.startHandshakeProcessing(true);
            return;
        }
        if (this.isStateSet(128)) {
            return;
        }
        try {
            this.wrapAndFlush(ctx);
        }
        catch (final Throwable cause) {
            this.setHandshakeFailure(ctx, cause);
            PlatformDependent.throwException(cause);
        }
    }
    
    private void wrapAndFlush(final ChannelHandlerContext ctx) throws SSLException {
        if (this.pendingUnencryptedWrites.isEmpty()) {
            this.pendingUnencryptedWrites.add(Unpooled.EMPTY_BUFFER, ctx.newPromise());
        }
        if (!this.handshakePromise.isDone()) {
            this.setState(2);
        }
        try {
            this.wrap(ctx, false);
        }
        finally {
            this.forceFlush(ctx);
        }
    }
    
    private void wrap(final ChannelHandlerContext ctx, final boolean inUnwrap) throws SSLException {
        ByteBuf out = null;
        final ByteBufAllocator alloc = ctx.alloc();
        try {
            final int wrapDataSize = this.wrapDataSize;
        Label_0437:
            while (!ctx.isRemoved()) {
                ChannelPromise promise = ctx.newPromise();
                final ByteBuf buf = (wrapDataSize > 0) ? this.pendingUnencryptedWrites.remove(alloc, wrapDataSize, promise) : this.pendingUnencryptedWrites.removeFirst(promise);
                if (buf == null) {
                    break;
                }
                if (out == null) {
                    out = this.allocateOutNetBuf(ctx, buf.readableBytes(), buf.nioBufferCount());
                }
                final SSLEngineResult result = this.wrap(alloc, this.engine, buf, out);
                if (buf.isReadable()) {
                    this.pendingUnencryptedWrites.addFirst(buf, promise);
                    promise = null;
                }
                else {
                    buf.release();
                }
                if (out.isReadable()) {
                    final ByteBuf b = out;
                    out = null;
                    if (promise != null) {
                        ctx.write(b, promise);
                    }
                    else {
                        ctx.write(b);
                    }
                }
                else if (promise != null) {
                    ctx.write(Unpooled.EMPTY_BUFFER, promise);
                }
                if (result.getStatus() == SSLEngineResult.Status.CLOSED) {
                    Throwable exception = this.handshakePromise.cause();
                    if (exception == null) {
                        exception = this.sslClosePromise.cause();
                        if (exception == null) {
                            exception = new SslClosedEngineException("SSLEngine closed already");
                        }
                    }
                    this.pendingUnencryptedWrites.releaseAndFailAll(ctx, exception);
                    return;
                }
                switch (result.getHandshakeStatus()) {
                    case NEED_TASK: {
                        if (!this.runDelegatedTasks(inUnwrap)) {
                            break Label_0437;
                        }
                        continue;
                    }
                    case FINISHED:
                    case NOT_HANDSHAKING: {
                        this.setHandshakeSuccess();
                        continue;
                    }
                    case NEED_WRAP: {
                        if (result.bytesProduced() > 0 && this.pendingUnencryptedWrites.isEmpty()) {
                            this.pendingUnencryptedWrites.add(Unpooled.EMPTY_BUFFER);
                            continue;
                        }
                        continue;
                    }
                    case NEED_UNWRAP: {
                        this.readIfNeeded(ctx);
                        return;
                    }
                    default: {
                        throw new IllegalStateException("Unknown handshake status: " + result.getHandshakeStatus());
                    }
                }
            }
        }
        finally {
            if (out != null) {
                out.release();
            }
            if (inUnwrap) {
                this.setState(16);
            }
        }
    }
    
    private boolean wrapNonAppData(final ChannelHandlerContext ctx, final boolean inUnwrap) throws SSLException {
        ByteBuf out = null;
        final ByteBufAllocator alloc = ctx.alloc();
        try {
        Label_0350:
            while (!ctx.isRemoved()) {
                if (out == null) {
                    out = this.allocateOutNetBuf(ctx, 2048, 1);
                }
                final SSLEngineResult result = this.wrap(alloc, this.engine, Unpooled.EMPTY_BUFFER, out);
                if (result.bytesProduced() > 0) {
                    ctx.write(out).addListener((GenericFutureListener<? extends Future<? super Void>>)new ChannelFutureListener() {
                        @Override
                        public void operationComplete(final ChannelFuture future) {
                            final Throwable cause = future.cause();
                            if (cause != null) {
                                SslHandler.this.setHandshakeFailureTransportFailure(ctx, cause);
                            }
                        }
                    });
                    if (inUnwrap) {
                        this.setState(16);
                    }
                    out = null;
                }
                final SSLEngineResult.HandshakeStatus status = result.getHandshakeStatus();
                switch (status) {
                    case FINISHED: {
                        if (this.setHandshakeSuccess() && inUnwrap && !this.pendingUnencryptedWrites.isEmpty()) {
                            this.wrap(ctx, true);
                        }
                        return false;
                    }
                    case NEED_TASK: {
                        if (!this.runDelegatedTasks(inUnwrap)) {
                            break Label_0350;
                        }
                        break;
                    }
                    case NEED_UNWRAP: {
                        if (inUnwrap || this.unwrapNonAppData(ctx) <= 0) {
                            return false;
                        }
                        break;
                    }
                    case NEED_WRAP: {
                        break;
                    }
                    case NOT_HANDSHAKING: {
                        if (this.setHandshakeSuccess() && inUnwrap && !this.pendingUnencryptedWrites.isEmpty()) {
                            this.wrap(ctx, true);
                        }
                        if (!inUnwrap) {
                            this.unwrapNonAppData(ctx);
                        }
                        return true;
                    }
                    default: {
                        throw new IllegalStateException("Unknown handshake status: " + result.getHandshakeStatus());
                    }
                }
                if (result.bytesProduced() == 0 && status != SSLEngineResult.HandshakeStatus.NEED_TASK) {
                    break;
                }
                if (result.bytesConsumed() == 0 && result.getHandshakeStatus() == SSLEngineResult.HandshakeStatus.NOT_HANDSHAKING) {
                    break;
                }
            }
        }
        finally {
            if (out != null) {
                out.release();
            }
        }
        return false;
    }
    
    private SSLEngineResult wrap(final ByteBufAllocator alloc, final SSLEngine engine, final ByteBuf in, final ByteBuf out) throws SSLException {
        ByteBuf newDirectIn = null;
        try {
            final int readerIndex = in.readerIndex();
            final int readableBytes = in.readableBytes();
            ByteBuffer[] in2;
            if (in.isDirect() || !this.engineType.wantsDirectBuffer) {
                if (!(in instanceof CompositeByteBuf) && in.nioBufferCount() == 1) {
                    in2 = this.singleBuffer;
                    in2[0] = in.internalNioBuffer(readerIndex, readableBytes);
                }
                else {
                    in2 = in.nioBuffers();
                }
            }
            else {
                newDirectIn = alloc.directBuffer(readableBytes);
                newDirectIn.writeBytes(in, readerIndex, readableBytes);
                in2 = this.singleBuffer;
                in2[0] = newDirectIn.internalNioBuffer(newDirectIn.readerIndex(), readableBytes);
            }
            SSLEngineResult result;
            while (true) {
                final ByteBuffer out2 = out.nioBuffer(out.writerIndex(), out.writableBytes());
                result = engine.wrap(in2, out2);
                in.skipBytes(result.bytesConsumed());
                out.writerIndex(out.writerIndex() + result.bytesProduced());
                if (result.getStatus() != SSLEngineResult.Status.BUFFER_OVERFLOW) {
                    break;
                }
                out.ensureWritable(engine.getSession().getPacketBufferSize());
            }
            return result;
        }
        finally {
            this.singleBuffer[0] = null;
            if (newDirectIn != null) {
                newDirectIn.release();
            }
        }
    }
    
    @Override
    public void channelInactive(final ChannelHandlerContext ctx) throws Exception {
        final boolean handshakeFailed = this.handshakePromise.cause() != null;
        final ClosedChannelException exception = new ClosedChannelException();
        this.setHandshakeFailure(ctx, exception, !this.isStateSet(32), this.isStateSet(8), false);
        this.notifyClosePromise(exception);
        try {
            super.channelInactive(ctx);
        }
        catch (final DecoderException e) {
            if (!handshakeFailed || !(e.getCause() instanceof SSLException)) {
                throw e;
            }
        }
    }
    
    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) throws Exception {
        if (this.ignoreException(cause)) {
            if (SslHandler.logger.isDebugEnabled()) {
                SslHandler.logger.debug("{} Swallowing a harmless 'connection reset by peer / broken pipe' error that occurred while writing close_notify in response to the peer's close_notify", ctx.channel(), cause);
            }
            if (ctx.channel().isActive()) {
                ctx.close();
            }
        }
        else {
            ctx.fireExceptionCaught(cause);
        }
    }
    
    private boolean ignoreException(final Throwable t) {
        if (!(t instanceof SSLException) && t instanceof IOException && this.sslClosePromise.isDone()) {
            final String message = t.getMessage();
            if (message != null && SslHandler.IGNORABLE_ERROR_MESSAGE.matcher(message).matches()) {
                return true;
            }
            final StackTraceElement[] stackTrace;
            final StackTraceElement[] elements = stackTrace = t.getStackTrace();
            for (final StackTraceElement element : stackTrace) {
                final String classname = element.getClassName();
                final String methodname = element.getMethodName();
                if (!classname.startsWith("io.netty.")) {
                    if ("read".equals(methodname)) {
                        if (SslHandler.IGNORABLE_CLASS_IN_STACK.matcher(classname).matches()) {
                            return true;
                        }
                        try {
                            final Class<?> clazz = PlatformDependent.getClassLoader(this.getClass()).loadClass(classname);
                            if (SocketChannel.class.isAssignableFrom(clazz) || DatagramChannel.class.isAssignableFrom(clazz)) {
                                return true;
                            }
                            if (PlatformDependent.javaVersion() >= 7 && "com.sun.nio.sctp.SctpChannel".equals(clazz.getSuperclass().getName())) {
                                return true;
                            }
                        }
                        catch (final Throwable cause) {
                            if (SslHandler.logger.isDebugEnabled()) {
                                SslHandler.logger.debug("Unexpected exception while loading class {} classname {}", this.getClass(), classname, cause);
                            }
                        }
                    }
                }
            }
        }
        return false;
    }
    
    public static boolean isEncrypted(final ByteBuf buffer) {
        if (buffer.readableBytes() < 5) {
            throw new IllegalArgumentException("buffer must have at least 5 readable bytes");
        }
        return SslUtils.getEncryptedPacketLength(buffer, buffer.readerIndex()) != -2;
    }
    
    private void decodeJdkCompatible(final ChannelHandlerContext ctx, final ByteBuf in) throws NotSslRecordException {
        int packetLength = this.packetLength;
        if (packetLength > 0) {
            if (in.readableBytes() < packetLength) {
                return;
            }
        }
        else {
            final int readableBytes = in.readableBytes();
            if (readableBytes < 5) {
                return;
            }
            packetLength = SslUtils.getEncryptedPacketLength(in, in.readerIndex());
            if (packetLength == -2) {
                final NotSslRecordException e = new NotSslRecordException("not an SSL/TLS record: " + ByteBufUtil.hexDump(in));
                in.skipBytes(in.readableBytes());
                this.setHandshakeFailure(ctx, e);
                throw e;
            }
            assert packetLength > 0;
            if (packetLength > readableBytes) {
                this.packetLength = packetLength;
                return;
            }
        }
        this.packetLength = 0;
        try {
            final int bytesConsumed = this.unwrap(ctx, in, packetLength);
            assert !(!this.engine.isInboundDone()) : "we feed the SSLEngine a packets worth of data: " + packetLength + " but it only consumed: " + bytesConsumed;
        }
        catch (final Throwable cause) {
            this.handleUnwrapThrowable(ctx, cause);
        }
    }
    
    private void decodeNonJdkCompatible(final ChannelHandlerContext ctx, final ByteBuf in) {
        try {
            this.unwrap(ctx, in, in.readableBytes());
        }
        catch (final Throwable cause) {
            this.handleUnwrapThrowable(ctx, cause);
        }
    }
    
    private void handleUnwrapThrowable(final ChannelHandlerContext ctx, final Throwable cause) {
        try {
            if (this.handshakePromise.tryFailure(cause)) {
                ctx.fireUserEventTriggered((Object)new SslHandshakeCompletionEvent(cause));
            }
            if (this.pendingUnencryptedWrites != null) {
                this.wrapAndFlush(ctx);
            }
        }
        catch (final SSLException ex) {
            SslHandler.logger.debug("SSLException during trying to call SSLEngine.wrap(...) because of an previous SSLException, ignoring...", ex);
        }
        finally {
            this.setHandshakeFailure(ctx, cause, true, false, true);
        }
        PlatformDependent.throwException(cause);
    }
    
    @Override
    protected void decode(final ChannelHandlerContext ctx, final ByteBuf in, final List<Object> out) throws SSLException {
        if (this.isStateSet(128)) {
            return;
        }
        if (this.jdkCompatibilityMode) {
            this.decodeJdkCompatible(ctx, in);
        }
        else {
            this.decodeNonJdkCompatible(ctx, in);
        }
    }
    
    @Override
    public void channelReadComplete(final ChannelHandlerContext ctx) throws Exception {
        this.channelReadComplete0(ctx);
    }
    
    private void channelReadComplete0(final ChannelHandlerContext ctx) {
        this.discardSomeReadBytes();
        this.flushIfNeeded(ctx);
        this.readIfNeeded(ctx);
        this.clearState(256);
        ctx.fireChannelReadComplete();
    }
    
    private void readIfNeeded(final ChannelHandlerContext ctx) {
        if (!ctx.channel().config().isAutoRead() && (!this.isStateSet(256) || !this.handshakePromise.isDone())) {
            ctx.read();
        }
    }
    
    private void flushIfNeeded(final ChannelHandlerContext ctx) {
        if (this.isStateSet(16)) {
            this.forceFlush(ctx);
        }
    }
    
    private int unwrapNonAppData(final ChannelHandlerContext ctx) throws SSLException {
        return this.unwrap(ctx, Unpooled.EMPTY_BUFFER, 0);
    }
    
    private int unwrap(final ChannelHandlerContext ctx, final ByteBuf packet, int length) throws SSLException {
        final int originalLength = length;
        boolean wrapLater = false;
        boolean notifyClosure = false;
        boolean executedRead = false;
        ByteBuf decodeOut = this.allocate(ctx, length);
        try {
            do {
                final SSLEngineResult result = this.engineType.unwrap(this, packet, length, decodeOut);
                final SSLEngineResult.Status status = result.getStatus();
                final SSLEngineResult.HandshakeStatus handshakeStatus = result.getHandshakeStatus();
                final int produced = result.bytesProduced();
                final int consumed = result.bytesConsumed();
                packet.skipBytes(consumed);
                length -= consumed;
                if (handshakeStatus == SSLEngineResult.HandshakeStatus.FINISHED || handshakeStatus == SSLEngineResult.HandshakeStatus.NOT_HANDSHAKING) {
                    final boolean b = wrapLater;
                    boolean b2 = false;
                    Label_0130: {
                        Label_0125: {
                            if (decodeOut.isReadable()) {
                                if (this.setHandshakeSuccessUnwrapMarkReentry()) {
                                    break Label_0125;
                                }
                            }
                            else if (this.setHandshakeSuccess()) {
                                break Label_0125;
                            }
                            if (handshakeStatus != SSLEngineResult.HandshakeStatus.FINISHED) {
                                b2 = false;
                                break Label_0130;
                            }
                        }
                        b2 = true;
                    }
                    wrapLater = (b | b2);
                }
                if (decodeOut.isReadable()) {
                    this.setState(256);
                    if (this.isStateSet(512)) {
                        executedRead = true;
                        this.executeChannelRead(ctx, decodeOut);
                    }
                    else {
                        ctx.fireChannelRead((Object)decodeOut);
                    }
                    decodeOut = null;
                }
                if (status == SSLEngineResult.Status.CLOSED) {
                    notifyClosure = true;
                }
                else if (status == SSLEngineResult.Status.BUFFER_OVERFLOW) {
                    if (decodeOut != null) {
                        decodeOut.release();
                    }
                    final int applicationBufferSize = this.engine.getSession().getApplicationBufferSize();
                    decodeOut = this.allocate(ctx, this.engineType.calculatePendingData(this, (applicationBufferSize < produced) ? applicationBufferSize : (applicationBufferSize - produced)));
                    continue;
                }
                if (handshakeStatus == SSLEngineResult.HandshakeStatus.NEED_TASK) {
                    final boolean pending = this.runDelegatedTasks(true);
                    if (!pending) {
                        wrapLater = false;
                        break;
                    }
                }
                else if (handshakeStatus == SSLEngineResult.HandshakeStatus.NEED_WRAP && this.wrapNonAppData(ctx, true) && length == 0) {
                    break;
                }
                if (status == SSLEngineResult.Status.BUFFER_UNDERFLOW || (handshakeStatus != SSLEngineResult.HandshakeStatus.NEED_TASK && ((consumed == 0 && produced == 0) || (length == 0 && handshakeStatus == SSLEngineResult.HandshakeStatus.NOT_HANDSHAKING)))) {
                    if (handshakeStatus == SSLEngineResult.HandshakeStatus.NEED_UNWRAP) {
                        this.readIfNeeded(ctx);
                        break;
                    }
                    break;
                }
                else {
                    if (decodeOut != null) {
                        continue;
                    }
                    decodeOut = this.allocate(ctx, length);
                }
            } while (!ctx.isRemoved());
            if (this.isStateSet(2) && this.handshakePromise.isDone()) {
                this.clearState(2);
                wrapLater = true;
            }
            if (wrapLater) {
                this.wrap(ctx, true);
            }
        }
        finally {
            if (decodeOut != null) {
                decodeOut.release();
            }
            if (notifyClosure) {
                if (executedRead) {
                    this.executeNotifyClosePromise(ctx);
                }
                else {
                    this.notifyClosePromise(null);
                }
            }
        }
        return originalLength - length;
    }
    
    private boolean setHandshakeSuccessUnwrapMarkReentry() {
        final boolean setReentryState = !this.isStateSet(512);
        if (setReentryState) {
            this.setState(512);
        }
        try {
            return this.setHandshakeSuccess();
        }
        finally {
            if (setReentryState) {
                this.clearState(512);
            }
        }
    }
    
    private void executeNotifyClosePromise(final ChannelHandlerContext ctx) {
        try {
            ctx.executor().execute(new Runnable() {
                @Override
                public void run() {
                    SslHandler.this.notifyClosePromise(null);
                }
            });
        }
        catch (final RejectedExecutionException e) {
            this.notifyClosePromise(e);
        }
    }
    
    private void executeChannelRead(final ChannelHandlerContext ctx, final ByteBuf decodedOut) {
        try {
            ctx.executor().execute(new Runnable() {
                @Override
                public void run() {
                    ctx.fireChannelRead((Object)decodedOut);
                }
            });
        }
        catch (final RejectedExecutionException e) {
            decodedOut.release();
            throw e;
        }
    }
    
    private static ByteBuffer toByteBuffer(final ByteBuf out, final int index, final int len) {
        return (out.nioBufferCount() == 1) ? out.internalNioBuffer(index, len) : out.nioBuffer(index, len);
    }
    
    private static boolean inEventLoop(final Executor executor) {
        return executor instanceof EventExecutor && ((EventExecutor)executor).inEventLoop();
    }
    
    private boolean runDelegatedTasks(final boolean inUnwrap) {
        if (this.delegatedTaskExecutor == ImmediateExecutor.INSTANCE || inEventLoop(this.delegatedTaskExecutor)) {
            while (true) {
                final Runnable task = this.engine.getDelegatedTask();
                if (task == null) {
                    break;
                }
                this.setState(128);
                if (task instanceof AsyncRunnable) {
                    boolean pending = false;
                    try {
                        final AsyncRunnable asyncTask = (AsyncRunnable)task;
                        final AsyncTaskCompletionHandler completionHandler = new AsyncTaskCompletionHandler(inUnwrap);
                        asyncTask.run(completionHandler);
                        pending = completionHandler.resumeLater();
                        if (pending) {
                            return false;
                        }
                        continue;
                    }
                    finally {
                        if (!pending) {
                            this.clearState(128);
                        }
                    }
                }
                else {
                    try {
                        task.run();
                    }
                    finally {
                        this.clearState(128);
                    }
                }
            }
            return true;
        }
        this.executeDelegatedTask(inUnwrap);
        return false;
    }
    
    private SslTasksRunner getTaskRunner(final boolean inUnwrap) {
        return inUnwrap ? this.sslTaskRunnerForUnwrap : this.sslTaskRunner;
    }
    
    private void executeDelegatedTask(final boolean inUnwrap) {
        this.executeDelegatedTask(this.getTaskRunner(inUnwrap));
    }
    
    private void executeDelegatedTask(final SslTasksRunner task) {
        this.setState(128);
        try {
            this.delegatedTaskExecutor.execute(task);
        }
        catch (final RejectedExecutionException e) {
            this.clearState(128);
            throw e;
        }
    }
    
    private boolean setHandshakeSuccess() {
        final boolean notified;
        if (notified = (!this.handshakePromise.isDone() && this.handshakePromise.trySuccess(this.ctx.channel()))) {
            if (SslHandler.logger.isDebugEnabled()) {
                final SSLSession session = this.engine.getSession();
                SslHandler.logger.debug("{} HANDSHAKEN: protocol:{} cipher suite:{}", this.ctx.channel(), session.getProtocol(), session.getCipherSuite());
            }
            this.ctx.fireUserEventTriggered((Object)SslHandshakeCompletionEvent.SUCCESS);
        }
        if (this.isStateSet(4)) {
            this.clearState(4);
            if (!this.ctx.channel().config().isAutoRead()) {
                this.ctx.read();
            }
        }
        return notified;
    }
    
    private void setHandshakeFailure(final ChannelHandlerContext ctx, final Throwable cause) {
        this.setHandshakeFailure(ctx, cause, true, true, false);
    }
    
    private void setHandshakeFailure(final ChannelHandlerContext ctx, final Throwable cause, final boolean closeInbound, final boolean notify, final boolean alwaysFlushAndClose) {
        try {
            this.setState(32);
            this.engine.closeOutbound();
            if (closeInbound) {
                try {
                    this.engine.closeInbound();
                }
                catch (final SSLException e) {
                    if (SslHandler.logger.isDebugEnabled()) {
                        final String msg = e.getMessage();
                        if (msg == null || (!msg.contains("possible truncation attack") && !msg.contains("closing inbound before receiving peer's close_notify"))) {
                            SslHandler.logger.debug("{} SSLEngine.closeInbound() raised an exception.", ctx.channel(), e);
                        }
                    }
                }
            }
            if (this.handshakePromise.tryFailure(cause) || alwaysFlushAndClose) {
                SslUtils.handleHandshakeFailure(ctx, cause, notify);
            }
        }
        finally {
            this.releaseAndFailAll(ctx, cause);
        }
    }
    
    private void setHandshakeFailureTransportFailure(final ChannelHandlerContext ctx, final Throwable cause) {
        try {
            final SSLException transportFailure = new SSLException("failure when writing TLS control frames", cause);
            this.releaseAndFailAll(ctx, transportFailure);
            if (this.handshakePromise.tryFailure(transportFailure)) {
                ctx.fireUserEventTriggered((Object)new SslHandshakeCompletionEvent(transportFailure));
            }
        }
        finally {
            ctx.close();
        }
    }
    
    private void releaseAndFailAll(final ChannelHandlerContext ctx, final Throwable cause) {
        if (this.pendingUnencryptedWrites != null) {
            this.pendingUnencryptedWrites.releaseAndFailAll(ctx, cause);
        }
    }
    
    private void notifyClosePromise(final Throwable cause) {
        if (cause == null) {
            if (this.sslClosePromise.trySuccess(this.ctx.channel())) {
                this.ctx.fireUserEventTriggered((Object)SslCloseCompletionEvent.SUCCESS);
            }
        }
        else if (this.sslClosePromise.tryFailure(cause)) {
            this.ctx.fireUserEventTriggered((Object)new SslCloseCompletionEvent(cause));
        }
    }
    
    private void closeOutboundAndChannel(final ChannelHandlerContext ctx, final ChannelPromise promise, final boolean disconnect) throws Exception {
        this.setState(32);
        this.engine.closeOutbound();
        if (!ctx.channel().isActive()) {
            if (disconnect) {
                ctx.disconnect(promise);
            }
            else {
                ctx.close(promise);
            }
            return;
        }
        final ChannelPromise closeNotifyPromise = ctx.newPromise();
        try {
            this.flush(ctx, closeNotifyPromise);
        }
        finally {
            if (!this.isStateSet(64)) {
                this.setState(64);
                this.safeClose(ctx, closeNotifyPromise, PromiseNotifier.cascade(false, ctx.newPromise(), (Promise<? super Object>)promise));
            }
            else {
                this.sslClosePromise.addListener(new FutureListener<Channel>() {
                    @Override
                    public void operationComplete(final Future<Channel> future) {
                        promise.setSuccess();
                    }
                });
            }
        }
    }
    
    private void flush(final ChannelHandlerContext ctx, final ChannelPromise promise) throws Exception {
        if (this.pendingUnencryptedWrites != null) {
            this.pendingUnencryptedWrites.add(Unpooled.EMPTY_BUFFER, promise);
        }
        else {
            promise.setFailure((Throwable)newPendingWritesNullException());
        }
        this.flush(ctx);
    }
    
    @Override
    public void handlerAdded(final ChannelHandlerContext ctx) throws Exception {
        this.ctx = ctx;
        final Channel channel = ctx.channel();
        this.pendingUnencryptedWrites = new SslHandlerCoalescingBufferQueue(channel, 16);
        final boolean fastOpen = Boolean.TRUE.equals(channel.config().getOption(ChannelOption.TCP_FASTOPEN_CONNECT));
        final boolean active = channel.isActive();
        if (active || fastOpen) {
            this.startHandshakeProcessing(active);
            final ChannelOutboundBuffer outboundBuffer;
            if (fastOpen && ((outboundBuffer = channel.unsafe().outboundBuffer()) == null || outboundBuffer.totalPendingWriteBytes() > 0L)) {
                this.setState(16);
            }
        }
    }
    
    private void startHandshakeProcessing(final boolean flushAtEnd) {
        if (!this.isStateSet(8)) {
            this.setState(8);
            if (this.engine.getUseClientMode()) {
                this.handshake(flushAtEnd);
            }
            this.applyHandshakeTimeout();
        }
        else if (this.isStateSet(16)) {
            this.forceFlush(this.ctx);
        }
    }
    
    public Future<Channel> renegotiate() {
        final ChannelHandlerContext ctx = this.ctx;
        if (ctx == null) {
            throw new IllegalStateException();
        }
        return this.renegotiate(ctx.executor().newPromise());
    }
    
    public Future<Channel> renegotiate(final Promise<Channel> promise) {
        ObjectUtil.checkNotNull(promise, "promise");
        final ChannelHandlerContext ctx = this.ctx;
        if (ctx == null) {
            throw new IllegalStateException();
        }
        final EventExecutor executor = ctx.executor();
        if (!executor.inEventLoop()) {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    SslHandler.this.renegotiateOnEventLoop(promise);
                }
            });
            return promise;
        }
        this.renegotiateOnEventLoop(promise);
        return promise;
    }
    
    private void renegotiateOnEventLoop(final Promise<Channel> newHandshakePromise) {
        final Promise<Channel> oldHandshakePromise = this.handshakePromise;
        if (!oldHandshakePromise.isDone()) {
            PromiseNotifier.cascade(oldHandshakePromise, (Promise<? super Object>)newHandshakePromise);
        }
        else {
            this.handshakePromise = newHandshakePromise;
            this.handshake(true);
            this.applyHandshakeTimeout();
        }
    }
    
    private void handshake(final boolean flushAtEnd) {
        if (this.engine.getHandshakeStatus() != SSLEngineResult.HandshakeStatus.NOT_HANDSHAKING) {
            return;
        }
        if (this.handshakePromise.isDone()) {
            return;
        }
        final ChannelHandlerContext ctx = this.ctx;
        try {
            this.engine.beginHandshake();
            this.wrapNonAppData(ctx, false);
        }
        catch (final Throwable e) {
            this.setHandshakeFailure(ctx, e);
        }
        finally {
            if (flushAtEnd) {
                this.forceFlush(ctx);
            }
        }
    }
    
    private void applyHandshakeTimeout() {
        final Promise<Channel> localHandshakePromise = this.handshakePromise;
        final long handshakeTimeoutMillis = this.handshakeTimeoutMillis;
        if (handshakeTimeoutMillis <= 0L || localHandshakePromise.isDone()) {
            return;
        }
        final Future<?> timeoutFuture = this.ctx.executor().schedule((Runnable)new Runnable() {
            @Override
            public void run() {
                if (localHandshakePromise.isDone()) {
                    return;
                }
                final SSLException exception = new SslHandshakeTimeoutException("handshake timed out after " + handshakeTimeoutMillis + "ms");
                try {
                    if (localHandshakePromise.tryFailure(exception)) {
                        SslUtils.handleHandshakeFailure(SslHandler.this.ctx, exception, true);
                    }
                }
                finally {
                    SslHandler.this.releaseAndFailAll(SslHandler.this.ctx, exception);
                }
            }
        }, handshakeTimeoutMillis, TimeUnit.MILLISECONDS);
        localHandshakePromise.addListener((GenericFutureListener<? extends Future<? super Channel>>)new FutureListener<Channel>() {
            @Override
            public void operationComplete(final Future<Channel> f) throws Exception {
                timeoutFuture.cancel(false);
            }
        });
    }
    
    private void forceFlush(final ChannelHandlerContext ctx) {
        this.clearState(16);
        ctx.flush();
    }
    
    @Override
    public void channelActive(final ChannelHandlerContext ctx) throws Exception {
        if (!this.startTls) {
            this.startHandshakeProcessing(true);
        }
        ctx.fireChannelActive();
    }
    
    private void safeClose(final ChannelHandlerContext ctx, final ChannelFuture flushFuture, final ChannelPromise promise) {
        if (!ctx.channel().isActive()) {
            ctx.close(promise);
            return;
        }
        Future<?> timeoutFuture;
        if (!flushFuture.isDone()) {
            final long closeNotifyTimeout = this.closeNotifyFlushTimeoutMillis;
            if (closeNotifyTimeout > 0L) {
                timeoutFuture = ctx.executor().schedule((Runnable)new Runnable() {
                    @Override
                    public void run() {
                        if (!flushFuture.isDone()) {
                            SslHandler.logger.warn("{} Last write attempt timed out; force-closing the connection.", ctx.channel());
                            addCloseListener(ctx.close(ctx.newPromise()), promise);
                        }
                    }
                }, closeNotifyTimeout, TimeUnit.MILLISECONDS);
            }
            else {
                timeoutFuture = null;
            }
        }
        else {
            timeoutFuture = null;
        }
        flushFuture.addListener((GenericFutureListener<? extends Future<? super Void>>)new ChannelFutureListener() {
            @Override
            public void operationComplete(final ChannelFuture f) {
                if (timeoutFuture != null) {
                    timeoutFuture.cancel(false);
                }
                final long closeNotifyReadTimeout = SslHandler.this.closeNotifyReadTimeoutMillis;
                if (closeNotifyReadTimeout <= 0L) {
                    addCloseListener(ctx.close(ctx.newPromise()), promise);
                }
                else {
                    Future<?> closeNotifyReadTimeoutFuture;
                    if (!SslHandler.this.sslClosePromise.isDone()) {
                        closeNotifyReadTimeoutFuture = ctx.executor().schedule((Runnable)new Runnable() {
                            @Override
                            public void run() {
                                if (!SslHandler.this.sslClosePromise.isDone()) {
                                    SslHandler.logger.debug("{} did not receive close_notify in {}ms; force-closing the connection.", ctx.channel(), closeNotifyReadTimeout);
                                    addCloseListener(ctx.close(ctx.newPromise()), promise);
                                }
                            }
                        }, closeNotifyReadTimeout, TimeUnit.MILLISECONDS);
                    }
                    else {
                        closeNotifyReadTimeoutFuture = null;
                    }
                    SslHandler.this.sslClosePromise.addListener(new FutureListener<Channel>() {
                        @Override
                        public void operationComplete(final Future<Channel> future) throws Exception {
                            if (closeNotifyReadTimeoutFuture != null) {
                                closeNotifyReadTimeoutFuture.cancel(false);
                            }
                            addCloseListener(ctx.close(ctx.newPromise()), promise);
                        }
                    });
                }
            }
        });
    }
    
    private static void addCloseListener(final ChannelFuture future, final ChannelPromise promise) {
        PromiseNotifier.cascade(false, future, (Promise<? super Object>)promise);
    }
    
    private ByteBuf allocate(final ChannelHandlerContext ctx, final int capacity) {
        final ByteBufAllocator alloc = ctx.alloc();
        if (this.engineType.wantsDirectBuffer) {
            return alloc.directBuffer(capacity);
        }
        return alloc.buffer(capacity);
    }
    
    private ByteBuf allocateOutNetBuf(final ChannelHandlerContext ctx, final int pendingBytes, final int numComponents) {
        return this.engineType.allocateWrapBuffer(this, ctx.alloc(), pendingBytes, numComponents);
    }
    
    private boolean isStateSet(final int bit) {
        return (this.state & bit) == bit;
    }
    
    private void setState(final int bit) {
        this.state |= (short)bit;
    }
    
    private void clearState(final int bit) {
        this.state &= (short)~bit;
    }
    
    private static boolean attemptCopyToCumulation(final ByteBuf cumulation, final ByteBuf next, final int wrapDataSize) {
        final int inReadableBytes = next.readableBytes();
        final int cumulationCapacity = cumulation.capacity();
        if (wrapDataSize - cumulation.readableBytes() >= inReadableBytes && ((cumulation.isWritable(inReadableBytes) && cumulationCapacity >= wrapDataSize) || (cumulationCapacity < wrapDataSize && ByteBufUtil.ensureWritableSuccess(cumulation.ensureWritable(inReadableBytes, false))))) {
            cumulation.writeBytes(next);
            next.release();
            return true;
        }
        return false;
    }
    
    static {
        logger = InternalLoggerFactory.getInstance(SslHandler.class);
        IGNORABLE_CLASS_IN_STACK = Pattern.compile("^.*(?:Socket|Datagram|Sctp|Udt)Channel.*$");
        IGNORABLE_ERROR_MESSAGE = Pattern.compile("^.*(?:connection.*(?:reset|closed|abort|broken)|broken.*pipe).*$", 2);
    }
    
    private enum SslEngineType
    {
        TCNATIVE(true, ByteToMessageDecoder.COMPOSITE_CUMULATOR) {
            @Override
            SSLEngineResult unwrap(final SslHandler handler, final ByteBuf in, final int len, final ByteBuf out) throws SSLException {
                final int nioBufferCount = in.nioBufferCount();
                final int writerIndex = out.writerIndex();
                SSLEngineResult result;
                if (nioBufferCount > 1) {
                    final ReferenceCountedOpenSslEngine opensslEngine = (ReferenceCountedOpenSslEngine)handler.engine;
                    try {
                        handler.singleBuffer[0] = toByteBuffer(out, writerIndex, out.writableBytes());
                        result = opensslEngine.unwrap(in.nioBuffers(in.readerIndex(), len), handler.singleBuffer);
                    }
                    finally {
                        handler.singleBuffer[0] = null;
                    }
                }
                else {
                    result = handler.engine.unwrap(toByteBuffer(in, in.readerIndex(), len), toByteBuffer(out, writerIndex, out.writableBytes()));
                }
                out.writerIndex(writerIndex + result.bytesProduced());
                return result;
            }
            
            @Override
            ByteBuf allocateWrapBuffer(final SslHandler handler, final ByteBufAllocator allocator, final int pendingBytes, final int numComponents) {
                return allocator.directBuffer(((ReferenceCountedOpenSslEngine)handler.engine).calculateMaxLengthForWrap(pendingBytes, numComponents));
            }
            
            @Override
            int calculatePendingData(final SslHandler handler, final int guess) {
                final int sslPending = ((ReferenceCountedOpenSslEngine)handler.engine).sslPending();
                return (sslPending > 0) ? sslPending : guess;
            }
            
            @Override
            boolean jdkCompatibilityMode(final SSLEngine engine) {
                return ((ReferenceCountedOpenSslEngine)engine).jdkCompatibilityMode;
            }
        }, 
        CONSCRYPT(true, ByteToMessageDecoder.COMPOSITE_CUMULATOR) {
            @Override
            SSLEngineResult unwrap(final SslHandler handler, final ByteBuf in, final int len, final ByteBuf out) throws SSLException {
                final int nioBufferCount = in.nioBufferCount();
                final int writerIndex = out.writerIndex();
                SSLEngineResult result;
                if (nioBufferCount > 1) {
                    try {
                        handler.singleBuffer[0] = toByteBuffer(out, writerIndex, out.writableBytes());
                        result = ((ConscryptAlpnSslEngine)handler.engine).unwrap(in.nioBuffers(in.readerIndex(), len), handler.singleBuffer);
                    }
                    finally {
                        handler.singleBuffer[0] = null;
                    }
                }
                else {
                    result = handler.engine.unwrap(toByteBuffer(in, in.readerIndex(), len), toByteBuffer(out, writerIndex, out.writableBytes()));
                }
                out.writerIndex(writerIndex + result.bytesProduced());
                return result;
            }
            
            @Override
            ByteBuf allocateWrapBuffer(final SslHandler handler, final ByteBufAllocator allocator, final int pendingBytes, final int numComponents) {
                return allocator.directBuffer(((ConscryptAlpnSslEngine)handler.engine).calculateOutNetBufSize(pendingBytes, numComponents));
            }
            
            @Override
            int calculatePendingData(final SslHandler handler, final int guess) {
                return guess;
            }
            
            @Override
            boolean jdkCompatibilityMode(final SSLEngine engine) {
                return true;
            }
        }, 
        JDK(false, ByteToMessageDecoder.MERGE_CUMULATOR) {
            @Override
            SSLEngineResult unwrap(final SslHandler handler, final ByteBuf in, final int len, final ByteBuf out) throws SSLException {
                final int writerIndex = out.writerIndex();
                final ByteBuffer inNioBuffer = toByteBuffer(in, in.readerIndex(), len);
                final int position = inNioBuffer.position();
                final SSLEngineResult result = handler.engine.unwrap(inNioBuffer, toByteBuffer(out, writerIndex, out.writableBytes()));
                out.writerIndex(writerIndex + result.bytesProduced());
                if (result.bytesConsumed() == 0) {
                    final int consumed = inNioBuffer.position() - position;
                    if (consumed != result.bytesConsumed()) {
                        return new SSLEngineResult(result.getStatus(), result.getHandshakeStatus(), consumed, result.bytesProduced());
                    }
                }
                return result;
            }
            
            @Override
            ByteBuf allocateWrapBuffer(final SslHandler handler, final ByteBufAllocator allocator, final int pendingBytes, final int numComponents) {
                return allocator.heapBuffer(handler.engine.getSession().getPacketBufferSize());
            }
            
            @Override
            int calculatePendingData(final SslHandler handler, final int guess) {
                return guess;
            }
            
            @Override
            boolean jdkCompatibilityMode(final SSLEngine engine) {
                return true;
            }
        };
        
        final boolean wantsDirectBuffer;
        final Cumulator cumulator;
        
        static SslEngineType forEngine(final SSLEngine engine) {
            return (engine instanceof ReferenceCountedOpenSslEngine) ? SslEngineType.TCNATIVE : ((engine instanceof ConscryptAlpnSslEngine) ? SslEngineType.CONSCRYPT : SslEngineType.JDK);
        }
        
        private SslEngineType(final boolean wantsDirectBuffer, final Cumulator cumulator) {
            this.wantsDirectBuffer = wantsDirectBuffer;
            this.cumulator = cumulator;
        }
        
        abstract SSLEngineResult unwrap(final SslHandler p0, final ByteBuf p1, final int p2, final ByteBuf p3) throws SSLException;
        
        abstract int calculatePendingData(final SslHandler p0, final int p1);
        
        abstract boolean jdkCompatibilityMode(final SSLEngine p0);
        
        abstract ByteBuf allocateWrapBuffer(final SslHandler p0, final ByteBufAllocator p1, final int p2, final int p3);
    }
    
    private final class AsyncTaskCompletionHandler implements Runnable
    {
        private final boolean inUnwrap;
        boolean didRun;
        boolean resumeLater;
        
        AsyncTaskCompletionHandler(final boolean inUnwrap) {
            this.inUnwrap = inUnwrap;
        }
        
        @Override
        public void run() {
            this.didRun = true;
            if (this.resumeLater) {
                SslHandler.this.getTaskRunner(this.inUnwrap).runComplete();
            }
        }
        
        boolean resumeLater() {
            return !this.didRun && (this.resumeLater = true);
        }
    }
    
    private final class SslTasksRunner implements Runnable
    {
        private final boolean inUnwrap;
        private final Runnable runCompleteTask;
        
        SslTasksRunner(final boolean inUnwrap) {
            this.runCompleteTask = new Runnable() {
                @Override
                public void run() {
                    SslTasksRunner.this.runComplete();
                }
            };
            this.inUnwrap = inUnwrap;
        }
        
        private void taskError(final Throwable e) {
            if (this.inUnwrap) {
                try {
                    SslHandler.this.handleUnwrapThrowable(SslHandler.this.ctx, e);
                }
                catch (final Throwable cause) {
                    this.safeExceptionCaught(cause);
                }
            }
            else {
                SslHandler.this.setHandshakeFailure(SslHandler.this.ctx, e);
                SslHandler.this.forceFlush(SslHandler.this.ctx);
            }
        }
        
        private void safeExceptionCaught(final Throwable cause) {
            try {
                SslHandler.this.exceptionCaught(SslHandler.this.ctx, this.wrapIfNeeded(cause));
            }
            catch (final Throwable error) {
                SslHandler.this.ctx.fireExceptionCaught(error);
            }
        }
        
        private Throwable wrapIfNeeded(final Throwable cause) {
            if (!this.inUnwrap) {
                return cause;
            }
            return (cause instanceof DecoderException) ? cause : new DecoderException(cause);
        }
        
        private void tryDecodeAgain() {
            try {
                SslHandler.this.channelRead(SslHandler.this.ctx, Unpooled.EMPTY_BUFFER);
            }
            catch (final Throwable cause) {
                this.safeExceptionCaught(cause);
            }
            finally {
                SslHandler.this.channelReadComplete0(SslHandler.this.ctx);
            }
        }
        
        private void resumeOnEventExecutor() {
            assert SslHandler.this.ctx.executor().inEventLoop();
            SslHandler.this.clearState(128);
            try {
                final SSLEngineResult.HandshakeStatus status = SslHandler.this.engine.getHandshakeStatus();
                switch (status) {
                    case NEED_TASK: {
                        SslHandler.this.executeDelegatedTask(this);
                        break;
                    }
                    case FINISHED:
                    case NOT_HANDSHAKING: {
                        SslHandler.this.setHandshakeSuccess();
                        try {
                            SslHandler.this.wrap(SslHandler.this.ctx, this.inUnwrap);
                        }
                        catch (final Throwable e) {
                            this.taskError(e);
                            return;
                        }
                        if (this.inUnwrap) {
                            SslHandler.this.unwrapNonAppData(SslHandler.this.ctx);
                        }
                        SslHandler.this.forceFlush(SslHandler.this.ctx);
                        this.tryDecodeAgain();
                        break;
                    }
                    case NEED_UNWRAP: {
                        try {
                            SslHandler.this.unwrapNonAppData(SslHandler.this.ctx);
                        }
                        catch (final SSLException e2) {
                            SslHandler.this.handleUnwrapThrowable(SslHandler.this.ctx, e2);
                            return;
                        }
                        this.tryDecodeAgain();
                        break;
                    }
                    case NEED_WRAP: {
                        try {
                            if (!SslHandler.this.wrapNonAppData(SslHandler.this.ctx, false) && this.inUnwrap) {
                                SslHandler.this.unwrapNonAppData(SslHandler.this.ctx);
                            }
                            SslHandler.this.forceFlush(SslHandler.this.ctx);
                        }
                        catch (final Throwable e) {
                            this.taskError(e);
                            return;
                        }
                        this.tryDecodeAgain();
                        break;
                    }
                    default: {
                        throw new AssertionError();
                    }
                }
            }
            catch (final Throwable cause) {
                this.safeExceptionCaught(cause);
            }
        }
        
        void runComplete() {
            final EventExecutor executor = SslHandler.this.ctx.executor();
            if (executor.inEventLoop()) {
                this.resumeOnEventExecutor();
            }
            else {
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        SslTasksRunner.this.resumeOnEventExecutor();
                    }
                });
            }
        }
        
        @Override
        public void run() {
            try {
                final Runnable task = SslHandler.this.engine.getDelegatedTask();
                if (task == null) {
                    return;
                }
                if (task instanceof AsyncRunnable) {
                    final AsyncRunnable asyncTask = (AsyncRunnable)task;
                    asyncTask.run(this.runCompleteTask);
                }
                else {
                    task.run();
                    this.runComplete();
                }
            }
            catch (final Throwable cause) {
                this.handleException(cause);
            }
        }
        
        private void handleException(final Throwable cause) {
            final EventExecutor executor = SslHandler.this.ctx.executor();
            if (executor.inEventLoop()) {
                SslHandler.this.clearState(128);
                this.safeExceptionCaught(cause);
            }
            else {
                try {
                    executor.execute(new Runnable() {
                        @Override
                        public void run() {
                            SslHandler.this.clearState(128);
                            SslTasksRunner.this.safeExceptionCaught(cause);
                        }
                    });
                }
                catch (final RejectedExecutionException ignore) {
                    SslHandler.this.clearState(128);
                    SslHandler.this.ctx.fireExceptionCaught(cause);
                }
            }
        }
    }
    
    private final class SslHandlerCoalescingBufferQueue extends AbstractCoalescingBufferQueue
    {
        SslHandlerCoalescingBufferQueue(final Channel channel, final int initSize) {
            super(channel, initSize);
        }
        
        @Override
        protected ByteBuf compose(final ByteBufAllocator alloc, final ByteBuf cumulation, final ByteBuf next) {
            final int wrapDataSize = SslHandler.this.wrapDataSize;
            if (cumulation instanceof CompositeByteBuf) {
                final CompositeByteBuf composite = (CompositeByteBuf)cumulation;
                final int numComponents = composite.numComponents();
                if (numComponents == 0 || !attemptCopyToCumulation(composite.internalComponent(numComponents - 1), next, wrapDataSize)) {
                    composite.addComponent(true, next);
                }
                return composite;
            }
            return attemptCopyToCumulation(cumulation, next, wrapDataSize) ? cumulation : this.copyAndCompose(alloc, cumulation, next);
        }
        
        @Override
        protected ByteBuf composeFirst(final ByteBufAllocator allocator, ByteBuf first) {
            if (first instanceof CompositeByteBuf) {
                final CompositeByteBuf composite = (CompositeByteBuf)first;
                if (SslHandler.this.engineType.wantsDirectBuffer) {
                    first = allocator.directBuffer(composite.readableBytes());
                }
                else {
                    first = allocator.heapBuffer(composite.readableBytes());
                }
                try {
                    first.writeBytes(composite);
                }
                catch (final Throwable cause) {
                    first.release();
                    PlatformDependent.throwException(cause);
                }
                composite.release();
            }
            return first;
        }
        
        @Override
        protected ByteBuf removeEmptyValue() {
            return null;
        }
    }
    
    private final class LazyChannelPromise extends DefaultPromise<Channel>
    {
        @Override
        protected EventExecutor executor() {
            if (SslHandler.this.ctx == null) {
                throw new IllegalStateException();
            }
            return SslHandler.this.ctx.executor();
        }
        
        @Override
        protected void checkDeadLock() {
            if (SslHandler.this.ctx == null) {
                return;
            }
            super.checkDeadLock();
        }
    }
}
