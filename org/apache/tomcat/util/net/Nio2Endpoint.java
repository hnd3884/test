package org.apache.tomcat.util.net;

import javax.net.ssl.SSLEngine;
import org.apache.tomcat.util.net.jsse.JSSESupport;
import java.nio.file.Path;
import java.nio.channels.FileChannel;
import java.nio.file.StandardOpenOption;
import java.nio.file.OpenOption;
import java.io.File;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;
import java.net.SocketTimeoutException;
import java.util.concurrent.ExecutionException;
import java.nio.channels.AsynchronousCloseException;
import java.io.EOFException;
import java.util.concurrent.Semaphore;
import java.nio.ByteBuffer;
import java.nio.channels.CompletionHandler;
import org.apache.juli.logging.LogFactory;
import java.nio.channels.NetworkChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.TimeUnit;
import java.io.IOException;
import java.util.Iterator;
import org.apache.tomcat.util.ExceptionUtils;
import java.net.SocketAddress;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import org.apache.tomcat.util.collections.SynchronizedStack;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import org.apache.juli.logging.Log;

public class Nio2Endpoint extends AbstractJsseEndpoint<Nio2Channel>
{
    private static final Log log;
    private volatile AsynchronousServerSocketChannel serverSock;
    private static ThreadLocal<Boolean> inlineCompletion;
    private AsynchronousChannelGroup threadGroup;
    private volatile boolean allClosed;
    private SynchronizedStack<Nio2Channel> nioChannels;
    
    public Nio2Endpoint() {
        this.serverSock = null;
        this.threadGroup = null;
        this.setMaxConnections(-1);
    }
    
    public void setSocketProperties(final SocketProperties socketProperties) {
        this.socketProperties = socketProperties;
    }
    
    public boolean getDeferAccept() {
        return false;
    }
    
    public int getKeepAliveCount() {
        return -1;
    }
    
    @Override
    public void bind() throws Exception {
        if (this.getExecutor() == null) {
            this.createExecutor();
        }
        if (this.getExecutor() instanceof ExecutorService) {
            this.threadGroup = AsynchronousChannelGroup.withThreadPool((ExecutorService)this.getExecutor());
        }
        if (!this.internalExecutor) {
            Nio2Endpoint.log.warn((Object)Nio2Endpoint.sm.getString("endpoint.nio2.exclusiveExecutor"));
        }
        this.serverSock = AsynchronousServerSocketChannel.open(this.threadGroup);
        this.socketProperties.setProperties(this.serverSock);
        final InetSocketAddress addr = (this.getAddress() != null) ? new InetSocketAddress(this.getAddress(), this.getPort()) : new InetSocketAddress(this.getPort());
        this.serverSock.bind(addr, this.getAcceptCount());
        if (this.acceptorThreadCount != 1) {
            this.acceptorThreadCount = 1;
        }
        this.initialiseSsl();
    }
    
    @Override
    public void startInternal() throws Exception {
        if (!this.running) {
            this.allClosed = false;
            this.running = true;
            this.paused = false;
            this.processorCache = (SynchronizedStack<SocketProcessorBase<S>>)new SynchronizedStack(128, this.socketProperties.getProcessorCache());
            this.nioChannels = (SynchronizedStack<Nio2Channel>)new SynchronizedStack(128, this.socketProperties.getBufferPool());
            if (this.getExecutor() == null) {
                this.createExecutor();
            }
            this.initializeConnectionLatch();
            this.startAcceptorThreads();
        }
    }
    
    @Override
    public void stopInternal() {
        this.releaseConnectionLatch();
        if (!this.paused) {
            this.pause();
        }
        if (this.running) {
            this.running = false;
            this.unlockAccept();
            this.getExecutor().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        for (final Nio2Channel channel : Nio2Endpoint.this.getHandler().getOpenSockets()) {
                            channel.getSocket().close();
                        }
                    }
                    catch (final Throwable t) {
                        ExceptionUtils.handleThrowable(t);
                    }
                    finally {
                        Nio2Endpoint.this.allClosed = true;
                    }
                }
            });
            this.nioChannels.clear();
            this.processorCache.clear();
        }
    }
    
    @Override
    public void unbind() throws Exception {
        if (this.running) {
            this.stop();
        }
        this.doCloseServerSocket();
        this.destroySsl();
        super.unbind();
        this.shutdownExecutor();
        if (this.getHandler() != null) {
            this.getHandler().recycle();
        }
    }
    
    @Override
    protected void doCloseServerSocket() throws IOException {
        if (this.serverSock != null) {
            this.serverSock.close();
            this.serverSock = null;
        }
    }
    
    @Override
    public void shutdownExecutor() {
        if (this.threadGroup != null && this.internalExecutor) {
            try {
                long timeout = this.getExecutorTerminationTimeoutMillis();
                while (timeout > 0L && !this.allClosed) {
                    timeout -= 100L;
                    Thread.sleep(100L);
                }
                this.threadGroup.shutdownNow();
                if (timeout > 0L) {
                    this.threadGroup.awaitTermination(timeout, TimeUnit.MILLISECONDS);
                }
            }
            catch (final IOException e) {
                this.getLog().warn((Object)Nio2Endpoint.sm.getString("endpoint.warn.executorShutdown", new Object[] { this.getName() }), (Throwable)e);
            }
            catch (final InterruptedException ex) {}
            if (!this.threadGroup.isTerminated()) {
                this.getLog().warn((Object)Nio2Endpoint.sm.getString("endpoint.warn.executorShutdown", new Object[] { this.getName() }));
            }
            this.threadGroup = null;
        }
        super.shutdownExecutor();
    }
    
    public int getWriteBufSize() {
        return this.socketProperties.getTxBufSize();
    }
    
    public int getReadBufSize() {
        return this.socketProperties.getRxBufSize();
    }
    
    @Override
    protected AbstractEndpoint.Acceptor createAcceptor() {
        return new Acceptor();
    }
    
    protected boolean setSocketOptions(final AsynchronousSocketChannel socket) {
        try {
            this.socketProperties.setProperties(socket);
            Nio2Channel channel = (Nio2Channel)this.nioChannels.pop();
            if (channel == null) {
                final SocketBufferHandler bufhandler = new SocketBufferHandler(this.socketProperties.getAppReadBufSize(), this.socketProperties.getAppWriteBufSize(), this.socketProperties.getDirectBuffer());
                if (this.isSSLEnabled()) {
                    channel = new SecureNio2Channel(bufhandler, this);
                }
                else {
                    channel = new Nio2Channel(bufhandler);
                }
            }
            final Nio2SocketWrapper socketWrapper = new Nio2SocketWrapper(channel, this);
            channel.reset(socket, socketWrapper);
            socketWrapper.setReadTimeout(this.getSocketProperties().getSoTimeout());
            socketWrapper.setWriteTimeout(this.getSocketProperties().getSoTimeout());
            socketWrapper.setKeepAliveLeft(this.getMaxKeepAliveRequests());
            socketWrapper.setReadTimeout(this.getConnectionTimeout());
            socketWrapper.setWriteTimeout(this.getConnectionTimeout());
            return this.processSocket(socketWrapper, SocketEvent.OPEN_READ, true);
        }
        catch (final Throwable t) {
            ExceptionUtils.handleThrowable(t);
            Nio2Endpoint.log.error((Object)"", t);
            return false;
        }
    }
    
    @Override
    protected SocketProcessorBase<Nio2Channel> createSocketProcessor(final SocketWrapperBase<Nio2Channel> socketWrapper, final SocketEvent event) {
        return new SocketProcessor(socketWrapper, event);
    }
    
    @Override
    protected Log getLog() {
        return Nio2Endpoint.log;
    }
    
    @Override
    protected NetworkChannel getServerSocket() {
        return this.serverSock;
    }
    
    public static void startInline() {
        Nio2Endpoint.inlineCompletion.set(Boolean.TRUE);
    }
    
    public static void endInline() {
        Nio2Endpoint.inlineCompletion.set(Boolean.FALSE);
    }
    
    public static boolean isInline() {
        final Boolean flag = Nio2Endpoint.inlineCompletion.get();
        return flag != null && flag;
    }
    
    static {
        log = LogFactory.getLog((Class)Nio2Endpoint.class);
        Nio2Endpoint.inlineCompletion = new ThreadLocal<Boolean>();
    }
    
    protected class Acceptor extends AbstractEndpoint.Acceptor
    {
        @Override
        public void run() {
            int errorDelay = 0;
            while (Nio2Endpoint.this.running) {
                while (Nio2Endpoint.this.paused && Nio2Endpoint.this.running) {
                    this.state = AcceptorState.PAUSED;
                    try {
                        Thread.sleep(50L);
                    }
                    catch (final InterruptedException ex) {}
                }
                if (!Nio2Endpoint.this.running) {
                    break;
                }
                this.state = AcceptorState.RUNNING;
                try {
                    Nio2Endpoint.this.countUpOrAwaitConnection();
                    AsynchronousSocketChannel socket = null;
                    try {
                        socket = Nio2Endpoint.this.serverSock.accept().get();
                    }
                    catch (final Exception e) {
                        Nio2Endpoint.this.countDownConnection();
                        if (Nio2Endpoint.this.running) {
                            errorDelay = Nio2Endpoint.this.handleExceptionWithDelay(errorDelay);
                            throw e;
                        }
                        break;
                    }
                    errorDelay = 0;
                    if (Nio2Endpoint.this.running && !Nio2Endpoint.this.paused) {
                        if (Nio2Endpoint.this.setSocketOptions(socket)) {
                            continue;
                        }
                        this.closeSocket(socket);
                    }
                    else {
                        this.closeSocket(socket);
                    }
                }
                catch (final Throwable t) {
                    ExceptionUtils.handleThrowable(t);
                    Nio2Endpoint.log.error((Object)AbstractEndpoint.sm.getString("endpoint.accept.fail"), t);
                }
            }
            this.state = AcceptorState.ENDED;
        }
        
        private void closeSocket(final AsynchronousSocketChannel socket) {
            Nio2Endpoint.this.countDownConnection();
            try {
                socket.close();
            }
            catch (final IOException ioe) {
                if (Nio2Endpoint.log.isDebugEnabled()) {
                    Nio2Endpoint.log.debug((Object)AbstractEndpoint.sm.getString("endpoint.err.close"), (Throwable)ioe);
                }
            }
        }
    }
    
    public static class Nio2SocketWrapper extends SocketWrapperBase<Nio2Channel>
    {
        private SendfileData sendfileData;
        private final CompletionHandler<Integer, ByteBuffer> readCompletionHandler;
        private final Semaphore readPending;
        private boolean readInterest;
        private boolean readNotify;
        private final CompletionHandler<Integer, ByteBuffer> writeCompletionHandler;
        private final CompletionHandler<Long, ByteBuffer[]> gatheringWriteCompletionHandler;
        private final Semaphore writePending;
        private boolean writeInterest;
        private boolean writeNotify;
        private volatile boolean closed;
        private CompletionHandler<Integer, SendfileData> sendfileHandler;
        
        public Nio2SocketWrapper(final Nio2Channel channel, final Nio2Endpoint endpoint) {
            super(channel, endpoint);
            this.sendfileData = null;
            this.readPending = new Semaphore(1);
            this.readInterest = false;
            this.readNotify = false;
            this.writePending = new Semaphore(1);
            this.writeInterest = false;
            this.writeNotify = false;
            this.closed = false;
            this.sendfileHandler = new CompletionHandler<Integer, SendfileData>() {
                @Override
                public void completed(final Integer nWrite, final SendfileData attachment) {
                    if (nWrite < 0) {
                        this.failed((Throwable)new EOFException(), attachment);
                        return;
                    }
                    attachment.pos += nWrite;
                    final ByteBuffer buffer = Nio2SocketWrapper.this.getSocket().getBufHandler().getWriteBuffer();
                    if (!buffer.hasRemaining()) {
                        if (attachment.length <= 0L) {
                            Nio2SocketWrapper.this.setSendfileData(null);
                            try {
                                attachment.fchannel.close();
                            }
                            catch (final IOException ex) {}
                            if (Nio2Endpoint.isInline()) {
                                attachment.doneInline = true;
                            }
                            else {
                                switch (attachment.keepAliveState) {
                                    case NONE: {
                                        Nio2SocketWrapper.this.getEndpoint().processSocket(Nio2SocketWrapper.this, SocketEvent.DISCONNECT, false);
                                        break;
                                    }
                                    case PIPELINED: {
                                        Nio2SocketWrapper.this.getEndpoint().processSocket(Nio2SocketWrapper.this, SocketEvent.OPEN_READ, true);
                                        break;
                                    }
                                    case OPEN: {
                                        Nio2SocketWrapper.this.registerReadInterest();
                                        break;
                                    }
                                }
                            }
                            return;
                        }
                        Nio2SocketWrapper.this.getSocket().getBufHandler().configureWriteBufferForWrite();
                        int nRead = -1;
                        try {
                            nRead = attachment.fchannel.read(buffer);
                        }
                        catch (final IOException e) {
                            this.failed((Throwable)e, attachment);
                            return;
                        }
                        if (nRead <= 0) {
                            this.failed((Throwable)new EOFException(), attachment);
                            return;
                        }
                        Nio2SocketWrapper.this.getSocket().getBufHandler().configureWriteBufferForRead();
                        if (attachment.length < buffer.remaining()) {
                            buffer.limit(buffer.limit() - buffer.remaining() + (int)attachment.length);
                        }
                        attachment.length -= nRead;
                    }
                    Nio2SocketWrapper.this.getSocket().write(buffer, AbstractEndpoint.toTimeout(Nio2SocketWrapper.this.getWriteTimeout()), TimeUnit.MILLISECONDS, attachment, this);
                }
                
                @Override
                public void failed(final Throwable exc, final SendfileData attachment) {
                    try {
                        attachment.fchannel.close();
                    }
                    catch (final IOException ex) {}
                    if (!Nio2Endpoint.isInline()) {
                        Nio2SocketWrapper.this.getEndpoint().processSocket(Nio2SocketWrapper.this, SocketEvent.ERROR, false);
                    }
                    else {
                        attachment.doneInline = true;
                        attachment.error = true;
                    }
                }
            };
            this.socketBufferHandler = channel.getBufHandler();
            this.readCompletionHandler = new CompletionHandler<Integer, ByteBuffer>() {
                @Override
                public void completed(final Integer nBytes, final ByteBuffer attachment) {
                    if (Nio2Endpoint.log.isDebugEnabled()) {
                        Nio2Endpoint.log.debug((Object)("Socket: [" + Nio2SocketWrapper.this + "], Interest: [" + Nio2SocketWrapper.this.readInterest + "]"));
                    }
                    Nio2SocketWrapper.this.readNotify = false;
                    synchronized (Nio2SocketWrapper.this.readCompletionHandler) {
                        if (nBytes < 0) {
                            this.failed((Throwable)new EOFException(), attachment);
                        }
                        else {
                            if (Nio2SocketWrapper.this.readInterest && !Nio2Endpoint.isInline()) {
                                Nio2SocketWrapper.this.readNotify = true;
                            }
                            else {
                                Nio2SocketWrapper.this.readPending.release();
                            }
                            Nio2SocketWrapper.this.readInterest = false;
                        }
                    }
                    if (Nio2SocketWrapper.this.readNotify) {
                        Nio2SocketWrapper.this.getEndpoint().processSocket(Nio2SocketWrapper.this, SocketEvent.OPEN_READ, false);
                    }
                }
                
                @Override
                public void failed(final Throwable exc, final ByteBuffer attachment) {
                    IOException ioe;
                    if (exc instanceof IOException) {
                        ioe = (IOException)exc;
                    }
                    else {
                        ioe = new IOException(exc);
                    }
                    Nio2SocketWrapper.this.setError(ioe);
                    if (exc instanceof AsynchronousCloseException) {
                        Nio2SocketWrapper.this.readPending.release();
                        return;
                    }
                    Nio2SocketWrapper.this.getEndpoint().processSocket(Nio2SocketWrapper.this, SocketEvent.ERROR, true);
                }
            };
            this.writeCompletionHandler = new CompletionHandler<Integer, ByteBuffer>() {
                @Override
                public void completed(final Integer nBytes, final ByteBuffer attachment) {
                    Nio2SocketWrapper.this.writeNotify = false;
                    boolean notify = false;
                    synchronized (Nio2SocketWrapper.this.writeCompletionHandler) {
                        if (nBytes < 0) {
                            this.failed((Throwable)new EOFException(SocketWrapperBase.sm.getString("iob.failedwrite")), attachment);
                        }
                        else if (!Nio2SocketWrapper.this.nonBlockingWriteBuffer.isEmpty()) {
                            final ByteBuffer[] array = Nio2SocketWrapper.this.nonBlockingWriteBuffer.toArray(attachment);
                            Nio2SocketWrapper.this.getSocket().write(array, 0, array.length, AbstractEndpoint.toTimeout(Nio2SocketWrapper.this.getWriteTimeout()), TimeUnit.MILLISECONDS, array, Nio2SocketWrapper.this.gatheringWriteCompletionHandler);
                        }
                        else if (attachment.hasRemaining()) {
                            Nio2SocketWrapper.this.getSocket().write(attachment, AbstractEndpoint.toTimeout(Nio2SocketWrapper.this.getWriteTimeout()), TimeUnit.MILLISECONDS, attachment, Nio2SocketWrapper.this.writeCompletionHandler);
                        }
                        else {
                            if (Nio2SocketWrapper.this.writeInterest && !Nio2Endpoint.isInline()) {
                                Nio2SocketWrapper.this.writeNotify = true;
                                notify = true;
                            }
                            else {
                                Nio2SocketWrapper.this.writePending.release();
                            }
                            Nio2SocketWrapper.this.writeInterest = false;
                        }
                    }
                    if (notify) {
                        endpoint.processSocket(Nio2SocketWrapper.this, SocketEvent.OPEN_WRITE, true);
                    }
                }
                
                @Override
                public void failed(final Throwable exc, final ByteBuffer attachment) {
                    IOException ioe;
                    if (exc instanceof IOException) {
                        ioe = (IOException)exc;
                    }
                    else {
                        ioe = new IOException(exc);
                    }
                    Nio2SocketWrapper.this.setError(ioe);
                    Nio2SocketWrapper.this.writePending.release();
                    endpoint.processSocket(Nio2SocketWrapper.this, SocketEvent.ERROR, true);
                }
            };
            this.gatheringWriteCompletionHandler = new CompletionHandler<Long, ByteBuffer[]>() {
                @Override
                public void completed(final Long nBytes, final ByteBuffer[] attachment) {
                    Nio2SocketWrapper.this.writeNotify = false;
                    boolean notify = false;
                    synchronized (Nio2SocketWrapper.this.writeCompletionHandler) {
                        if (nBytes < 0L) {
                            this.failed((Throwable)new EOFException(SocketWrapperBase.sm.getString("iob.failedwrite")), attachment);
                        }
                        else if (!Nio2SocketWrapper.this.nonBlockingWriteBuffer.isEmpty() || SocketWrapperBase.buffersArrayHasRemaining(attachment, 0, attachment.length)) {
                            final ByteBuffer[] array = Nio2SocketWrapper.this.nonBlockingWriteBuffer.toArray(attachment);
                            Nio2SocketWrapper.this.getSocket().write(array, 0, array.length, AbstractEndpoint.toTimeout(Nio2SocketWrapper.this.getWriteTimeout()), TimeUnit.MILLISECONDS, array, Nio2SocketWrapper.this.gatheringWriteCompletionHandler);
                        }
                        else {
                            if (Nio2SocketWrapper.this.writeInterest && !Nio2Endpoint.isInline()) {
                                Nio2SocketWrapper.this.writeNotify = true;
                                notify = true;
                            }
                            else {
                                Nio2SocketWrapper.this.writePending.release();
                            }
                            Nio2SocketWrapper.this.writeInterest = false;
                        }
                    }
                    if (notify) {
                        endpoint.processSocket(Nio2SocketWrapper.this, SocketEvent.OPEN_WRITE, true);
                    }
                }
                
                @Override
                public void failed(final Throwable exc, final ByteBuffer[] attachment) {
                    IOException ioe;
                    if (exc instanceof IOException) {
                        ioe = (IOException)exc;
                    }
                    else {
                        ioe = new IOException(exc);
                    }
                    Nio2SocketWrapper.this.setError(ioe);
                    Nio2SocketWrapper.this.writePending.release();
                    endpoint.processSocket(Nio2SocketWrapper.this, SocketEvent.ERROR, true);
                }
            };
        }
        
        public void setSendfileData(final SendfileData sf) {
            this.sendfileData = sf;
        }
        
        public SendfileData getSendfileData() {
            return this.sendfileData;
        }
        
        @Override
        public boolean isReadyForRead() throws IOException {
            synchronized (this.readCompletionHandler) {
                if (this.readNotify) {
                    return true;
                }
                if (!this.readPending.tryAcquire()) {
                    this.readInterest = true;
                    return false;
                }
                if (!this.socketBufferHandler.isReadBufferEmpty()) {
                    this.readPending.release();
                    return true;
                }
                final boolean isReady = this.fillReadBuffer(false) > 0;
                if (!isReady) {
                    this.readInterest = true;
                }
                return isReady;
            }
        }
        
        @Override
        public boolean isReadyForWrite() {
            synchronized (this.writeCompletionHandler) {
                if (this.writeNotify) {
                    return true;
                }
                if (!this.writePending.tryAcquire()) {
                    this.writeInterest = true;
                    return false;
                }
                if (this.socketBufferHandler.isWriteBufferEmpty() && this.nonBlockingWriteBuffer.isEmpty()) {
                    this.writePending.release();
                    return true;
                }
                final boolean isReady = !this.flushNonBlockingInternal(true);
                if (!isReady) {
                    this.writeInterest = true;
                }
                return isReady;
            }
        }
        
        @Override
        public int read(final boolean block, final byte[] b, final int off, final int len) throws IOException {
            this.checkError();
            if (Nio2Endpoint.log.isDebugEnabled()) {
                Nio2Endpoint.log.debug((Object)("Socket: [" + this + "], block: [" + block + "], length: [" + len + "]"));
            }
            if (this.socketBufferHandler == null) {
                throw new IOException(Nio2SocketWrapper.sm.getString("socket.closed"));
            }
            Label_0177: {
                if (!this.readNotify) {
                    if (block) {
                        try {
                            this.readPending.acquire();
                            break Label_0177;
                        }
                        catch (final InterruptedException e) {
                            throw new IOException(e);
                        }
                    }
                    if (!this.readPending.tryAcquire()) {
                        if (Nio2Endpoint.log.isDebugEnabled()) {
                            Nio2Endpoint.log.debug((Object)("Socket: [" + this + "], Read in progress. Returning [0]"));
                        }
                        return 0;
                    }
                }
            }
            int nRead = this.populateReadBuffer(b, off, len);
            if (nRead > 0) {
                this.readNotify = false;
                this.readPending.release();
                return nRead;
            }
            synchronized (this.readCompletionHandler) {
                nRead = this.fillReadBuffer(block);
                if (nRead > 0) {
                    this.socketBufferHandler.configureReadBufferForRead();
                    nRead = Math.min(nRead, len);
                    this.socketBufferHandler.getReadBuffer().get(b, off, nRead);
                }
                else if (nRead == 0 && !block) {
                    this.readInterest = true;
                }
                if (Nio2Endpoint.log.isDebugEnabled()) {
                    Nio2Endpoint.log.debug((Object)("Socket: [" + this + "], Read: [" + nRead + "]"));
                }
                return nRead;
            }
        }
        
        @Override
        public int read(final boolean block, final ByteBuffer to) throws IOException {
            this.checkError();
            if (this.socketBufferHandler == null) {
                throw new IOException(Nio2SocketWrapper.sm.getString("socket.closed"));
            }
            Label_0113: {
                if (!this.readNotify) {
                    if (block) {
                        try {
                            this.readPending.acquire();
                            break Label_0113;
                        }
                        catch (final InterruptedException e) {
                            throw new IOException(e);
                        }
                    }
                    if (!this.readPending.tryAcquire()) {
                        if (Nio2Endpoint.log.isDebugEnabled()) {
                            Nio2Endpoint.log.debug((Object)("Socket: [" + this + "], Read in progress. Returning [0]"));
                        }
                        return 0;
                    }
                }
            }
            int nRead = this.populateReadBuffer(to);
            if (nRead > 0) {
                this.readNotify = false;
                this.readPending.release();
                return nRead;
            }
            synchronized (this.readCompletionHandler) {
                final int limit = this.socketBufferHandler.getReadBuffer().capacity();
                if (block && to.remaining() >= limit) {
                    to.limit(to.position() + limit);
                    nRead = this.fillReadBuffer(block, to);
                    if (Nio2Endpoint.log.isDebugEnabled()) {
                        Nio2Endpoint.log.debug((Object)("Socket: [" + this + "], Read direct from socket: [" + nRead + "]"));
                    }
                }
                else {
                    nRead = this.fillReadBuffer(block);
                    if (Nio2Endpoint.log.isDebugEnabled()) {
                        Nio2Endpoint.log.debug((Object)("Socket: [" + this + "], Read into buffer: [" + nRead + "]"));
                    }
                    if (nRead > 0) {
                        nRead = this.populateReadBuffer(to);
                    }
                    else if (nRead == 0 && !block) {
                        this.readInterest = true;
                    }
                }
                return nRead;
            }
        }
        
        @Override
        public void close() {
            if (Nio2Endpoint.log.isDebugEnabled()) {
                Nio2Endpoint.log.debug((Object)("Calling [" + this.getEndpoint() + "].closeSocket([" + this + "])"), (Throwable)new Exception());
            }
            try {
                this.getEndpoint().getHandler().release(this);
            }
            catch (final Throwable e) {
                ExceptionUtils.handleThrowable(e);
                if (Nio2Endpoint.log.isDebugEnabled()) {
                    Nio2Endpoint.log.error((Object)"Channel close error", e);
                }
            }
            try {
                synchronized (this.getSocket()) {
                    if (!this.closed) {
                        this.closed = true;
                        this.getEndpoint().countDownConnection();
                    }
                    if (this.getSocket().isOpen()) {
                        this.getSocket().close(true);
                    }
                }
            }
            catch (final Throwable e) {
                ExceptionUtils.handleThrowable(e);
                if (Nio2Endpoint.log.isDebugEnabled()) {
                    Nio2Endpoint.log.error((Object)"Channel close error", e);
                }
            }
            try {
                final SendfileData data = this.getSendfileData();
                if (data != null && data.fchannel != null && data.fchannel.isOpen()) {
                    data.fchannel.close();
                }
            }
            catch (final Throwable e) {
                ExceptionUtils.handleThrowable(e);
                if (Nio2Endpoint.log.isDebugEnabled()) {
                    Nio2Endpoint.log.error((Object)"Channel close error", e);
                }
            }
        }
        
        @Override
        public boolean isClosed() {
            return this.closed;
        }
        
        @Override
        public boolean hasAsyncIO() {
            return this.getEndpoint().getUseAsyncIO();
        }
        
        @Override
        public boolean needSemaphores() {
            return true;
        }
        
        @Override
        public boolean hasPerOperationTimeout() {
            return true;
        }
        
        @Override
        protected <A> OperationState<A> newOperationState(final boolean read, final ByteBuffer[] buffers, final int offset, final int length, final BlockingMode block, final long timeout, final TimeUnit unit, final A attachment, final CompletionCheck check, final CompletionHandler<Long, ? super A> handler, final Semaphore semaphore, final VectoredIOCompletionHandler<A> completion) {
            return new Nio2OperationState<A>(read, buffers, offset, length, block, timeout, unit, (Object)attachment, check, (CompletionHandler)handler, semaphore, (VectoredIOCompletionHandler)completion);
        }
        
        private int fillReadBuffer(final boolean block) throws IOException {
            this.socketBufferHandler.configureReadBufferForWrite();
            return this.fillReadBuffer(block, this.socketBufferHandler.getReadBuffer());
        }
        
        private int fillReadBuffer(final boolean block, final ByteBuffer to) throws IOException {
            int nRead = 0;
            Future<Integer> integer = null;
            if (block) {
                try {
                    integer = this.getSocket().read(to);
                    final long timeout = this.getReadTimeout();
                    if (timeout > 0L) {
                        nRead = integer.get(timeout, TimeUnit.MILLISECONDS);
                    }
                    else {
                        nRead = integer.get();
                    }
                }
                catch (final ExecutionException e) {
                    if (e.getCause() instanceof IOException) {
                        throw (IOException)e.getCause();
                    }
                    throw new IOException(e);
                }
                catch (final InterruptedException e2) {
                    throw new IOException(e2);
                }
                catch (final TimeoutException e3) {
                    integer.cancel(true);
                    throw new SocketTimeoutException();
                }
                finally {
                    this.readPending.release();
                }
            }
            else {
                Nio2Endpoint.startInline();
                this.getSocket().read(to, AbstractEndpoint.toTimeout(this.getReadTimeout()), TimeUnit.MILLISECONDS, to, this.readCompletionHandler);
                Nio2Endpoint.endInline();
                if (this.readPending.availablePermits() == 1) {
                    nRead = to.position();
                }
            }
            return nRead;
        }
        
        @Override
        protected void writeNonBlocking(final byte[] buf, int off, int len) throws IOException {
            synchronized (this.writeCompletionHandler) {
                this.checkError();
                if (this.writeNotify || this.writePending.tryAcquire()) {
                    this.socketBufferHandler.configureWriteBufferForWrite();
                    final int thisTime = SocketWrapperBase.transfer(buf, off, len, this.socketBufferHandler.getWriteBuffer());
                    len -= thisTime;
                    off += thisTime;
                    if (len > 0) {
                        this.nonBlockingWriteBuffer.add(buf, off, len);
                    }
                    this.flushNonBlockingInternal(true);
                }
                else {
                    this.nonBlockingWriteBuffer.add(buf, off, len);
                }
            }
        }
        
        @Override
        protected void writeNonBlocking(final ByteBuffer from) throws IOException {
            this.writeNonBlockingInternal(from);
        }
        
        @Override
        protected void writeNonBlockingInternal(final ByteBuffer from) throws IOException {
            synchronized (this.writeCompletionHandler) {
                this.checkError();
                if (this.writeNotify || this.writePending.tryAcquire()) {
                    this.socketBufferHandler.configureWriteBufferForWrite();
                    SocketWrapperBase.transfer(from, this.socketBufferHandler.getWriteBuffer());
                    if (from.remaining() > 0) {
                        this.nonBlockingWriteBuffer.add(from);
                    }
                    this.flushNonBlockingInternal(true);
                }
                else {
                    this.nonBlockingWriteBuffer.add(from);
                }
            }
        }
        
        @Override
        protected void doWrite(final boolean block, final ByteBuffer from) throws IOException {
            Future<Integer> integer = null;
            try {
                do {
                    integer = this.getSocket().write(from);
                    final long timeout = this.getWriteTimeout();
                    if (timeout > 0L) {
                        if (integer.get(timeout, TimeUnit.MILLISECONDS) < 0) {
                            throw new EOFException(Nio2SocketWrapper.sm.getString("iob.failedwrite"));
                        }
                        continue;
                    }
                    else {
                        if (integer.get() < 0) {
                            throw new EOFException(Nio2SocketWrapper.sm.getString("iob.failedwrite"));
                        }
                        continue;
                    }
                } while (from.hasRemaining());
            }
            catch (final ExecutionException e) {
                if (e.getCause() instanceof IOException) {
                    throw (IOException)e.getCause();
                }
                throw new IOException(e);
            }
            catch (final InterruptedException e2) {
                throw new IOException(e2);
            }
            catch (final TimeoutException e3) {
                integer.cancel(true);
                throw new SocketTimeoutException();
            }
        }
        
        @Override
        protected void flushBlocking() throws IOException {
            this.checkError();
            try {
                if (!this.writePending.tryAcquire(AbstractEndpoint.toTimeout(this.getWriteTimeout()), TimeUnit.MILLISECONDS)) {
                    throw new SocketTimeoutException();
                }
                this.writePending.release();
            }
            catch (final InterruptedException ex) {}
            super.flushBlocking();
        }
        
        @Override
        protected boolean flushNonBlocking() throws IOException {
            this.checkError();
            return this.flushNonBlockingInternal(false);
        }
        
        private boolean flushNonBlockingInternal(final boolean hasPermit) {
            synchronized (this.writeCompletionHandler) {
                if (this.writeNotify || hasPermit || this.writePending.tryAcquire()) {
                    this.writeNotify = false;
                    this.socketBufferHandler.configureWriteBufferForRead();
                    if (!this.nonBlockingWriteBuffer.isEmpty()) {
                        final ByteBuffer[] array = this.nonBlockingWriteBuffer.toArray(this.socketBufferHandler.getWriteBuffer());
                        Nio2Endpoint.startInline();
                        this.getSocket().write(array, 0, array.length, AbstractEndpoint.toTimeout(this.getWriteTimeout()), TimeUnit.MILLISECONDS, array, this.gatheringWriteCompletionHandler);
                        Nio2Endpoint.endInline();
                    }
                    else if (this.socketBufferHandler.getWriteBuffer().hasRemaining()) {
                        Nio2Endpoint.startInline();
                        this.getSocket().write(this.socketBufferHandler.getWriteBuffer(), AbstractEndpoint.toTimeout(this.getWriteTimeout()), TimeUnit.MILLISECONDS, this.socketBufferHandler.getWriteBuffer(), this.writeCompletionHandler);
                        Nio2Endpoint.endInline();
                    }
                    else {
                        if (!hasPermit) {
                            this.writePending.release();
                        }
                        this.writeInterest = false;
                    }
                }
                return this.hasDataToWrite();
            }
        }
        
        @Override
        public boolean hasDataToRead() {
            synchronized (this.readCompletionHandler) {
                return !this.socketBufferHandler.isReadBufferEmpty() || this.readNotify || this.getError() != null;
            }
        }
        
        @Override
        public boolean hasDataToWrite() {
            synchronized (this.writeCompletionHandler) {
                return !this.socketBufferHandler.isWriteBufferEmpty() || !this.nonBlockingWriteBuffer.isEmpty() || this.writeNotify || this.writePending.availablePermits() == 0 || this.getError() != null;
            }
        }
        
        @Override
        public boolean isReadPending() {
            synchronized (this.readCompletionHandler) {
                return this.readPending.availablePermits() == 0;
            }
        }
        
        @Override
        public boolean isWritePending() {
            synchronized (this.writeCompletionHandler) {
                return this.writePending.availablePermits() == 0;
            }
        }
        
        @Override
        public boolean awaitReadComplete(final long timeout, final TimeUnit unit) {
            synchronized (this.readCompletionHandler) {
                try {
                    if (this.readNotify) {
                        return true;
                    }
                    if (this.readPending.tryAcquire(timeout, unit)) {
                        this.readPending.release();
                        return true;
                    }
                    return false;
                }
                catch (final InterruptedException e) {
                    return false;
                }
            }
        }
        
        @Override
        public boolean awaitWriteComplete(final long timeout, final TimeUnit unit) {
            synchronized (this.writeCompletionHandler) {
                try {
                    if (this.writeNotify) {
                        return true;
                    }
                    if (this.writePending.tryAcquire(timeout, unit)) {
                        this.writePending.release();
                        return true;
                    }
                    return false;
                }
                catch (final InterruptedException e) {
                    return false;
                }
            }
        }
        
        @Override
        public void registerReadInterest() {
            synchronized (this.readCompletionHandler) {
                if (this.readNotify) {
                    return;
                }
                if (Nio2Endpoint.log.isDebugEnabled()) {
                    Nio2Endpoint.log.debug((Object)Nio2SocketWrapper.sm.getString("endpoint.debug.registerRead", new Object[] { this }));
                }
                this.readInterest = true;
                if (this.readPending.tryAcquire()) {
                    try {
                        if (this.fillReadBuffer(false) > 0) {
                            this.getEndpoint().processSocket(this, SocketEvent.OPEN_READ, true);
                        }
                    }
                    catch (final IOException e) {
                        this.setError(e);
                    }
                }
            }
        }
        
        @Override
        public void registerWriteInterest() {
            synchronized (this.writeCompletionHandler) {
                if (this.writeNotify) {
                    return;
                }
                if (Nio2Endpoint.log.isDebugEnabled()) {
                    Nio2Endpoint.log.debug((Object)Nio2SocketWrapper.sm.getString("endpoint.debug.registerWrite", new Object[] { this }));
                }
                this.writeInterest = true;
                if (this.writePending.availablePermits() == 1) {
                    this.getEndpoint().processSocket(this, SocketEvent.OPEN_WRITE, true);
                }
            }
        }
        
        @Override
        public SendfileDataBase createSendfileData(final String filename, final long pos, final long length) {
            return new SendfileData(filename, pos, length);
        }
        
        @Override
        public SendfileState processSendfile(final SendfileDataBase sendfileData) {
            final SendfileData data = (SendfileData)sendfileData;
            this.setSendfileData(data);
            if (data.fchannel == null || !data.fchannel.isOpen()) {
                final Path path = new File(sendfileData.fileName).toPath();
                try {
                    data.fchannel = FileChannel.open(path, StandardOpenOption.READ).position(sendfileData.pos);
                }
                catch (final IOException e) {
                    return SendfileState.ERROR;
                }
            }
            this.getSocket().getBufHandler().configureWriteBufferForWrite();
            final ByteBuffer buffer = this.getSocket().getBufHandler().getWriteBuffer();
            int nRead = -1;
            try {
                nRead = data.fchannel.read(buffer);
            }
            catch (final IOException e2) {
                return SendfileState.ERROR;
            }
            if (nRead < 0) {
                return SendfileState.ERROR;
            }
            final SendfileData sendfileData2 = data;
            sendfileData2.length -= nRead;
            this.getSocket().getBufHandler().configureWriteBufferForRead();
            Nio2Endpoint.startInline();
            this.getSocket().write(buffer, AbstractEndpoint.toTimeout(this.getWriteTimeout()), TimeUnit.MILLISECONDS, data, this.sendfileHandler);
            Nio2Endpoint.endInline();
            if (!data.doneInline) {
                return SendfileState.PENDING;
            }
            if (data.error) {
                return SendfileState.ERROR;
            }
            return SendfileState.DONE;
        }
        
        @Override
        protected void populateRemoteAddr() {
            SocketAddress socketAddress = null;
            try {
                socketAddress = this.getSocket().getIOChannel().getRemoteAddress();
            }
            catch (final IOException ex) {}
            if (socketAddress instanceof InetSocketAddress) {
                this.remoteAddr = ((InetSocketAddress)socketAddress).getAddress().getHostAddress();
            }
        }
        
        @Override
        protected void populateRemoteHost() {
            SocketAddress socketAddress = null;
            try {
                socketAddress = this.getSocket().getIOChannel().getRemoteAddress();
            }
            catch (final IOException e) {
                Nio2Endpoint.log.warn((Object)Nio2SocketWrapper.sm.getString("endpoint.warn.noRemoteHost", new Object[] { ((SocketWrapperBase<Object>)this).getSocket() }), (Throwable)e);
            }
            if (socketAddress instanceof InetSocketAddress) {
                this.remoteHost = ((InetSocketAddress)socketAddress).getAddress().getHostName();
                if (this.remoteAddr == null) {
                    this.remoteAddr = ((InetSocketAddress)socketAddress).getAddress().getHostAddress();
                }
            }
        }
        
        @Override
        protected void populateRemotePort() {
            SocketAddress socketAddress = null;
            try {
                socketAddress = this.getSocket().getIOChannel().getRemoteAddress();
            }
            catch (final IOException e) {
                Nio2Endpoint.log.warn((Object)Nio2SocketWrapper.sm.getString("endpoint.warn.noRemotePort", new Object[] { ((SocketWrapperBase<Object>)this).getSocket() }), (Throwable)e);
            }
            if (socketAddress instanceof InetSocketAddress) {
                this.remotePort = ((InetSocketAddress)socketAddress).getPort();
            }
        }
        
        @Override
        protected void populateLocalName() {
            SocketAddress socketAddress = null;
            try {
                socketAddress = this.getSocket().getIOChannel().getLocalAddress();
            }
            catch (final IOException e) {
                Nio2Endpoint.log.warn((Object)Nio2SocketWrapper.sm.getString("endpoint.warn.noLocalName", new Object[] { ((SocketWrapperBase<Object>)this).getSocket() }), (Throwable)e);
            }
            if (socketAddress instanceof InetSocketAddress) {
                this.localName = ((InetSocketAddress)socketAddress).getHostName();
            }
        }
        
        @Override
        protected void populateLocalAddr() {
            SocketAddress socketAddress = null;
            try {
                socketAddress = this.getSocket().getIOChannel().getLocalAddress();
            }
            catch (final IOException e) {
                Nio2Endpoint.log.warn((Object)Nio2SocketWrapper.sm.getString("endpoint.warn.noLocalAddr", new Object[] { ((SocketWrapperBase<Object>)this).getSocket() }), (Throwable)e);
            }
            if (socketAddress instanceof InetSocketAddress) {
                this.localAddr = ((InetSocketAddress)socketAddress).getAddress().getHostAddress();
            }
        }
        
        @Override
        protected void populateLocalPort() {
            SocketAddress socketAddress = null;
            try {
                socketAddress = this.getSocket().getIOChannel().getLocalAddress();
            }
            catch (final IOException e) {
                Nio2Endpoint.log.warn((Object)Nio2SocketWrapper.sm.getString("endpoint.warn.noLocalPort", new Object[] { ((SocketWrapperBase<Object>)this).getSocket() }), (Throwable)e);
            }
            if (socketAddress instanceof InetSocketAddress) {
                this.localPort = ((InetSocketAddress)socketAddress).getPort();
            }
        }
        
        @Override
        public SSLSupport getSslSupport(final String clientCertProvider) {
            if (this.getSocket() instanceof SecureNio2Channel) {
                final SecureNio2Channel ch = ((SocketWrapperBase<SecureNio2Channel>)this).getSocket();
                return ch.getSSLSupport();
            }
            return null;
        }
        
        @Override
        public void doClientAuth(final SSLSupport sslSupport) throws IOException {
            final SecureNio2Channel sslChannel = ((SocketWrapperBase<SecureNio2Channel>)this).getSocket();
            final SSLEngine engine = sslChannel.getSslEngine();
            if (!engine.getNeedClientAuth()) {
                engine.setNeedClientAuth(true);
                sslChannel.rehandshake();
                ((JSSESupport)sslSupport).setSession(engine.getSession());
            }
        }
        
        @Override
        public void setAppReadBufHandler(final ApplicationBufferHandler handler) {
            this.getSocket().setAppReadBufHandler(handler);
        }
        
        private class Nio2OperationState<A> extends OperationState<A>
        {
            private Nio2OperationState(final boolean read, final ByteBuffer[] buffers, final int offset, final int length, final BlockingMode block, final long timeout, final TimeUnit unit, final A attachment, final CompletionCheck check, final CompletionHandler<Long, ? super A> handler, final Semaphore semaphore, final VectoredIOCompletionHandler<A> completion) {
                super(read, buffers, offset, length, block, timeout, unit, attachment, check, handler, semaphore, completion);
            }
            
            @Override
            protected boolean isInline() {
                return Nio2Endpoint.isInline();
            }
            
            @Override
            protected void start() {
                if (this.read) {
                    Nio2SocketWrapper.this.readNotify = true;
                }
                else {
                    Nio2SocketWrapper.this.writeNotify = true;
                }
                Nio2Endpoint.startInline();
                this.run();
                Nio2Endpoint.endInline();
            }
            
            @Override
            public void run() {
                if (this.read) {
                    long nBytes = 0L;
                    if (!Nio2SocketWrapper.this.socketBufferHandler.isReadBufferEmpty()) {
                        synchronized (Nio2SocketWrapper.this.readCompletionHandler) {
                            Nio2SocketWrapper.this.socketBufferHandler.configureReadBufferForRead();
                            for (int i = 0; i < this.length && !Nio2SocketWrapper.this.socketBufferHandler.isReadBufferEmpty(); ++i) {
                                nBytes += SocketWrapperBase.transfer(Nio2SocketWrapper.this.socketBufferHandler.getReadBuffer(), this.buffers[this.offset + i]);
                            }
                        }
                        if (nBytes > 0L) {
                            this.completion.completed(Long.valueOf(nBytes), (OperationState<A>)this);
                        }
                    }
                    if (nBytes == 0L) {
                        Nio2SocketWrapper.this.getSocket().read(this.buffers, this.offset, this.length, this.timeout, this.unit, this, this.completion);
                    }
                }
                else {
                    if (!Nio2SocketWrapper.this.socketBufferHandler.isWriteBufferEmpty()) {
                        synchronized (Nio2SocketWrapper.this.writeCompletionHandler) {
                            Nio2SocketWrapper.this.socketBufferHandler.configureWriteBufferForRead();
                            final ByteBuffer[] array = Nio2SocketWrapper.this.nonBlockingWriteBuffer.toArray(Nio2SocketWrapper.this.socketBufferHandler.getWriteBuffer());
                            if (SocketWrapperBase.buffersArrayHasRemaining(array, 0, array.length)) {
                                Nio2SocketWrapper.this.getSocket().write(array, 0, array.length, this.timeout, this.unit, array, new CompletionHandler<Long, ByteBuffer[]>() {
                                    @Override
                                    public void completed(final Long nBytes, final ByteBuffer[] buffers) {
                                        if (nBytes < 0L) {
                                            this.failed((Throwable)new EOFException(), (ByteBuffer[])null);
                                        }
                                        else if (SocketWrapperBase.buffersArrayHasRemaining(buffers, 0, buffers.length)) {
                                            Nio2SocketWrapper.this.getSocket().write(buffers, 0, buffers.length, AbstractEndpoint.toTimeout(Nio2SocketWrapper.this.getWriteTimeout()), TimeUnit.MILLISECONDS, buffers, this);
                                        }
                                        else {
                                            Nio2OperationState.this.process();
                                        }
                                    }
                                    
                                    @Override
                                    public void failed(final Throwable exc, final ByteBuffer[] buffers) {
                                        Nio2OperationState.this.completion.failed(exc, (OperationState<A>)Nio2OperationState.this);
                                    }
                                });
                                return;
                            }
                        }
                    }
                    Nio2SocketWrapper.this.getSocket().write(this.buffers, this.offset, this.length, this.timeout, this.unit, this, this.completion);
                }
            }
        }
    }
    
    protected class SocketProcessor extends SocketProcessorBase<Nio2Channel>
    {
        public SocketProcessor(final SocketWrapperBase<Nio2Channel> socketWrapper, final SocketEvent event) {
            super(socketWrapper, event);
        }
        
        @Override
        protected void doRun() {
            boolean launch = false;
            try {
                int handshake = -1;
                try {
                    if (((Nio2Channel)this.socketWrapper.getSocket()).isHandshakeComplete()) {
                        handshake = 0;
                    }
                    else if (this.event == SocketEvent.STOP || this.event == SocketEvent.DISCONNECT || this.event == SocketEvent.ERROR) {
                        handshake = -1;
                    }
                    else {
                        handshake = ((Nio2Channel)this.socketWrapper.getSocket()).handshake();
                        this.event = SocketEvent.OPEN_READ;
                    }
                }
                catch (final IOException x) {
                    handshake = -1;
                    if (Nio2Endpoint.log.isDebugEnabled()) {
                        Nio2Endpoint.log.debug((Object)AbstractEndpoint.sm.getString("endpoint.err.handshake"), (Throwable)x);
                    }
                }
                if (handshake == 0) {
                    Handler.SocketState state = Handler.SocketState.OPEN;
                    if (this.event == null) {
                        state = Nio2Endpoint.this.getHandler().process((SocketWrapperBase<Nio2Channel>)this.socketWrapper, SocketEvent.OPEN_READ);
                    }
                    else {
                        state = Nio2Endpoint.this.getHandler().process((SocketWrapperBase<Nio2Channel>)this.socketWrapper, this.event);
                    }
                    if (state == Handler.SocketState.CLOSED) {
                        this.socketWrapper.close();
                        if (Nio2Endpoint.this.running && !Nio2Endpoint.this.paused && !Nio2Endpoint.this.nioChannels.push((Object)this.socketWrapper.getSocket())) {
                            ((Nio2Channel)this.socketWrapper.getSocket()).free();
                        }
                    }
                    else if (state == Handler.SocketState.UPGRADING) {
                        launch = true;
                    }
                }
                else if (handshake == -1) {
                    Nio2Endpoint.this.getHandler().process((SocketWrapperBase<Nio2Channel>)this.socketWrapper, SocketEvent.CONNECT_FAIL);
                    this.socketWrapper.close();
                    if (Nio2Endpoint.this.running && !Nio2Endpoint.this.paused && !Nio2Endpoint.this.nioChannels.push((Object)this.socketWrapper.getSocket())) {
                        ((Nio2Channel)this.socketWrapper.getSocket()).free();
                    }
                }
            }
            catch (final VirtualMachineError vme) {
                ExceptionUtils.handleThrowable((Throwable)vme);
            }
            catch (final Throwable t) {
                Nio2Endpoint.log.error((Object)AbstractEndpoint.sm.getString("endpoint.processing.fail"), t);
                if (this.socketWrapper != null) {
                    ((Nio2SocketWrapper)this.socketWrapper).close();
                }
            }
            finally {
                if (launch) {
                    try {
                        Nio2Endpoint.this.getExecutor().execute(new SocketProcessor((SocketWrapperBase<Nio2Channel>)this.socketWrapper, SocketEvent.OPEN_READ));
                    }
                    catch (final NullPointerException npe) {
                        if (Nio2Endpoint.this.running) {
                            Nio2Endpoint.log.error((Object)AbstractEndpoint.sm.getString("endpoint.launch.fail"), (Throwable)npe);
                        }
                    }
                }
                this.socketWrapper = null;
                this.event = null;
                if (Nio2Endpoint.this.running && !Nio2Endpoint.this.paused) {
                    Nio2Endpoint.this.processorCache.push((Object)this);
                }
            }
        }
    }
    
    public static class SendfileData extends SendfileDataBase
    {
        private FileChannel fchannel;
        private boolean doneInline;
        private boolean error;
        
        public SendfileData(final String filename, final long pos, final long length) {
            super(filename, pos, length);
            this.doneInline = false;
            this.error = false;
        }
    }
}
