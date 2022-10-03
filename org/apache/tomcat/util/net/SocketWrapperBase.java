package org.apache.tomcat.util.net;

import java.nio.channels.InterruptedByTimeoutException;
import java.io.EOFException;
import org.apache.tomcat.util.ExceptionUtils;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.juli.logging.LogFactory;
import java.nio.channels.WritePendingException;
import java.nio.channels.ReadPendingException;
import java.net.SocketTimeoutException;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.TimeUnit;
import java.nio.ByteBuffer;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.Semaphore;
import java.io.IOException;
import org.apache.tomcat.util.res.StringManager;
import org.apache.juli.logging.Log;

public abstract class SocketWrapperBase<E>
{
    private static final Log log;
    protected static final StringManager sm;
    private final E socket;
    private final AbstractEndpoint<E> endpoint;
    private volatile long readTimeout;
    private volatile long writeTimeout;
    protected volatile IOException previousIOException;
    private volatile int keepAliveLeft;
    private volatile boolean upgraded;
    private boolean secure;
    private String negotiatedProtocol;
    protected String localAddr;
    protected String localName;
    protected int localPort;
    protected String remoteAddr;
    protected String remoteHost;
    protected int remotePort;
    private volatile IOException error;
    protected volatile SocketBufferHandler socketBufferHandler;
    protected int bufferedWriteSize;
    protected final WriteBuffer nonBlockingWriteBuffer;
    protected final Semaphore readPending;
    protected volatile OperationState<?> readOperation;
    protected final Semaphore writePending;
    protected volatile OperationState<?> writeOperation;
    public static final CompletionCheck COMPLETE_WRITE;
    public static final CompletionCheck COMPLETE_WRITE_WITH_COMPLETION;
    public static final CompletionCheck READ_DATA;
    public static final CompletionCheck COMPLETE_READ_WITH_COMPLETION;
    public static final CompletionCheck COMPLETE_READ;
    
    public SocketWrapperBase(final E socket, final AbstractEndpoint<E> endpoint) {
        this.readTimeout = -1L;
        this.writeTimeout = -1L;
        this.previousIOException = null;
        this.keepAliveLeft = 100;
        this.upgraded = false;
        this.secure = false;
        this.negotiatedProtocol = null;
        this.localAddr = null;
        this.localName = null;
        this.localPort = -1;
        this.remoteAddr = null;
        this.remoteHost = null;
        this.remotePort = -1;
        this.error = null;
        this.socketBufferHandler = null;
        this.bufferedWriteSize = 65536;
        this.nonBlockingWriteBuffer = new WriteBuffer(this.bufferedWriteSize);
        this.readOperation = null;
        this.writeOperation = null;
        this.socket = socket;
        this.endpoint = endpoint;
        if (endpoint.getUseAsyncIO() || this.needSemaphores()) {
            this.readPending = new Semaphore(1);
            this.writePending = new Semaphore(1);
        }
        else {
            this.readPending = null;
            this.writePending = null;
        }
    }
    
    public E getSocket() {
        return this.socket;
    }
    
    public AbstractEndpoint<E> getEndpoint() {
        return this.endpoint;
    }
    
    public void execute(final Runnable runnable) {
        final Executor executor = this.endpoint.getExecutor();
        if (!this.endpoint.isRunning() || executor == null) {
            throw new RejectedExecutionException();
        }
        executor.execute(runnable);
    }
    
    public IOException getError() {
        return this.error;
    }
    
    public void setError(final IOException error) {
        if (this.error != null) {
            return;
        }
        this.error = error;
    }
    
    public void checkError() throws IOException {
        if (this.error != null) {
            throw this.error;
        }
    }
    
    @Deprecated
    public boolean isUpgraded() {
        return this.upgraded;
    }
    
    @Deprecated
    public void setUpgraded(final boolean upgraded) {
        this.upgraded = upgraded;
    }
    
    @Deprecated
    public boolean isSecure() {
        return this.secure;
    }
    
    @Deprecated
    public void setSecure(final boolean secure) {
        this.secure = secure;
    }
    
    public String getNegotiatedProtocol() {
        return this.negotiatedProtocol;
    }
    
    public void setNegotiatedProtocol(final String negotiatedProtocol) {
        this.negotiatedProtocol = negotiatedProtocol;
    }
    
    public void setReadTimeout(final long readTimeout) {
        if (readTimeout > 0L) {
            this.readTimeout = readTimeout;
        }
        else {
            this.readTimeout = -1L;
        }
    }
    
    public long getReadTimeout() {
        return this.readTimeout;
    }
    
    public void setWriteTimeout(final long writeTimeout) {
        if (writeTimeout > 0L) {
            this.writeTimeout = writeTimeout;
        }
        else {
            this.writeTimeout = -1L;
        }
    }
    
    public long getWriteTimeout() {
        return this.writeTimeout;
    }
    
    public void setKeepAliveLeft(final int keepAliveLeft) {
        this.keepAliveLeft = keepAliveLeft;
    }
    
    public int decrementKeepAlive() {
        return --this.keepAliveLeft;
    }
    
    public String getRemoteHost() {
        if (this.remoteHost == null) {
            this.populateRemoteHost();
        }
        return this.remoteHost;
    }
    
    protected abstract void populateRemoteHost();
    
    public String getRemoteAddr() {
        if (this.remoteAddr == null) {
            this.populateRemoteAddr();
        }
        return this.remoteAddr;
    }
    
    protected abstract void populateRemoteAddr();
    
    public int getRemotePort() {
        if (this.remotePort == -1) {
            this.populateRemotePort();
        }
        return this.remotePort;
    }
    
    protected abstract void populateRemotePort();
    
    public String getLocalName() {
        if (this.localName == null) {
            this.populateLocalName();
        }
        return this.localName;
    }
    
    protected abstract void populateLocalName();
    
    public String getLocalAddr() {
        if (this.localAddr == null) {
            this.populateLocalAddr();
        }
        return this.localAddr;
    }
    
    protected abstract void populateLocalAddr();
    
    public int getLocalPort() {
        if (this.localPort == -1) {
            this.populateLocalPort();
        }
        return this.localPort;
    }
    
    protected abstract void populateLocalPort();
    
    public SocketBufferHandler getSocketBufferHandler() {
        return this.socketBufferHandler;
    }
    
    public boolean hasDataToRead() {
        return true;
    }
    
    public boolean hasDataToWrite() {
        return !this.socketBufferHandler.isWriteBufferEmpty() || !this.nonBlockingWriteBuffer.isEmpty();
    }
    
    public boolean isReadyForWrite() {
        final boolean result = this.canWrite();
        if (!result) {
            this.registerWriteInterest();
        }
        return result;
    }
    
    public boolean canWrite() {
        if (this.socketBufferHandler == null) {
            throw new IllegalStateException(SocketWrapperBase.sm.getString("socket.closed"));
        }
        return this.socketBufferHandler.isWriteBufferWritable() && this.nonBlockingWriteBuffer.isEmpty();
    }
    
    @Override
    public String toString() {
        return super.toString() + ":" + String.valueOf(this.socket);
    }
    
    public abstract int read(final boolean p0, final byte[] p1, final int p2, final int p3) throws IOException;
    
    public abstract int read(final boolean p0, final ByteBuffer p1) throws IOException;
    
    public abstract boolean isReadyForRead() throws IOException;
    
    public abstract void setAppReadBufHandler(final ApplicationBufferHandler p0);
    
    protected int populateReadBuffer(final byte[] b, final int off, final int len) {
        this.socketBufferHandler.configureReadBufferForRead();
        final ByteBuffer readBuffer = this.socketBufferHandler.getReadBuffer();
        int remaining = readBuffer.remaining();
        if (remaining > 0) {
            remaining = Math.min(remaining, len);
            readBuffer.get(b, off, remaining);
            if (SocketWrapperBase.log.isDebugEnabled()) {
                SocketWrapperBase.log.debug((Object)("Socket: [" + this + "], Read from buffer: [" + remaining + "]"));
            }
        }
        return remaining;
    }
    
    protected int populateReadBuffer(final ByteBuffer to) {
        this.socketBufferHandler.configureReadBufferForRead();
        final int nRead = transfer(this.socketBufferHandler.getReadBuffer(), to);
        if (SocketWrapperBase.log.isDebugEnabled()) {
            SocketWrapperBase.log.debug((Object)("Socket: [" + this + "], Read from buffer: [" + nRead + "]"));
        }
        return nRead;
    }
    
    public void unRead(final ByteBuffer returnedInput) {
        if (returnedInput != null) {
            this.socketBufferHandler.configureReadBufferForWrite();
            this.socketBufferHandler.getReadBuffer().put(returnedInput);
        }
    }
    
    public abstract void close() throws IOException;
    
    public abstract boolean isClosed();
    
    public final void write(final boolean block, final byte[] buf, final int off, final int len) throws IOException {
        if (len == 0 || buf == null) {
            return;
        }
        if (block) {
            this.writeBlocking(buf, off, len);
        }
        else {
            this.writeNonBlocking(buf, off, len);
        }
    }
    
    public final void write(final boolean block, final ByteBuffer from) throws IOException {
        if (from == null || from.remaining() == 0) {
            return;
        }
        if (block) {
            this.writeBlocking(from);
        }
        else {
            this.writeNonBlocking(from);
        }
    }
    
    protected void writeBlocking(final byte[] buf, int off, int len) throws IOException {
        if (len > 0) {
            this.socketBufferHandler.configureWriteBufferForWrite();
            int thisTime;
            for (thisTime = transfer(buf, off, len, this.socketBufferHandler.getWriteBuffer()), len -= thisTime; len > 0; len -= thisTime) {
                off += thisTime;
                this.doWrite(true);
                this.socketBufferHandler.configureWriteBufferForWrite();
                thisTime = transfer(buf, off, len, this.socketBufferHandler.getWriteBuffer());
            }
        }
    }
    
    protected void writeBlocking(final ByteBuffer from) throws IOException {
        if (from.hasRemaining()) {
            this.socketBufferHandler.configureWriteBufferForWrite();
            transfer(from, this.socketBufferHandler.getWriteBuffer());
            while (from.hasRemaining()) {
                this.doWrite(true);
                this.socketBufferHandler.configureWriteBufferForWrite();
                transfer(from, this.socketBufferHandler.getWriteBuffer());
            }
        }
    }
    
    protected void writeNonBlocking(final byte[] buf, int off, int len) throws IOException {
        if (len > 0 && this.nonBlockingWriteBuffer.isEmpty() && this.socketBufferHandler.isWriteBufferWritable()) {
            this.socketBufferHandler.configureWriteBufferForWrite();
            int thisTime;
            for (thisTime = transfer(buf, off, len, this.socketBufferHandler.getWriteBuffer()), len -= thisTime; len > 0; len -= thisTime) {
                off += thisTime;
                this.doWrite(false);
                if (len <= 0 || !this.socketBufferHandler.isWriteBufferWritable()) {
                    break;
                }
                this.socketBufferHandler.configureWriteBufferForWrite();
                thisTime = transfer(buf, off, len, this.socketBufferHandler.getWriteBuffer());
            }
        }
        if (len > 0) {
            this.nonBlockingWriteBuffer.add(buf, off, len);
        }
    }
    
    protected void writeNonBlocking(final ByteBuffer from) throws IOException {
        if (from.hasRemaining() && this.nonBlockingWriteBuffer.isEmpty() && this.socketBufferHandler.isWriteBufferWritable()) {
            this.writeNonBlockingInternal(from);
        }
        if (from.hasRemaining()) {
            this.nonBlockingWriteBuffer.add(from);
        }
    }
    
    protected void writeNonBlockingInternal(final ByteBuffer from) throws IOException {
        this.socketBufferHandler.configureWriteBufferForWrite();
        transfer(from, this.socketBufferHandler.getWriteBuffer());
        while (from.hasRemaining()) {
            this.doWrite(false);
            if (!this.socketBufferHandler.isWriteBufferWritable()) {
                break;
            }
            this.socketBufferHandler.configureWriteBufferForWrite();
            transfer(from, this.socketBufferHandler.getWriteBuffer());
        }
    }
    
    public boolean flush(final boolean block) throws IOException {
        boolean result = false;
        if (block) {
            this.flushBlocking();
        }
        else {
            result = this.flushNonBlocking();
        }
        return result;
    }
    
    protected void flushBlocking() throws IOException {
        this.doWrite(true);
        if (!this.nonBlockingWriteBuffer.isEmpty()) {
            this.nonBlockingWriteBuffer.write(this, true);
            if (!this.socketBufferHandler.isWriteBufferEmpty()) {
                this.doWrite(true);
            }
        }
    }
    
    protected boolean flushNonBlocking() throws IOException {
        boolean dataLeft = !this.socketBufferHandler.isWriteBufferEmpty();
        if (dataLeft) {
            this.doWrite(false);
            dataLeft = !this.socketBufferHandler.isWriteBufferEmpty();
        }
        if (!dataLeft && !this.nonBlockingWriteBuffer.isEmpty()) {
            dataLeft = this.nonBlockingWriteBuffer.write(this, false);
            if (!dataLeft && !this.socketBufferHandler.isWriteBufferEmpty()) {
                this.doWrite(false);
                dataLeft = !this.socketBufferHandler.isWriteBufferEmpty();
            }
        }
        return dataLeft;
    }
    
    protected void doWrite(final boolean block) throws IOException {
        this.socketBufferHandler.configureWriteBufferForRead();
        this.doWrite(block, this.socketBufferHandler.getWriteBuffer());
    }
    
    protected abstract void doWrite(final boolean p0, final ByteBuffer p1) throws IOException;
    
    public void processSocket(final SocketEvent socketStatus, final boolean dispatch) {
        this.endpoint.processSocket(this, socketStatus, dispatch);
    }
    
    public abstract void registerReadInterest();
    
    public abstract void registerWriteInterest();
    
    public abstract SendfileDataBase createSendfileData(final String p0, final long p1, final long p2);
    
    public abstract SendfileState processSendfile(final SendfileDataBase p0);
    
    public abstract void doClientAuth(final SSLSupport p0) throws IOException;
    
    public abstract SSLSupport getSslSupport(final String p0);
    
    public boolean hasAsyncIO() {
        return this.readPending != null;
    }
    
    public boolean needSemaphores() {
        return false;
    }
    
    public boolean hasPerOperationTimeout() {
        return false;
    }
    
    public boolean isReadPending() {
        return false;
    }
    
    public boolean isWritePending() {
        return false;
    }
    
    @Deprecated
    public boolean awaitReadComplete(final long timeout, final TimeUnit unit) {
        return true;
    }
    
    @Deprecated
    public boolean awaitWriteComplete(final long timeout, final TimeUnit unit) {
        return true;
    }
    
    public final <A> CompletionState read(final long timeout, final TimeUnit unit, final A attachment, final CompletionHandler<Long, ? super A> handler, final ByteBuffer... dsts) {
        if (dsts == null) {
            throw new IllegalArgumentException();
        }
        return this.read(dsts, 0, dsts.length, BlockingMode.CLASSIC, timeout, unit, attachment, null, handler);
    }
    
    public final <A> CompletionState read(final BlockingMode block, final long timeout, final TimeUnit unit, final A attachment, final CompletionCheck check, final CompletionHandler<Long, ? super A> handler, final ByteBuffer... dsts) {
        if (dsts == null) {
            throw new IllegalArgumentException();
        }
        return this.read(dsts, 0, dsts.length, block, timeout, unit, attachment, check, handler);
    }
    
    public final <A> CompletionState read(final ByteBuffer[] dsts, final int offset, final int length, final BlockingMode block, final long timeout, final TimeUnit unit, final A attachment, final CompletionCheck check, final CompletionHandler<Long, ? super A> handler) {
        return this.vectoredOperation(true, dsts, offset, length, block, timeout, unit, attachment, check, handler);
    }
    
    public final <A> CompletionState write(final long timeout, final TimeUnit unit, final A attachment, final CompletionHandler<Long, ? super A> handler, final ByteBuffer... srcs) {
        if (srcs == null) {
            throw new IllegalArgumentException();
        }
        return this.write(srcs, 0, srcs.length, BlockingMode.CLASSIC, timeout, unit, attachment, null, handler);
    }
    
    public final <A> CompletionState write(final BlockingMode block, final long timeout, final TimeUnit unit, final A attachment, final CompletionCheck check, final CompletionHandler<Long, ? super A> handler, final ByteBuffer... srcs) {
        if (srcs == null) {
            throw new IllegalArgumentException();
        }
        return this.write(srcs, 0, srcs.length, block, timeout, unit, attachment, check, handler);
    }
    
    public final <A> CompletionState write(final ByteBuffer[] srcs, final int offset, final int length, final BlockingMode block, final long timeout, final TimeUnit unit, final A attachment, final CompletionCheck check, final CompletionHandler<Long, ? super A> handler) {
        return this.vectoredOperation(false, srcs, offset, length, block, timeout, unit, attachment, check, handler);
    }
    
    protected final <A> CompletionState vectoredOperation(final boolean read, final ByteBuffer[] buffers, final int offset, final int length, final BlockingMode block, long timeout, TimeUnit unit, final A attachment, final CompletionCheck check, final CompletionHandler<Long, ? super A> handler) {
        final IOException ioe = this.getError();
        if (ioe != null) {
            handler.failed(ioe, attachment);
            return CompletionState.ERROR;
        }
        if (timeout == -1L) {
            timeout = AbstractEndpoint.toTimeout(read ? this.getReadTimeout() : this.getWriteTimeout());
            unit = TimeUnit.MILLISECONDS;
        }
        else if (!this.hasPerOperationTimeout() && unit.toMillis(timeout) != (read ? this.getReadTimeout() : this.getWriteTimeout())) {
            if (read) {
                this.setReadTimeout(unit.toMillis(timeout));
            }
            else {
                this.setWriteTimeout(unit.toMillis(timeout));
            }
        }
        Label_0289: {
            Label_0216: {
                if (block != BlockingMode.BLOCK) {
                    if (block != BlockingMode.SEMI_BLOCK) {
                        break Label_0216;
                    }
                }
                try {
                    Label_0196: {
                        if (read) {
                            if (this.readPending.tryAcquire(timeout, unit)) {
                                break Label_0196;
                            }
                        }
                        else if (this.writePending.tryAcquire(timeout, unit)) {
                            break Label_0196;
                        }
                        handler.failed(new SocketTimeoutException(), attachment);
                        return CompletionState.ERROR;
                    }
                    break Label_0289;
                }
                catch (final InterruptedException e) {
                    handler.failed(e, attachment);
                    return CompletionState.ERROR;
                }
            }
            if (read) {
                if (this.readPending.tryAcquire()) {
                    break Label_0289;
                }
            }
            else if (this.writePending.tryAcquire()) {
                break Label_0289;
            }
            if (block == BlockingMode.NON_BLOCK) {
                return CompletionState.NOT_DONE;
            }
            handler.failed(read ? new ReadPendingException() : new WritePendingException(), attachment);
            return CompletionState.ERROR;
        }
        final VectoredIOCompletionHandler<A> completion = new VectoredIOCompletionHandler<A>();
        final OperationState<A> state = this.newOperationState(read, buffers, offset, length, block, timeout, unit, attachment, check, handler, read ? this.readPending : this.writePending, completion);
        if (read) {
            this.readOperation = state;
        }
        else {
            this.writeOperation = state;
        }
        state.start();
        if (block == BlockingMode.BLOCK) {
            synchronized (state) {
                if (state.state == CompletionState.PENDING) {
                    try {
                        state.wait(unit.toMillis(timeout));
                        if (state.state == CompletionState.PENDING) {
                            if (handler != null && state.callHandler.compareAndSet(true, false)) {
                                handler.failed(new SocketTimeoutException(this.getTimeoutMsg(read)), attachment);
                            }
                            return CompletionState.ERROR;
                        }
                    }
                    catch (final InterruptedException e2) {
                        if (handler != null && state.callHandler.compareAndSet(true, false)) {
                            handler.failed(new SocketTimeoutException(this.getTimeoutMsg(read)), attachment);
                        }
                        return CompletionState.ERROR;
                    }
                }
            }
        }
        return state.state;
    }
    
    private String getTimeoutMsg(final boolean read) {
        if (read) {
            return SocketWrapperBase.sm.getString("socketWrapper.readTimeout");
        }
        return SocketWrapperBase.sm.getString("socketWrapper.writeTimeout");
    }
    
    protected abstract <A> OperationState<A> newOperationState(final boolean p0, final ByteBuffer[] p1, final int p2, final int p3, final BlockingMode p4, final long p5, final TimeUnit p6, final A p7, final CompletionCheck p8, final CompletionHandler<Long, ? super A> p9, final Semaphore p10, final VectoredIOCompletionHandler<A> p11);
    
    protected static int transfer(final byte[] from, final int offset, final int length, final ByteBuffer to) {
        final int max = Math.min(length, to.remaining());
        if (max > 0) {
            to.put(from, offset, max);
        }
        return max;
    }
    
    protected static int transfer(final ByteBuffer from, final ByteBuffer to) {
        final int max = Math.min(from.remaining(), to.remaining());
        if (max > 0) {
            final int fromLimit = from.limit();
            from.limit(from.position() + max);
            to.put(from);
            from.limit(fromLimit);
        }
        return max;
    }
    
    protected static boolean buffersArrayHasRemaining(final ByteBuffer[] buffers, final int offset, final int length) {
        for (int pos = offset; pos < offset + length; ++pos) {
            if (buffers[pos].hasRemaining()) {
                return true;
            }
        }
        return false;
    }
    
    static {
        log = LogFactory.getLog((Class)SocketWrapperBase.class);
        sm = StringManager.getManager((Class)SocketWrapperBase.class);
        COMPLETE_WRITE = new CompletionCheck() {
            @Override
            public CompletionHandlerCall callHandler(final CompletionState state, final ByteBuffer[] buffers, final int offset, final int length) {
                for (int i = 0; i < length; ++i) {
                    if (buffers[offset + i].hasRemaining()) {
                        return CompletionHandlerCall.CONTINUE;
                    }
                }
                return (state == CompletionState.DONE) ? CompletionHandlerCall.DONE : CompletionHandlerCall.NONE;
            }
        };
        COMPLETE_WRITE_WITH_COMPLETION = new CompletionCheck() {
            @Override
            public CompletionHandlerCall callHandler(final CompletionState state, final ByteBuffer[] buffers, final int offset, final int length) {
                for (int i = 0; i < length; ++i) {
                    if (buffers[offset + i].hasRemaining()) {
                        return CompletionHandlerCall.CONTINUE;
                    }
                }
                return CompletionHandlerCall.DONE;
            }
        };
        READ_DATA = new CompletionCheck() {
            @Override
            public CompletionHandlerCall callHandler(final CompletionState state, final ByteBuffer[] buffers, final int offset, final int length) {
                return (state == CompletionState.DONE) ? CompletionHandlerCall.DONE : CompletionHandlerCall.NONE;
            }
        };
        COMPLETE_READ_WITH_COMPLETION = SocketWrapperBase.COMPLETE_WRITE_WITH_COMPLETION;
        COMPLETE_READ = SocketWrapperBase.COMPLETE_WRITE;
    }
    
    public enum BlockingMode
    {
        CLASSIC, 
        NON_BLOCK, 
        SEMI_BLOCK, 
        BLOCK;
    }
    
    public enum CompletionState
    {
        PENDING, 
        NOT_DONE, 
        INLINE, 
        ERROR, 
        DONE;
    }
    
    public enum CompletionHandlerCall
    {
        CONTINUE, 
        NONE, 
        DONE;
    }
    
    protected abstract class OperationState<A> implements Runnable
    {
        protected final boolean read;
        protected final ByteBuffer[] buffers;
        protected final int offset;
        protected final int length;
        protected final A attachment;
        protected final long timeout;
        protected final TimeUnit unit;
        protected final BlockingMode block;
        protected final CompletionCheck check;
        protected final CompletionHandler<Long, ? super A> handler;
        protected final Semaphore semaphore;
        protected final VectoredIOCompletionHandler<A> completion;
        protected final AtomicBoolean callHandler;
        protected volatile long nBytes;
        protected volatile CompletionState state;
        protected boolean completionDone;
        
        protected OperationState(final boolean read, final ByteBuffer[] buffers, final int offset, final int length, final BlockingMode block, final long timeout, final TimeUnit unit, final A attachment, final CompletionCheck check, final CompletionHandler<Long, ? super A> handler, final Semaphore semaphore, final VectoredIOCompletionHandler<A> completion) {
            this.nBytes = 0L;
            this.state = CompletionState.PENDING;
            this.completionDone = true;
            this.read = read;
            this.buffers = buffers;
            this.offset = offset;
            this.length = length;
            this.block = block;
            this.timeout = timeout;
            this.unit = unit;
            this.attachment = attachment;
            this.check = check;
            this.handler = handler;
            this.semaphore = semaphore;
            this.completion = completion;
            this.callHandler = ((handler != null) ? new AtomicBoolean(true) : null);
        }
        
        protected abstract boolean isInline();
        
        protected boolean process() {
            try {
                SocketWrapperBase.this.getEndpoint().getExecutor().execute(this);
                return true;
            }
            catch (final RejectedExecutionException ree) {
                SocketWrapperBase.log.warn((Object)SocketWrapperBase.sm.getString("endpoint.executor.fail", new Object[] { SocketWrapperBase.this }), (Throwable)ree);
            }
            catch (final Throwable t) {
                ExceptionUtils.handleThrowable(t);
                SocketWrapperBase.log.error((Object)SocketWrapperBase.sm.getString("endpoint.process.fail"), t);
            }
            return false;
        }
        
        protected void start() {
            this.run();
        }
        
        protected void end() {
        }
    }
    
    protected class VectoredIOCompletionHandler<A> implements CompletionHandler<Long, OperationState<A>>
    {
        @Override
        public void completed(final Long nBytes, final OperationState<A> state) {
            if (nBytes < 0L) {
                this.failed((Throwable)new EOFException(), state);
            }
            else {
                state.nBytes += nBytes;
                final CompletionState currentState = state.isInline() ? CompletionState.INLINE : CompletionState.DONE;
                boolean complete = true;
                boolean completion = true;
                if (state.check != null) {
                    final CompletionHandlerCall call = state.check.callHandler(currentState, state.buffers, state.offset, state.length);
                    if (call == CompletionHandlerCall.CONTINUE) {
                        complete = false;
                    }
                    else if (call == CompletionHandlerCall.NONE) {
                        completion = false;
                    }
                }
                if (complete) {
                    boolean notify = false;
                    if (state.read) {
                        SocketWrapperBase.this.readOperation = null;
                    }
                    else {
                        SocketWrapperBase.this.writeOperation = null;
                    }
                    state.semaphore.release();
                    if (state.block == BlockingMode.BLOCK && currentState != CompletionState.INLINE) {
                        notify = true;
                    }
                    else {
                        state.state = currentState;
                    }
                    state.end();
                    if (completion && state.handler != null && state.callHandler.compareAndSet(true, false)) {
                        state.handler.completed(state.nBytes, state.attachment);
                    }
                    synchronized (state) {
                        state.completionDone = true;
                        if (notify) {
                            state.state = currentState;
                            state.notify();
                        }
                    }
                }
                else {
                    synchronized (state) {
                        state.completionDone = true;
                    }
                    state.run();
                }
            }
        }
        
        @Override
        public void failed(Throwable exc, final OperationState<A> state) {
            IOException ioe = null;
            if (exc instanceof InterruptedByTimeoutException) {
                ioe = (IOException)(exc = new SocketTimeoutException());
            }
            else if (exc instanceof IOException) {
                ioe = (IOException)exc;
            }
            SocketWrapperBase.this.setError(ioe);
            boolean notify = false;
            if (state.read) {
                SocketWrapperBase.this.readOperation = null;
            }
            else {
                SocketWrapperBase.this.writeOperation = null;
            }
            state.semaphore.release();
            if (state.block == BlockingMode.BLOCK) {
                notify = true;
            }
            else {
                state.state = (state.isInline() ? CompletionState.ERROR : CompletionState.DONE);
            }
            state.end();
            if (state.handler != null && state.callHandler.compareAndSet(true, false)) {
                state.handler.failed(exc, state.attachment);
            }
            synchronized (state) {
                state.completionDone = true;
                if (notify) {
                    state.state = (state.isInline() ? CompletionState.ERROR : CompletionState.DONE);
                    state.notify();
                }
            }
        }
    }
    
    public interface CompletionCheck
    {
        CompletionHandlerCall callHandler(final CompletionState p0, final ByteBuffer[] p1, final int p2, final int p3);
    }
}
