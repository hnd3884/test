package com.sun.corba.se.impl.transport;

import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.org.omg.SendingContext.CodeBase;
import org.omg.CORBA.portable.OutputStream;
import sun.corba.OutputStreamFactory;
import com.sun.corba.se.impl.protocol.giopmsgheaders.Message;
import com.sun.corba.se.impl.protocol.giopmsgheaders.MessageBase;
import com.sun.corba.se.pept.transport.InboundConnectionCache;
import com.sun.corba.se.pept.transport.OutboundConnectionCache;
import org.omg.CORBA.CompletionStatus;
import com.sun.corba.se.impl.encoding.OSFCodeSetRegistry;
import com.sun.corba.se.pept.transport.ResponseWaitingRoom;
import java.nio.channels.SelectableChannel;
import com.sun.corba.se.spi.orbutil.threadpool.NoSuchWorkQueueException;
import com.sun.corba.se.spi.orbutil.threadpool.NoSuchThreadPoolException;
import com.sun.corba.se.pept.encoding.InputObject;
import org.omg.CORBA.COMM_FAILURE;
import com.sun.corba.se.impl.encoding.CDROutputObject;
import com.sun.corba.se.pept.encoding.OutputObject;
import com.sun.corba.se.pept.transport.ContactInfo;
import java.io.InputStream;
import java.nio.ByteBuffer;
import com.sun.corba.se.pept.transport.Selector;
import com.sun.corba.se.pept.transport.EventHandler;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import org.omg.CORBA.INTERNAL;
import org.omg.CORBA.SystemException;
import com.sun.corba.se.pept.transport.Connection;
import com.sun.corba.se.pept.broker.Broker;
import java.util.Collections;
import java.util.HashMap;
import java.io.IOException;
import java.net.InetSocketAddress;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.pept.protocol.MessageMediator;
import com.sun.corba.se.impl.encoding.CodeSetComponentInfo;
import com.sun.corba.se.spi.protocol.CorbaMessageMediator;
import com.sun.corba.se.spi.transport.ReadTimeouts;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.impl.encoding.CachedCodeBase;
import com.sun.corba.se.spi.ior.IOR;
import java.util.Map;
import com.sun.corba.se.spi.transport.CorbaResponseWaitingRoom;
import java.net.Socket;
import com.sun.corba.se.pept.transport.ConnectionCache;
import com.sun.corba.se.pept.transport.Acceptor;
import com.sun.corba.se.spi.transport.CorbaContactInfo;
import java.nio.channels.SocketChannel;
import com.sun.corba.se.spi.orbutil.threadpool.Work;
import com.sun.corba.se.spi.transport.CorbaConnection;

public class SocketOrChannelConnectionImpl extends EventHandlerBase implements CorbaConnection, Work
{
    public static boolean dprintWriteLocks;
    protected long enqueueTime;
    protected SocketChannel socketChannel;
    protected CorbaContactInfo contactInfo;
    protected Acceptor acceptor;
    protected ConnectionCache connectionCache;
    protected Socket socket;
    protected long timeStamp;
    protected boolean isServer;
    protected int requestId;
    protected CorbaResponseWaitingRoom responseWaitingRoom;
    protected int state;
    protected Object stateEvent;
    protected Object writeEvent;
    protected boolean writeLocked;
    protected int serverRequestCount;
    Map serverRequestMap;
    protected boolean postInitialContexts;
    protected IOR codeBaseServerIOR;
    protected CachedCodeBase cachedCodeBase;
    protected ORBUtilSystemException wrapper;
    protected ReadTimeouts readTimeouts;
    protected boolean shouldReadGiopHeaderOnly;
    protected CorbaMessageMediator partialMessageMediator;
    protected CodeSetComponentInfo.CodeSetContext codeSetContext;
    protected MessageMediator clientReply_1_1;
    protected MessageMediator serverRequest_1_1;
    
    @Override
    public SocketChannel getSocketChannel() {
        return this.socketChannel;
    }
    
    protected SocketOrChannelConnectionImpl(final ORB orb) {
        this.timeStamp = 0L;
        this.isServer = false;
        this.requestId = 5;
        this.stateEvent = new Object();
        this.writeEvent = new Object();
        this.serverRequestCount = 0;
        this.serverRequestMap = null;
        this.postInitialContexts = false;
        this.cachedCodeBase = new CachedCodeBase(this);
        this.partialMessageMediator = null;
        this.codeSetContext = null;
        this.orb = orb;
        this.wrapper = ORBUtilSystemException.get(orb, "rpc.transport");
        this.setWork(this);
        this.responseWaitingRoom = new CorbaResponseWaitingRoomImpl(orb, this);
        this.setReadTimeouts(orb.getORBData().getTransportTCPReadTimeouts());
    }
    
    protected SocketOrChannelConnectionImpl(final ORB orb, final boolean useSelectThreadToWait, final boolean useWorkerThreadForEvent) {
        this(orb);
        this.setUseSelectThreadToWait(useSelectThreadToWait);
        this.setUseWorkerThreadForEvent(useWorkerThreadForEvent);
    }
    
    public SocketOrChannelConnectionImpl(final ORB orb, final CorbaContactInfo contactInfo, final boolean b, final boolean b2, final String s, final String s2, final int n) {
        this(orb, b, b2);
        this.contactInfo = contactInfo;
        try {
            this.socket = orb.getORBData().getSocketFactory().createSocket(s, new InetSocketAddress(s2, n));
            this.socketChannel = this.socket.getChannel();
            if (this.socketChannel != null) {
                this.socketChannel.configureBlocking(!b);
            }
            else {
                this.setUseSelectThreadToWait(false);
            }
            if (orb.transportDebugFlag) {
                this.dprint(".initialize: connection created: " + this.socket);
            }
        }
        catch (final Throwable t) {
            throw this.wrapper.connectFailure(t, s, s2, Integer.toString(n));
        }
        this.state = 1;
    }
    
    public SocketOrChannelConnectionImpl(final ORB orb, final CorbaContactInfo corbaContactInfo, final String s, final String s2, final int n) {
        this(orb, corbaContactInfo, orb.getORBData().connectionSocketUseSelectThreadToWait(), orb.getORBData().connectionSocketUseWorkerThreadForEvent(), s, s2, n);
    }
    
    public SocketOrChannelConnectionImpl(final ORB orb, final Acceptor acceptor, final Socket socket, final boolean b, final boolean b2) {
        this(orb, b, b2);
        this.socket = socket;
        this.socketChannel = socket.getChannel();
        if (this.socketChannel != null) {
            try {
                this.socketChannel.configureBlocking(!b);
            }
            catch (final IOException ex) {
                final RuntimeException ex2 = new RuntimeException();
                ex2.initCause(ex);
                throw ex2;
            }
        }
        this.acceptor = acceptor;
        this.serverRequestMap = Collections.synchronizedMap(new HashMap<Object, Object>());
        this.isServer = true;
        this.state = 2;
    }
    
    public SocketOrChannelConnectionImpl(final ORB orb, final Acceptor acceptor, final Socket socket) {
        this(orb, acceptor, socket, socket.getChannel() != null && orb.getORBData().connectionSocketUseSelectThreadToWait(), socket.getChannel() != null && orb.getORBData().connectionSocketUseWorkerThreadForEvent());
    }
    
    @Override
    public boolean shouldRegisterReadEvent() {
        return true;
    }
    
    @Override
    public boolean shouldRegisterServerReadEvent() {
        return true;
    }
    
    @Override
    public boolean read() {
        try {
            if (this.orb.transportDebugFlag) {
                this.dprint(".read->: " + this);
            }
            final CorbaMessageMediator bits = this.readBits();
            return bits == null || this.dispatch(bits);
        }
        finally {
            if (this.orb.transportDebugFlag) {
                this.dprint(".read<-: " + this);
            }
        }
    }
    
    protected CorbaMessageMediator readBits() {
        try {
            if (this.orb.transportDebugFlag) {
                this.dprint(".readBits->: " + this);
            }
            MessageMediator messageMediator;
            if (this.contactInfo != null) {
                messageMediator = this.contactInfo.createMessageMediator(this.orb, this);
            }
            else {
                if (this.acceptor == null) {
                    throw new RuntimeException("SocketOrChannelConnectionImpl.readBits");
                }
                messageMediator = this.acceptor.createMessageMediator(this.orb, this);
            }
            return (CorbaMessageMediator)messageMediator;
        }
        catch (final ThreadDeath threadDeath) {
            if (this.orb.transportDebugFlag) {
                this.dprint(".readBits: " + this + ": ThreadDeath: " + threadDeath, threadDeath);
            }
            try {
                this.purgeCalls(this.wrapper.connectionAbort(threadDeath), false, false);
            }
            catch (final Throwable t) {
                if (this.orb.transportDebugFlag) {
                    this.dprint(".readBits: " + this + ": purgeCalls: Throwable: " + t, t);
                }
            }
            throw threadDeath;
        }
        catch (final Throwable threadDeath) {
            if (this.orb.transportDebugFlag) {
                this.dprint(".readBits: " + this + ": Throwable: " + threadDeath, threadDeath);
            }
            try {
                if (threadDeath instanceof INTERNAL) {
                    this.sendMessageError(GIOPVersion.DEFAULT_VERSION);
                }
            }
            catch (final IOException ex) {
                if (this.orb.transportDebugFlag) {
                    this.dprint(".readBits: " + this + ": sendMessageError: IOException: " + ex, ex);
                }
            }
            final Selector selector = this.orb.getTransportManager().getSelector(0);
            if (selector != null) {
                selector.unregisterForEvent(this);
            }
            this.purgeCalls(this.wrapper.connectionAbort(threadDeath), true, false);
        }
        finally {
            if (this.orb.transportDebugFlag) {
                this.dprint(".readBits<-: " + this);
            }
        }
        return null;
    }
    
    protected CorbaMessageMediator finishReadingBits(MessageMediator messageMediator) {
        try {
            if (this.orb.transportDebugFlag) {
                this.dprint(".finishReadingBits->: " + this);
            }
            if (this.contactInfo != null) {
                messageMediator = this.contactInfo.finishCreatingMessageMediator(this.orb, this, messageMediator);
            }
            else {
                if (this.acceptor == null) {
                    throw new RuntimeException("SocketOrChannelConnectionImpl.finishReadingBits");
                }
                messageMediator = this.acceptor.finishCreatingMessageMediator(this.orb, this, messageMediator);
            }
            return (CorbaMessageMediator)messageMediator;
        }
        catch (final ThreadDeath threadDeath) {
            if (this.orb.transportDebugFlag) {
                this.dprint(".finishReadingBits: " + this + ": ThreadDeath: " + threadDeath, threadDeath);
            }
            try {
                this.purgeCalls(this.wrapper.connectionAbort(threadDeath), false, false);
            }
            catch (final Throwable t) {
                if (this.orb.transportDebugFlag) {
                    this.dprint(".finishReadingBits: " + this + ": purgeCalls: Throwable: " + t, t);
                }
            }
            throw threadDeath;
        }
        catch (final Throwable threadDeath) {
            if (this.orb.transportDebugFlag) {
                this.dprint(".finishReadingBits: " + this + ": Throwable: " + threadDeath, threadDeath);
            }
            try {
                if (threadDeath instanceof INTERNAL) {
                    this.sendMessageError(GIOPVersion.DEFAULT_VERSION);
                }
            }
            catch (final IOException ex) {
                if (this.orb.transportDebugFlag) {
                    this.dprint(".finishReadingBits: " + this + ": sendMessageError: IOException: " + ex, ex);
                }
            }
            this.orb.getTransportManager().getSelector(0).unregisterForEvent(this);
            this.purgeCalls(this.wrapper.connectionAbort(threadDeath), true, false);
        }
        finally {
            if (this.orb.transportDebugFlag) {
                this.dprint(".finishReadingBits<-: " + this);
            }
        }
        return null;
    }
    
    protected boolean dispatch(final CorbaMessageMediator corbaMessageMediator) {
        try {
            if (this.orb.transportDebugFlag) {
                this.dprint(".dispatch->: " + this);
            }
            return corbaMessageMediator.getProtocolHandler().handleRequest(corbaMessageMediator);
        }
        catch (final ThreadDeath threadDeath) {
            if (this.orb.transportDebugFlag) {
                this.dprint(".dispatch: ThreadDeath", threadDeath);
            }
            try {
                this.purgeCalls(this.wrapper.connectionAbort(threadDeath), false, false);
            }
            catch (final Throwable t) {
                if (this.orb.transportDebugFlag) {
                    this.dprint(".dispatch: purgeCalls: Throwable", t);
                }
            }
            throw threadDeath;
        }
        catch (final Throwable threadDeath) {
            if (this.orb.transportDebugFlag) {
                this.dprint(".dispatch: Throwable", threadDeath);
            }
            try {
                if (threadDeath instanceof INTERNAL) {
                    this.sendMessageError(GIOPVersion.DEFAULT_VERSION);
                }
            }
            catch (final IOException ex) {
                if (this.orb.transportDebugFlag) {
                    this.dprint(".dispatch: sendMessageError: IOException", ex);
                }
            }
            this.purgeCalls(this.wrapper.connectionAbort(threadDeath), false, false);
        }
        finally {
            if (this.orb.transportDebugFlag) {
                this.dprint(".dispatch<-: " + this);
            }
        }
        return true;
    }
    
    @Override
    public boolean shouldUseDirectByteBuffers() {
        return this.getSocketChannel() != null;
    }
    
    @Override
    public ByteBuffer read(final int n, final int n2, final int n3, final long n4) throws IOException {
        if (this.shouldUseDirectByteBuffers()) {
            final ByteBuffer byteBuffer = this.orb.getByteBufferPool().getByteBuffer(n);
            if (this.orb.transportDebugFlag) {
                final int identityHashCode = System.identityHashCode(byteBuffer);
                final StringBuffer sb = new StringBuffer(80);
                sb.append(".read: got ByteBuffer id (");
                sb.append(identityHashCode).append(") from ByteBufferPool.");
                this.dprint(sb.toString());
            }
            byteBuffer.position(n2);
            byteBuffer.limit(n);
            this.readFully(byteBuffer, n3, n4);
            return byteBuffer;
        }
        final byte[] array = new byte[n];
        this.readFully(this.getSocket().getInputStream(), array, n2, n3, n4);
        final ByteBuffer wrap = ByteBuffer.wrap(array);
        wrap.limit(n);
        return wrap;
    }
    
    @Override
    public ByteBuffer read(ByteBuffer byteBuffer, final int n, final int n2, final long n3) throws IOException {
        final int n4 = n + n2;
        if (this.shouldUseDirectByteBuffers()) {
            if (!byteBuffer.isDirect()) {
                throw this.wrapper.unexpectedNonDirectByteBufferWithChannelSocket();
            }
            if (n4 > byteBuffer.capacity()) {
                if (this.orb.transportDebugFlag) {
                    final int identityHashCode = System.identityHashCode(byteBuffer);
                    final StringBuffer sb = new StringBuffer(80);
                    sb.append(".read: releasing ByteBuffer id (").append(identityHashCode).append(") to ByteBufferPool.");
                    this.dprint(sb.toString());
                }
                this.orb.getByteBufferPool().releaseByteBuffer(byteBuffer);
                byteBuffer = this.orb.getByteBufferPool().getByteBuffer(n4);
            }
            byteBuffer.position(n);
            byteBuffer.limit(n4);
            this.readFully(byteBuffer, n2, n3);
            byteBuffer.position(0);
            byteBuffer.limit(n4);
            return byteBuffer;
        }
        else {
            if (byteBuffer.isDirect()) {
                throw this.wrapper.unexpectedDirectByteBufferWithNonChannelSocket();
            }
            final byte[] array = new byte[n4];
            this.readFully(this.getSocket().getInputStream(), array, n, n2, n3);
            return ByteBuffer.wrap(array);
        }
    }
    
    public void readFully(final ByteBuffer byteBuffer, final int n, final long n2) throws IOException {
        int n3 = 0;
        long n4 = this.readTimeouts.get_initial_time_to_wait();
        long n5 = 0L;
        do {
            final int read = this.getSocketChannel().read(byteBuffer);
            if (read < 0) {
                throw new IOException("End-of-stream");
            }
            if (read == 0) {
                try {
                    Thread.sleep(n4);
                    n5 += n4;
                    n4 *= (long)this.readTimeouts.get_backoff_factor();
                }
                catch (final InterruptedException ex) {
                    if (!this.orb.transportDebugFlag) {
                        continue;
                    }
                    this.dprint("readFully(): unexpected exception " + ex.toString());
                }
            }
            else {
                n3 += read;
            }
        } while (n3 < n && n5 < n2);
        if (n3 < n && n5 >= n2) {
            throw this.wrapper.transportReadTimeoutExceeded(new Integer(n), new Integer(n3), new Long(n2), new Long(n5));
        }
        this.getConnectionCache().stampTime(this);
    }
    
    public void readFully(final InputStream inputStream, final byte[] array, final int n, final int n2, final long n3) throws IOException {
        int n4 = 0;
        long n5 = this.readTimeouts.get_initial_time_to_wait();
        long n6 = 0L;
        do {
            final int read = inputStream.read(array, n + n4, n2 - n4);
            if (read < 0) {
                throw new IOException("End-of-stream");
            }
            if (read == 0) {
                try {
                    Thread.sleep(n5);
                    n6 += n5;
                    n5 *= (long)this.readTimeouts.get_backoff_factor();
                }
                catch (final InterruptedException ex) {
                    if (!this.orb.transportDebugFlag) {
                        continue;
                    }
                    this.dprint("readFully(): unexpected exception " + ex.toString());
                }
            }
            else {
                n4 += read;
            }
        } while (n4 < n2 && n6 < n3);
        if (n4 < n2 && n6 >= n3) {
            throw this.wrapper.transportReadTimeoutExceeded(new Integer(n2), new Integer(n4), new Long(n3), new Long(n6));
        }
        this.getConnectionCache().stampTime(this);
    }
    
    @Override
    public void write(final ByteBuffer byteBuffer) throws IOException {
        if (this.shouldUseDirectByteBuffers()) {
            do {
                this.getSocketChannel().write(byteBuffer);
            } while (byteBuffer.hasRemaining());
        }
        else {
            if (!byteBuffer.hasArray()) {
                throw this.wrapper.unexpectedDirectByteBufferWithNonChannelSocket();
            }
            this.getSocket().getOutputStream().write(byteBuffer.array(), 0, byteBuffer.limit());
            this.getSocket().getOutputStream().flush();
        }
        this.getConnectionCache().stampTime(this);
    }
    
    @Override
    public synchronized void close() {
        try {
            if (this.orb.transportDebugFlag) {
                this.dprint(".close->: " + this);
            }
            this.writeLock();
            if (this.isBusy()) {
                this.writeUnlock();
                if (this.orb.transportDebugFlag) {
                    this.dprint(".close: isBusy so no close: " + this);
                }
                return;
            }
            try {
                try {
                    this.sendCloseConnection(GIOPVersion.V1_0);
                }
                catch (final Throwable t) {
                    this.wrapper.exceptionWhenSendingCloseConnection(t);
                }
                synchronized (this.stateEvent) {
                    this.state = 3;
                    this.stateEvent.notifyAll();
                }
                this.purgeCalls(this.wrapper.connectionRebind(), false, true);
            }
            catch (final Exception ex) {
                if (this.orb.transportDebugFlag) {
                    this.dprint(".close: exception: " + this, ex);
                }
            }
            try {
                final Selector selector = this.orb.getTransportManager().getSelector(0);
                if (selector != null) {
                    selector.unregisterForEvent(this);
                }
                if (this.socketChannel != null) {
                    this.socketChannel.close();
                }
                this.socket.close();
            }
            catch (final IOException ex2) {
                if (this.orb.transportDebugFlag) {
                    this.dprint(".close: " + this, ex2);
                }
            }
            this.closeConnectionResources();
        }
        finally {
            if (this.orb.transportDebugFlag) {
                this.dprint(".close<-: " + this);
            }
        }
    }
    
    @Override
    public void closeConnectionResources() {
        if (this.orb.transportDebugFlag) {
            this.dprint(".closeConnectionResources->: " + this);
        }
        final Selector selector = this.orb.getTransportManager().getSelector(0);
        if (selector != null) {
            selector.unregisterForEvent(this);
        }
        try {
            if (this.socketChannel != null) {
                this.socketChannel.close();
            }
            if (this.socket != null && !this.socket.isClosed()) {
                this.socket.close();
            }
        }
        catch (final IOException ex) {
            if (this.orb.transportDebugFlag) {
                this.dprint(".closeConnectionResources: " + this, ex);
            }
        }
        if (this.orb.transportDebugFlag) {
            this.dprint(".closeConnectionResources<-: " + this);
        }
    }
    
    @Override
    public Acceptor getAcceptor() {
        return this.acceptor;
    }
    
    @Override
    public ContactInfo getContactInfo() {
        return this.contactInfo;
    }
    
    @Override
    public EventHandler getEventHandler() {
        return this;
    }
    
    public OutputObject createOutputObject(final MessageMediator messageMediator) {
        throw new RuntimeException("*****SocketOrChannelConnectionImpl.createOutputObject - should not be called.");
    }
    
    @Override
    public boolean isServer() {
        return this.isServer;
    }
    
    @Override
    public boolean isBusy() {
        return this.serverRequestCount > 0 || this.getResponseWaitingRoom().numberRegistered() > 0;
    }
    
    @Override
    public long getTimeStamp() {
        return this.timeStamp;
    }
    
    @Override
    public void setTimeStamp(final long timeStamp) {
        this.timeStamp = timeStamp;
    }
    
    @Override
    public void setState(final String s) {
        synchronized (this.stateEvent) {
            if (s.equals("ESTABLISHED")) {
                this.state = 2;
                this.stateEvent.notifyAll();
            }
        }
    }
    
    @Override
    public void writeLock() {
        try {
            if (SocketOrChannelConnectionImpl.dprintWriteLocks && this.orb.transportDebugFlag) {
                this.dprint(".writeLock->: " + this);
            }
        Label_0370:
            while (true) {
                switch (this.state) {
                    case 1: {
                        synchronized (this.stateEvent) {
                            if (this.state != 1) {
                                continue;
                            }
                            try {
                                this.stateEvent.wait();
                            }
                            catch (final InterruptedException ex) {
                                if (!this.orb.transportDebugFlag) {
                                    continue;
                                }
                                this.dprint(".writeLock: OPENING InterruptedException: " + this);
                            }
                        }
                        continue;
                    }
                    case 2: {
                        synchronized (this.writeEvent) {
                            if (!this.writeLocked) {
                                this.writeLocked = true;
                                return;
                            }
                            try {
                                while (this.state == 2 && this.writeLocked) {
                                    this.writeEvent.wait(100L);
                                }
                            }
                            catch (final InterruptedException ex2) {
                                if (!this.orb.transportDebugFlag) {
                                    continue;
                                }
                                this.dprint(".writeLock: ESTABLISHED InterruptedException: " + this);
                            }
                        }
                        continue;
                    }
                    case 5: {
                        synchronized (this.stateEvent) {
                            if (this.state != 5) {
                                continue;
                            }
                            throw this.wrapper.writeErrorSend();
                        }
                    }
                    case 4: {
                        synchronized (this.stateEvent) {
                            if (this.state != 4) {
                                continue;
                            }
                            throw this.wrapper.connectionCloseRebind();
                        }
                    }
                    default: {
                        break Label_0370;
                    }
                }
            }
            if (this.orb.transportDebugFlag) {
                this.dprint(".writeLock: default: " + this);
            }
            throw new RuntimeException(".writeLock: bad state");
        }
        finally {
            if (SocketOrChannelConnectionImpl.dprintWriteLocks && this.orb.transportDebugFlag) {
                this.dprint(".writeLock<-: " + this);
            }
        }
    }
    
    @Override
    public void writeUnlock() {
        try {
            if (SocketOrChannelConnectionImpl.dprintWriteLocks && this.orb.transportDebugFlag) {
                this.dprint(".writeUnlock->: " + this);
            }
            synchronized (this.writeEvent) {
                this.writeLocked = false;
                this.writeEvent.notify();
            }
        }
        finally {
            if (SocketOrChannelConnectionImpl.dprintWriteLocks && this.orb.transportDebugFlag) {
                this.dprint(".writeUnlock<-: " + this);
            }
        }
    }
    
    @Override
    public void sendWithoutLock(final OutputObject outputObject) {
        try {
            ((CDROutputObject)outputObject).writeTo(this);
        }
        catch (final IOException ex) {
            final COMM_FAILURE writeErrorSend = this.wrapper.writeErrorSend(ex);
            this.purgeCalls(writeErrorSend, false, true);
            throw writeErrorSend;
        }
    }
    
    @Override
    public void registerWaiter(final MessageMediator messageMediator) {
        this.responseWaitingRoom.registerWaiter(messageMediator);
    }
    
    @Override
    public void unregisterWaiter(final MessageMediator messageMediator) {
        this.responseWaitingRoom.unregisterWaiter(messageMediator);
    }
    
    @Override
    public InputObject waitForResponse(final MessageMediator messageMediator) {
        return this.responseWaitingRoom.waitForResponse(messageMediator);
    }
    
    @Override
    public void setConnectionCache(final ConnectionCache connectionCache) {
        this.connectionCache = connectionCache;
    }
    
    @Override
    public ConnectionCache getConnectionCache() {
        return this.connectionCache;
    }
    
    @Override
    public void setUseSelectThreadToWait(final boolean useSelectThreadToWait) {
        this.useSelectThreadToWait = useSelectThreadToWait;
        this.setReadGiopHeaderOnly(this.shouldUseSelectThreadToWait());
    }
    
    @Override
    public void handleEvent() {
        if (this.orb.transportDebugFlag) {
            this.dprint(".handleEvent->: " + this);
        }
        this.getSelectionKey().interestOps(this.getSelectionKey().interestOps() & ~this.getInterestOps());
        if (this.shouldUseWorkerThreadForEvent()) {
            Throwable t = null;
            try {
                int threadPoolToUse = 0;
                if (this.shouldReadGiopHeaderOnly()) {
                    this.partialMessageMediator = this.readBits();
                    threadPoolToUse = this.partialMessageMediator.getThreadPoolToUse();
                }
                if (this.orb.transportDebugFlag) {
                    this.dprint(".handleEvent: addWork to pool: " + threadPoolToUse);
                }
                this.orb.getThreadPoolManager().getThreadPool(threadPoolToUse).getWorkQueue(0).addWork(this.getWork());
            }
            catch (final NoSuchThreadPoolException ex) {
                t = ex;
            }
            catch (final NoSuchWorkQueueException ex2) {
                t = ex2;
            }
            if (t != null) {
                if (this.orb.transportDebugFlag) {
                    this.dprint(".handleEvent: " + t);
                }
                final INTERNAL internal = new INTERNAL("NoSuchThreadPoolException");
                internal.initCause(t);
                throw internal;
            }
        }
        else {
            if (this.orb.transportDebugFlag) {
                this.dprint(".handleEvent: doWork");
            }
            this.getWork().doWork();
        }
        if (this.orb.transportDebugFlag) {
            this.dprint(".handleEvent<-: " + this);
        }
    }
    
    @Override
    public SelectableChannel getChannel() {
        return this.socketChannel;
    }
    
    @Override
    public int getInterestOps() {
        return 1;
    }
    
    @Override
    public Connection getConnection() {
        return this;
    }
    
    @Override
    public String getName() {
        return this.toString();
    }
    
    @Override
    public void doWork() {
        try {
            if (this.orb.transportDebugFlag) {
                this.dprint(".doWork->: " + this);
            }
            if (!this.shouldReadGiopHeaderOnly()) {
                this.read();
            }
            else {
                final CorbaMessageMediator finishReadingBits = this.finishReadingBits(this.getPartialMessageMediator());
                if (finishReadingBits != null) {
                    this.dispatch(finishReadingBits);
                }
            }
        }
        catch (final Throwable t) {
            if (this.orb.transportDebugFlag) {
                this.dprint(".doWork: ignoring Throwable: " + t + " " + this);
            }
        }
        finally {
            if (this.orb.transportDebugFlag) {
                this.dprint(".doWork<-: " + this);
            }
        }
    }
    
    @Override
    public void setEnqueueTime(final long enqueueTime) {
        this.enqueueTime = enqueueTime;
    }
    
    @Override
    public long getEnqueueTime() {
        return this.enqueueTime;
    }
    
    @Override
    public boolean shouldReadGiopHeaderOnly() {
        return this.shouldReadGiopHeaderOnly;
    }
    
    protected void setReadGiopHeaderOnly(final boolean shouldReadGiopHeaderOnly) {
        this.shouldReadGiopHeaderOnly = shouldReadGiopHeaderOnly;
    }
    
    @Override
    public ResponseWaitingRoom getResponseWaitingRoom() {
        return this.responseWaitingRoom;
    }
    
    @Override
    public void serverRequestMapPut(final int n, final CorbaMessageMediator corbaMessageMediator) {
        this.serverRequestMap.put(new Integer(n), corbaMessageMediator);
    }
    
    @Override
    public CorbaMessageMediator serverRequestMapGet(final int n) {
        return this.serverRequestMap.get(new Integer(n));
    }
    
    @Override
    public void serverRequestMapRemove(final int n) {
        this.serverRequestMap.remove(new Integer(n));
    }
    
    @Override
    public Socket getSocket() {
        return this.socket;
    }
    
    @Override
    public synchronized void serverRequestProcessingBegins() {
        ++this.serverRequestCount;
    }
    
    @Override
    public synchronized void serverRequestProcessingEnds() {
        --this.serverRequestCount;
    }
    
    @Override
    public synchronized int getNextRequestId() {
        return this.requestId++;
    }
    
    @Override
    public ORB getBroker() {
        return this.orb;
    }
    
    @Override
    public CodeSetComponentInfo.CodeSetContext getCodeSetContext() {
        if (this.codeSetContext == null) {
            synchronized (this) {
                return this.codeSetContext;
            }
        }
        return this.codeSetContext;
    }
    
    @Override
    public synchronized void setCodeSetContext(final CodeSetComponentInfo.CodeSetContext codeSetContext) {
        if (this.codeSetContext == null) {
            if (OSFCodeSetRegistry.lookupEntry(codeSetContext.getCharCodeSet()) == null || OSFCodeSetRegistry.lookupEntry(codeSetContext.getWCharCodeSet()) == null) {
                throw this.wrapper.badCodesetsFromClient();
            }
            this.codeSetContext = codeSetContext;
        }
    }
    
    @Override
    public MessageMediator clientRequestMapGet(final int n) {
        return this.responseWaitingRoom.getMessageMediator(n);
    }
    
    @Override
    public void clientReply_1_1_Put(final MessageMediator clientReply_1_1) {
        this.clientReply_1_1 = clientReply_1_1;
    }
    
    @Override
    public MessageMediator clientReply_1_1_Get() {
        return this.clientReply_1_1;
    }
    
    @Override
    public void clientReply_1_1_Remove() {
        this.clientReply_1_1 = null;
    }
    
    @Override
    public void serverRequest_1_1_Put(final MessageMediator serverRequest_1_1) {
        this.serverRequest_1_1 = serverRequest_1_1;
    }
    
    @Override
    public MessageMediator serverRequest_1_1_Get() {
        return this.serverRequest_1_1;
    }
    
    @Override
    public void serverRequest_1_1_Remove() {
        this.serverRequest_1_1 = null;
    }
    
    protected String getStateString(final int n) {
        synchronized (this.stateEvent) {
            switch (n) {
                case 1: {
                    return "OPENING";
                }
                case 2: {
                    return "ESTABLISHED";
                }
                case 3: {
                    return "CLOSE_SENT";
                }
                case 4: {
                    return "CLOSE_RECVD";
                }
                case 5: {
                    return "ABORT";
                }
                default: {
                    return "???";
                }
            }
        }
    }
    
    @Override
    public synchronized boolean isPostInitialContexts() {
        return this.postInitialContexts;
    }
    
    @Override
    public synchronized void setPostInitialContexts() {
        this.postInitialContexts = true;
    }
    
    @Override
    public void purgeCalls(final SystemException ex, final boolean b, final boolean b2) {
        final int minor = ex.minor;
        try {
            if (this.orb.transportDebugFlag) {
                this.dprint(".purgeCalls->: " + minor + "/" + b + "/" + b2 + " " + this);
            }
            synchronized (this.stateEvent) {
                if (this.state == 5 || this.state == 4) {
                    if (this.orb.transportDebugFlag) {
                        this.dprint(".purgeCalls: exiting since state is: " + this.getStateString(this.state) + " " + this);
                    }
                    return;
                }
            }
            try {
                if (!b2) {
                    this.writeLock();
                }
            }
            catch (final SystemException ex2) {
                if (this.orb.transportDebugFlag) {
                    this.dprint(".purgeCalls: SystemException" + ex2 + "; continuing " + this);
                }
            }
            synchronized (this.stateEvent) {
                if (minor == 1398079697) {
                    this.state = 4;
                    ex.completed = CompletionStatus.COMPLETED_NO;
                }
                else {
                    this.state = 5;
                    ex.completed = CompletionStatus.COMPLETED_MAYBE;
                }
                this.stateEvent.notifyAll();
            }
            try {
                this.socket.getInputStream().close();
                this.socket.getOutputStream().close();
                this.socket.close();
            }
            catch (final Exception ex3) {
                if (this.orb.transportDebugFlag) {
                    this.dprint(".purgeCalls: Exception closing socket: " + ex3 + " " + this);
                }
            }
            this.responseWaitingRoom.signalExceptionToAllWaiters(ex);
        }
        finally {
            if (this.contactInfo != null) {
                ((OutboundConnectionCache)this.getConnectionCache()).remove(this.contactInfo);
            }
            else if (this.acceptor != null) {
                ((InboundConnectionCache)this.getConnectionCache()).remove(this);
            }
            this.writeUnlock();
            if (this.orb.transportDebugFlag) {
                this.dprint(".purgeCalls<-: " + minor + "/" + b + "/" + b2 + " " + this);
            }
        }
    }
    
    @Override
    public void sendCloseConnection(final GIOPVersion giopVersion) throws IOException {
        this.sendHelper(giopVersion, MessageBase.createCloseConnection(giopVersion));
    }
    
    @Override
    public void sendMessageError(final GIOPVersion giopVersion) throws IOException {
        this.sendHelper(giopVersion, MessageBase.createMessageError(giopVersion));
    }
    
    @Override
    public void sendCancelRequest(final GIOPVersion giopVersion, final int n) throws IOException {
        this.sendHelper(giopVersion, MessageBase.createCancelRequest(giopVersion, n));
    }
    
    protected void sendHelper(final GIOPVersion giopVersion, final Message message) throws IOException {
        final CDROutputObject cdrOutputObject = OutputStreamFactory.newCDROutputObject(this.orb, null, giopVersion, this, message, (byte)1);
        message.write(cdrOutputObject);
        cdrOutputObject.writeTo(this);
    }
    
    @Override
    public void sendCancelRequestWithLock(final GIOPVersion giopVersion, final int n) throws IOException {
        this.writeLock();
        try {
            this.sendCancelRequest(giopVersion, n);
        }
        finally {
            this.writeUnlock();
        }
    }
    
    @Override
    public final void setCodeBaseIOR(final IOR codeBaseServerIOR) {
        this.codeBaseServerIOR = codeBaseServerIOR;
    }
    
    @Override
    public final IOR getCodeBaseIOR() {
        return this.codeBaseServerIOR;
    }
    
    @Override
    public final CodeBase getCodeBase() {
        return this.cachedCodeBase;
    }
    
    protected void setReadTimeouts(final ReadTimeouts readTimeouts) {
        this.readTimeouts = readTimeouts;
    }
    
    protected void setPartialMessageMediator(final CorbaMessageMediator partialMessageMediator) {
        this.partialMessageMediator = partialMessageMediator;
    }
    
    protected CorbaMessageMediator getPartialMessageMediator() {
        return this.partialMessageMediator;
    }
    
    @Override
    public String toString() {
        synchronized (this.stateEvent) {
            return "SocketOrChannelConnectionImpl[ " + ((this.socketChannel == null) ? this.socket.toString() : this.socketChannel.toString()) + " " + this.getStateString(this.state) + " " + this.shouldUseSelectThreadToWait() + " " + this.shouldUseWorkerThreadForEvent() + " " + this.shouldReadGiopHeaderOnly() + "]";
        }
    }
    
    @Override
    public void dprint(final String s) {
        ORBUtility.dprint("SocketOrChannelConnectionImpl", s);
    }
    
    protected void dprint(final String s, final Throwable t) {
        this.dprint(s);
        t.printStackTrace(System.out);
    }
    
    static {
        SocketOrChannelConnectionImpl.dprintWriteLocks = false;
    }
}
